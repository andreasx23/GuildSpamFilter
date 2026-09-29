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
- `./gradlew run` starts RuneLite with the plugin (via `GuildSpamFilterTest.main`, a launcher, not a unit test) in
  `--developer-mode --debug`. Debug logging shows every broadcast as `Checking broadcast: ...`, which is the way to
  capture real broadcast wording.
- `build.gradle` compiles with `options.release.set(11)` like the Plugin Hub, so Java 12+ APIs fail locally too. The
  `java { sourceCompatibility }` block only tells IntelliJ which language level to use.
- Gradle wrapper 8.10 and Lombok 1.18.30 are required on JDK 21+ (Gradle 7.4 cannot run on JDK 22, and Lombok
  older than 1.18.30 fails with `NoSuchFieldError ... JCImport qualid`).

## Architecture

All logic lives in `GuildSpamFilterPlugin`:

- **Hook:** `onScriptCallbackEvent` handles RuneLite's `chatFilterCheck` event, fired by the game's chat script for
  every message, including all old messages when `client.refreshChat()` runs. Int stack: `[size-3]` = show flag
  (set to `0` to hide), `[size-2]` = message type, `[size-1]` = message id. Object stack `[size-1]` = message text.
- **Pipeline:** `shouldFilterMessage` strips a leading `<img=N>` and anything up to the first `|`, checks the
  always-included players first, then ORs the `filterX` methods; the first match hides the message. The Leagues
  filter looks at the raw message for `<img=22>`.
- **Parsing:** `indexOf`/`substring`, not regex. Numbers are read with `readNumber`, which ignores commas, "coins"
  and punctuation and returns -1 instead of throwing. The rule is: if a filter is on and the broadcast can't be
  parsed, hide it. As a safety net, `onScriptCallbackEvent` catches any exception from the filters, shows that
  broadcast and logs one warning quoting it.
- **Threads:** filtering runs on the client thread. `onConfigChanged` is called on the Swing thread, so it does its
  list updates inside `clientThread.invoke`, together with the chat refresh.
- **Always-included players** (`isBroadcastMessageForPlayer`): the game writes spaces in names as non-breaking spaces,
  so spaces in the configured name act as wildcards, and the name must end at a space character ("Bob" must not
  match "Bobby").
- **Settings:** config group `GuildSpamFilterConfig.GROUP` (`"GuildSpamFilter"`). The comma-separated lists
  (personal bests, custom filters, always-included players) are cached in `HashSet`s and only reloaded in
  `onConfigChanged`, which matches on the settings' `keyName`s and ignores other plugins' groups. `startUp`,
  `shutDown` and `onConfigChanged` all call `client.refreshChat()` on the client thread, so existing chat is
  refiltered.
- **Saved names never change:** users' settings are saved under the config group, each `@ConfigItem`'s `keyName`,
  and enum constant names (`ALL`, `ELITE`, `EXCLUDE_ALL_EXCEPT`). Renaming any of them silently resets that setting
  for every user. Java method and class names can be renamed freely, which is why some `keyName`s no longer match
  their methods (e.g. `alwaysIncludedPlayerNames()` is saved as `excludedPlayerNames`).
- **Tier thresholds:** `AchievementDiaryTier` / `CombatAchievementTier` are the dropdown options (`ALL` hides every
  tier). Easy is deliberately not offered as a threshold; broadcast tiers are resolved by `getAchievementDiaryTierId`
  / `getCombatAchievementTierId`, which treat Easy as id 0 and unknown words as -1. Don't use `Enum.valueOf` on
  broadcast text.
- **Raid loot:** `raidItemIds` maps every item on the collection log's Raids tab to its item id (built in
  `loadCollectionLog`). The value comes from `ItemManager.getItemPrice` (GE price), looked up when the broadcast
  arrives, not from the coin value in the broadcast. Don't cache prices at load time: that runs at the login screen,
  possibly before RuneLite has fetched prices. Items not on the Raids tab are never filtered. Since RuneLite 1.13.0,
  `getItemPrice` returns `long`.
- **Collection log:** `CollectionLogHandler.readData(client)` reads the game cache, the same data the in-game
  collection log uses: enum `2102` → tab structs (param `682` name, `683` enum of pages) → page structs (param `689`
  name, `690` enum of item ids) → `client.getItemDefinition(id).getName()`. RuneLite has no named constants for these
  ids; they match the `collection-log` (evansloan) and `kill-clog` Hub plugins. Cache reads need the client thread
  and a loaded game, so `startUp` schedules `loadCollectionLog` with `clientThread.invoke(BooleanSupplier)`; returning
  `false` retries every tick until `GameState` reaches `LOGIN_SCREEN`. In the real client it logs
  `Loaded 1721 collection log items in 5 tabs, including 67 raid items` (September 2026).
  `CollectionLogTab.lowercaseItemNames` is lowercase, and `filterCollectionLogByTab` compares lowercase item names
  and switches on the tab name constants in `CollectionLogTab` (`BOSSES`, `RAIDS`, `CLUES`, `MINIGAMES`, `OTHER`),
  which must match the game's tab names exactly. Always use the constants; test fakes deliberately spell the names
  out.

## Tests

JUnit 4 + Mockito 5 (test-only). Test classes mirror the config sections (`GeneralFiltersTest`,
`CollectionLogFiltersTest`, `SkillingFiltersTest`, `PvmFiltersTest`, `PvpFiltersTest`, `MiscellaneousFiltersTest`),
plus `MessageHandlingTest` and `CollectionLogHandlerTest`.

