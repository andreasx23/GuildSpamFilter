# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

"Clan Spam Filter", a RuneLite Plugin Hub plugin (Hub id `guild-spam-filter`) that hides clan broadcasts
(`ChatMessageType.CLAN_MESSAGE`) the player doesn't care about. Messages typed by clanmates (`CLAN_CHAT`) are never
touched. Every filter is off by default, and threshold settings hide only broadcasts *below* the threshold.

## Commands

```
./gradlew build                                                        # compile + all tests
./gradlew test --tests "com.GuildSpamFilter.GeneralFiltersTest"         # one test class
./gradlew test --tests "com.GuildSpamFilter.GeneralFiltersTest.hidesEasyCombatTasks"   # one test
```

- On Windows from PowerShell use `.\gradlew.bat`. There is no linter or formatter.
- To run the plugin in a real client, run `main` in `src/test/java/com/GuildSpamFilter/GuildSpamFilterTest.java`
  from IntelliJ (it is a launcher, not a unit test). RuneLite's example plugin passes `--developer-mode --debug`;
  with `--debug` the plugin's `log.debug` output appears, including every broadcast as `Broadcast message: ...`,
  which is the way to capture real broadcast wording.
- Gradle wrapper 8.10 and Lombok 1.18.30 are required on JDK 21+ (Gradle 7.4 cannot run on JDK 22, and Lombok
  older than 1.18.30 fails with `NoSuchFieldError ... JCImport qualid`).

## Architecture

All logic lives in `GuildSpamFilterPlugin`:

- **Hook:** `onScriptCallbackEvent` handles RuneLite's `chatFilterCheck` event, fired by the game's chat script for
  every message, including all old messages when `client.refreshChat()` runs. Int stack: `[size-3]` = show flag
  (set to `0` to hide), `[size-2]` = message type, `[size-1]` = message id. Object stack `[size-1]` = message text.
- **Pipeline:** `shouldFilterMessage` strips a leading `<img=N>` and anything up to the first `|`, checks the
  always-show list first, then ORs the `filterX` methods; the first match hides the message. The Leagues filter
  looks at the raw message for `<img=22>`.
- **Parsing:** `indexOf`/`substring`, not regex. The existing rule is: if a filter is on and the broadcast can't be
  parsed, hide it.
- **Always-show players** (`isBroadcastMessageForPlayer`): the game writes spaces in names as non-breaking spaces,
  so spaces in the configured name act as wildcards, and the name must end at a space character ("Bob" must not
  match "Bobby").
- **Settings:** config group `GuildSpamFilterConfig.GROUP` (`"GuildSpamFilter"`). The comma-separated lists
  (`pbsToIncludeOrExclude`, `customFilters`, `excludedPlayerNames`) are cached in `HashSet`s and only reloaded by
  key in `onConfigChanged`. `onConfigChanged` ignores other plugins' groups, and it, `startUp` and `shutDown` call
  `clientThread.invoke(client::refreshChat)` so existing chat is refiltered. `excludedPlayerNames` is the
  *always-show* list despite its name; config key names can't be changed without losing users' saved settings.
- **Tier thresholds:** `AchievementDiariesEnum` / `CombatDiariesEnum` are the dropdown options (`ALL` hides every
  tier). `EASY` is deliberately left out so it isn't offered as a threshold; broadcast tiers are resolved by
  `getAchievementDiaryTierId` / `getCombatDiaryTierId`, which treat Easy as id 0 and unknown words as -1. Don't use
  `Enum.valueOf` on broadcast text.
- **Raid loot:** the value comes from `ItemManager.getItemPrice` (GE price) for the hardcoded item ids in
  `AddCoxRaidItems` / `AddTobRaidItems` / `AddToaRaidItems`, looked up on the first `chatFilterCheck`, not from the
  coin value in the broadcast. Since RuneLite 1.13.0, `getItemPrice` returns `long`.
- **Collection log:** `CollectionLogHandler.ReadData(client)` reads the game cache, the same data the in-game
  collection log uses: enum `2102` → tab structs (param `682` name, `683` enum of pages) → page structs (param `689`
  name, `690` enum of item ids) → `client.getItemDefinition(id).getName()`. RuneLite has no named constants for these
  ids; they match the `collection-log` (evansloan) and `kill-clog` Hub plugins. Cache reads need the client thread
  and a loaded game, so `startUp` schedules `LoadCollectionLog` with `clientThread.invoke(BooleanSupplier)`; returning
  `false` retries every tick until `GameState` reaches `LOGIN_SCREEN`. It logs
  `Loaded N collection log items in 5 categories` (N ≈ 1,700). `Categori.allItems` is lowercase, and
  `filterCollectionLogByCategory` compares lowercase item names and switches on the tab names `Bosses`, `Raids`,
  `Clues`, `Minigames`, `Other`.

## Tests

JUnit 4 + Mockito 5 (test-only). Test classes mirror the config sections (`GeneralFiltersTest`,
`CollectionLogFiltersTest`, `SkillingFiltersTest`, `PvmFiltersTest`, `PvpFiltersTest`, `MiscellaneousFiltersTest`),
plus `MessageHandlingTest` and `CollectionLogHandlerTest`.

- `FilterTestBase` injects mocks into the real plugin with Guice and calls `startUp()`. The config mock uses
  `CALLS_REAL_METHODS`, so every setting has its real default until stubbed. `clientThread.invoke(BooleanSupplier)`
  runs immediately, and `FakeCollectionLog` installs a small collection log using the real cache ids.
- `isHidden(message)` simulates the `chatFilterCheck` stacks. For the list settings use `setCustomFilters`,
  `setAlwaysShownPlayers` or `setPersonalBestList`: they fire `ConfigChanged`, because stubbing the getter alone
  doesn't update the cached sets.
- Exceptions propagate in tests. In the real client the EventBus logs them and the message stays shown, so a crash
  in a filter looks like "not filtered" to players.

## Broadcast wording

Filters depend on Jagex's exact wording, which changes occasionally (see git history).

- **Confirmed** (also checked by the `better-clan-broadcasts` plugin): `received a drop:`,
  `received special loot from a raid:` (now ends with `(N coins)`), `has completed a quest:`,
  `received a new collection log item:`, `personal best:`, `has defeated` / `has been defeated by`,
  `has a funny feeling like`, `has been invited into the clan by`, `has completed the <Tier> <Area> diary.`,
  `tier of rewards from Combat Achievements!`, `has completed a(n) <Tier> combat task`,
  `To talk in your clan's channel, start each line of chat with // or /c.`
- **Not verified against a real broadcast:** skill level-ups (tests use `has reached Fishing level 90.`; the parser
  also accepts `level of 90`), the words before `the <Tier> tier of rewards`, hardcore deaths, kicked members, rare
  drops, the PvP `(N coins)` format, and whether collection log counts of 1,000+ contain commas.

## Plugin Hub and releases

- `runelite-plugin.properties` has `build=standard`: the Hub replaces `build.gradle` and `settings.gradle` with its
  own template (RuneLite client, Lombok 1.18.30, JetBrains annotations, `--release 11`) and only packages the `main`
  source set. Tests and test dependencies are never built there, and runtime dependencies beyond RuneLite's can't
  be added without a Hub dependency-verification PR.
- The Hub rebuilds every plugin for each RuneLite release. If this plugin fails to compile, it silently stops being
  served for that client version. To diagnose, check `~/.runelite/logs/client.log` for a missing
  `Loading external plugin "guild-spam-filter"` line, then build against `latest.release`.
- To check Hub compatibility locally, compile on JDK 11 (`JAVA_HOME` pointed at a JDK 11) with
  `./gradlew clean compileJava`.
- **Release steps:**
  1. Bump `version` in `build.gradle`.
  2. Merge into `master` through a pull request (`master` has a branch protection rule requiring PRs).
  3. Open a PR to `runelite/plugin-hub` updating `commit=` in `plugins/guild-spam-filter` to the full 40-character
     hash on `master`. Squash or rebase merges change the hash.

## Conventions

- Allman braces, 4-space indentation, PascalCase private helpers (`UpdateCustomFilters`, `LoadCollectionLog`),
  `log.debug` with string concatenation. `Categori` is the existing model name.
- `.idea/` is tracked. IntelliJ rewrites `.idea/compiler.xml` (the Lombok processor path) after Gradle dependency
  changes; commit that alongside the change that caused it.
- Git runs with `core.autocrlf`, so "LF will be replaced by CRLF" warnings are expected.