- `FilterTestBase` injects mocks into the real plugin with Guice and calls `startUp()`. The config mock uses
  `CALLS_REAL_METHODS`, so every setting has its real default until stubbed. Both `clientThread.invoke` overloads
  run immediately, and `FakeCollectionLog` installs a small collection log using the real cache ids.
- Because the plugin turns filter crashes into warnings, `FilterTestBase` fails any test that logs an unexpected
  warning. A test that expects one calls `assertWarned(text)`.
- `isHidden(message)` simulates the `chatFilterCheck` stacks. For the list settings use `setCustomFilters`,
  `setAlwaysIncludedPlayers` or `setPersonalBestList`: they fire `ConfigChanged`, because stubbing the getter alone
  doesn't update the cached sets.

## Broadcast wording

Filters depend on Jagex's exact wording, which changes occasionally (see git history).

- **Seen in in-game screenshots** (RuneLite's screenshot plugin saves them under
  `~/.runelite/screenshots/<player>/`, chat box included):
  `Biceps Btw received a drop: Viggora's chainmace (u) (3,950,787 coins).`,
  `Biceps Btw received a new collection log item: Viggora's chainmace (u) (1114/1717)` (no thousands separator in
  the slot count, no full stop), and
  `Biceps Btw has unlocked the Grandmaster tier of rewards from Combat Achievements!`
- **Also checked by the `better-clan-broadcasts` plugin:** `received special loot from a raid:` (currently ends
  with `(N coins)`, which it hasn't always), `has completed a quest:`, `personal best:`, `has defeated` /
  `has been defeated by`, `has a funny feeling like`, `has been invited into the clan by`,
  `has completed the <Tier> <Area> diary.`, `has completed a(n) <Tier> combat task`,
  `To talk in your clan's channel, start each line of chat with // or /c.`
- **Not verified against a real broadcast:** skill level-ups (tests use `has reached Fishing level 90.`; the parser
  also accepts `level of 90`), hardcore deaths, kicked members, rare drops, the PvP `(N coins)` format, and
  whether total levels contain a thousands separator (the parser accepts both).

## Plugin Hub and releases

- `runelite-plugin.properties` has `build=standard`: the Hub replaces `build.gradle` and `settings.gradle` with its
  own template (RuneLite client, Lombok 1.18.30, JetBrains annotations, `--release 11`) and only packages the `main`
  source set. Tests and test dependencies are never built there, and runtime dependencies beyond RuneLite's can't
  be added without a Hub dependency-verification PR. The Hub also rejects classes newer than Java 11, classes in the
  `net.runelite` package, and the APIs in `disallowed-apis.txt` in the `runelite/plugin-hub-tooling` repo.
- The Hub rebuilds every plugin for each RuneLite release. If this plugin fails to compile, it silently stops being
  served for that client version. To diagnose, check `~/.runelite/logs/client.log` for a missing
  `Loading external plugin "guild-spam-filter"` line, or look for `guild-spam-filter` in the Hub's public list,
  `https://repo.runelite.net/plugins/manifest/<RuneLite version>_full.js` (a 4-byte signature length and the
  signature, then JSON). Then build against `latest.release`.
- The version players see comes from `version=` in `runelite-plugin.properties`. With `build=standard` the Hub
  ignores `build.gradle`'s version, and without the property it shows the first 8 characters of the commit hash.
- **Release steps:**
  1. Bump `version` in both `runelite-plugin.properties` and `build.gradle`.
  2. Merge into `master` through a pull request (`master` has a branch protection rule requiring PRs).
  3. Update the Plugin Hub through the fork `andreasx23/plugin-hub`. Keep the fork's `master` identical to
     `runelite/plugin-hub`'s: RuneLite squash-merges Hub PRs, so commits made on the fork's `master` never reach
     upstream and pile up in every later PR (it once reached 74 commits for a one-line change). For each release:
     - Sync the fork's `master` (GitHub's **Sync fork**), then create a branch from it, e.g.
       `guild-spam-filter-1.9.4`.
     - In that branch, change only `commit=` in `plugins/guild-spam-filter` to the full 40-character hash on this
       repo's `master`. Copy it after merging, since squash and rebase merges change the hash.
     - Push the branch and open the PR from it into `runelite/plugin-hub:master`. It should show 1 commit and
       1 changed file.
     - The Hub's CI builds the plugin against the current RuneLite release. Check that it passes, then delete the
       branch once RuneLite merges the PR.

## Conventions

- Allman braces, 4-space indentation, standard Java naming (camelCase methods and fields, no `_` prefixes, no
  `Enum` suffix on types).
- Logging uses SLF4J `{}` placeholders, never string concatenation. `info` is for once-per-session events (start,
  stop, collection log loaded) and `warn` for problems that silently disable a filter. Per-broadcast logging is
  `debug`: each broadcast is logged once as `Checking broadcast: …`, and a filter that hides it logs one
  `Hiding … (<setting name as shown in the panel>: <value>)` line with the reason. A broadcast with no `Hiding` line
  was shown, so don't add "detected" lines.
- Spell names out instead of abbreviating (`personalBestMode`, not `pbMode`; `alwaysIncludedPlayerNames`, not
  `alwaysIncludedPlayerIgns`). The exceptions are the game terms the settings panel itself uses: XP, GP, PvM and PvP.
- `.idea/` is tracked. IntelliJ rewrites `.idea/compiler.xml` and `.idea/misc.xml` (Lombok processor path, Java
  language level) when it reloads the Gradle project; commit those alongside the change that caused them.
- Git runs with `core.autocrlf`, so "LF will be replaced by CRLF" warnings are expected.
