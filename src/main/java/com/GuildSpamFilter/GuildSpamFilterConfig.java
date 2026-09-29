package com.GuildSpamFilter;

import com.GuildSpamFilter.Configs.AchievementDiaryTier;
import com.GuildSpamFilter.Configs.CombatAchievementTier;
import com.GuildSpamFilter.Configs.PersonalBestMode;
import net.runelite.client.config.*;

// The group and every keyName are what users' settings are saved under, so they must never change,
// even when a method is renamed. That's why some keyNames don't match their method names.
@ConfigGroup(GuildSpamFilterConfig.GROUP)
public interface GuildSpamFilterConfig extends Config
{
    String GROUP = "GuildSpamFilter";

    @ConfigSection(
            position = 0,
            closedByDefault = false,
            name = "General Filters",
            description = "Filter common clan broadcasts including personal bests, pets, quests, achievement diaries, Combat Achievements and clan member activity"
    )
    final String filterSectionGeneral = "General Filters";

    @ConfigSection(
            position = 1,
            closedByDefault = true,
            name = "Collection Log Filters",
            description = "Filter new collection log item broadcasts by collection log tab, or by how many slots the player has"
    )
    final String filterSectionCollectionLog = "Collection Log Filters";

    @ConfigSection(
            position = 2,
            closedByDefault = true,
            name = "Skilling Filters",
            description = "Filter skill-related broadcasts including level ups, XP milestones, and total level achievements"
    )
    final String filterSectionSkilling = "Skilling Filters";

    @ConfigSection(
            position = 3,
            closedByDefault = true,
            name = "PvM Filters",
            description = "Filter PvM broadcasts including raid loot, drops and rare drops"
    )
    final String filterSectionPvm = "PvM Filters";

    @ConfigSection(
            position = 4,
            closedByDefault = true,
            name = "PvP Filters",
            description = "Filter PvP broadcasts for player kills and deaths"
    )
    final String filterSectionPvp = "PvP Filters";

    @ConfigSection(
            position = 5,
            closedByDefault = true,
            name = "Miscellaneous",
            description = "Players whose broadcasts are always shown, and Leagues broadcasts"
    )
    final String miscellaneous = "Miscellaneous";

    // General
    @ConfigItem(
            keyName = "filterPb",
            name = "Filter Personal Bests",
            description = "Remove personal best broadcasts from clan chat",
            section = filterSectionGeneral,
            position = 0
    )
    default boolean filterPersonalBests()
    {
        return false;
    }

    @ConfigItem(
            keyName = "pbToIncludeOrExcludeEnum",
            name = "Personal Best Mode",
            description = "Include all except removes only the personal bests in your list. Exclude all except removes every personal best except the ones in your list. (Default: Exclude all except)",
            section = filterSectionGeneral,
            position = 1
    )
    default PersonalBestMode personalBestMode()
    {
        return PersonalBestMode.EXCLUDE_ALL_EXCEPT;
    }

    @ConfigItem(
            keyName = "pbsToIncludeOrExclude",
            name = "Personal Bests to Include or Exclude",
            description = "Comma-separated list of personal bests to include or exclude based on the selected mode (e.g. Chambers of Xeric, theatre of blood). Case insensitive.",
            section = filterSectionGeneral,
            position = 2
    )
    default String personalBestsToIncludeOrExclude()
    {
        return "";
    }

    @ConfigItem(
            keyName = "filterPets",
            name = "Filter Pet Drops",
            description = "Remove pet drop broadcasts from clan chat",
            section = filterSectionGeneral,
            position = 3
    )
    default boolean filterPets()
    {
        return false;
    }

    @ConfigItem(
            keyName = "filterNewClanMember",
            name = "Filter New Clan Members",
            description = "Remove new clan member join broadcasts from clan chat",
            section = filterSectionGeneral,
            position = 4
    )
    default boolean filterNewClanMember()
    {
        return false;
    }

    @ConfigItem(
            keyName = "filterClanMemberKicked",
            name = "Filter Kicked Clan Members",
            description = "Remove clan member kick broadcasts from clan chat",
            section = filterSectionGeneral,
            position = 5
    )
    default boolean filterClanMemberKicked()
    {
        return false;
    }

    @ConfigItem(
            keyName = "filterMemberLeftClan",
            name = "Filter Members Who Leave",
            description = "Remove member leave broadcasts from clan chat",
            section = filterSectionGeneral,
            position = 6,
            hidden = true
    )
    default boolean filterMemberLeftClan()
    {
        return false;
    }

    @ConfigItem(
            keyName = "filterQuestComplete",
            name = "Filter Quest Completions",
            description = "Remove quest completion broadcasts from clan chat",
            section = filterSectionGeneral,
            position = 7
    )
    default boolean filterQuestComplete()
    {
        return false;
    }

    @ConfigItem(
            keyName = "filterAchievementDiaries",
            name = "Filter Achievement Diaries",
            description = "Remove achievement diary completion broadcasts from clan chat",
            section = filterSectionGeneral,
            position = 8
    )
    default boolean filterAchievementDiaries()
    {
        return false;
    }

    @ConfigItem(
            keyName = "achievementDiariesThreshold",
            name = "Achievement Diary Threshold",
            description = "Achievement diaries easier than this tier are removed. All removes every diary. (Default: All)",
            section = filterSectionGeneral,
            position = 9
    )
    default AchievementDiaryTier achievementDiariesThreshold()
    {
        return AchievementDiaryTier.ALL;
    }

    @ConfigItem(
            keyName = "filterCombatDiaries",
            name = "Filter Combat Achievement Tiers",
            description = "Remove broadcasts for unlocking a Combat Achievement reward tier from clan chat",
            section = filterSectionGeneral,
            position = 10
    )
    default boolean filterCombatAchievementTiers()
    {
        return false;
    }

    @ConfigItem(
            keyName = "filterCombatDiaryTasks",
            name = "Filter Combat Achievement Tasks",
            description = "Remove Combat Achievement task completion broadcasts from clan chat",
            section = filterSectionGeneral,
            position = 11
    )
    default boolean filterCombatAchievementTasks()
    {
        return false;
    }

    @ConfigItem(
            keyName = "combatDiariesThreshold",
            name = "Combat Achievement Threshold",
            description = "Combat Achievement tiers and tasks easier than this tier are removed. All removes every one. (Default: All)",
            section = filterSectionGeneral,
            position = 12
    )
    default CombatAchievementTier combatAchievementThreshold()
    {
        return CombatAchievementTier.ALL;
    }

    @ConfigItem(
            keyName = "filterHardcoreDeath",
            name = "Filter Hardcore Deaths",
            description = "Remove hardcore ironman death broadcasts from clan chat",
            section = filterSectionGeneral,
            position = 13
    )
    default boolean filterHardcoreDeath()
    {
        return false;
    }

    @ConfigItem(
            keyName = "filterCombatLevelUpMessage",
            name = "Filter Combat Level Ups",
            description = "Remove combat level up broadcasts from clan chat",
            section = filterSectionGeneral,
            position = 14
    )
    default boolean filterCombatLevelUps()
    {
        return false;
    }

    @ConfigItem(
            keyName = "filterCombatLevelUpThreshold",
            name = "Combat Level Up Threshold",
            description = "Combat level ups below this level are removed. The default removes all of them, including max combat. (Default: 127)",
            section = filterSectionGeneral,
            position = 15
    )
    default int combatLevelUpThreshold()
    {
        return 127;
    }

    @ConfigItem(
            keyName = "filterDefaultMessage",
            name = "Filter Clan Login Message",
            description = "Remove the \"To talk in your clan's channel, start each line of chat with // or /c.\" message shown when you log in",
            section = filterSectionGeneral,
            position = 16
    )
    default boolean filterDefaultMessage()
    {
        return false;
    }

    @ConfigItem(
            keyName = "customFilters",
            name = "Custom Filters",
            description = "Comma-separated list of custom terms to filter from broadcasts (e.g. Chambers of Xeric, theatre of blood). Any broadcast containing these terms will be removed. Case insensitive.",
            section = filterSectionGeneral,
            position = 17
    )
    default String customFilters()
    {
        return "";
    }

    // Collection Log
    @ConfigItem(
            keyName = "filterCollectionLogBosses",
            name = "Filter Bosses Tab",
            description = "Remove new collection log item broadcasts for items on the Bosses tab",
            section = filterSectionCollectionLog,
            position = 0
    )
    default boolean filterCollectionLogBosses()
    {
        return false;
    }

    @ConfigItem(
            keyName = "filterCollectionLogRaids",
            name = "Filter Raids Tab",
            description = "Remove new collection log item broadcasts for items on the Raids tab",
            section = filterSectionCollectionLog,
            position = 1
    )
    default boolean filterCollectionLogRaids()
    {
        return false;
    }

    @ConfigItem(
            keyName = "filterCollectionLogClues",
            name = "Filter Clues Tab",
            description = "Remove new collection log item broadcasts for items on the Clues tab",
            section = filterSectionCollectionLog,
            position = 2
    )
    default boolean filterCollectionLogClues()
    {
        return false;
    }

    @ConfigItem(
            keyName = "filterCollectionLogMinigames",
            name = "Filter Minigames Tab",
            description = "Remove new collection log item broadcasts for items on the Minigames tab",
            section = filterSectionCollectionLog,
            position = 3
    )
    default boolean filterCollectionLogMinigames()
    {
        return false;
    }

    @ConfigItem(
            keyName = "filterCollectionLogOther",
            name = "Filter Other Tab",
            description = "Remove new collection log item broadcasts for items on the Other tab",
            section = filterSectionCollectionLog,
            position = 4
    )
    default boolean filterCollectionLogOther()
    {
        return false;
    }

    @ConfigItem(
            keyName = "enableCollectionLogThreshold",
            name = "Enable Collection Log Threshold",
            description = "Remove new collection log item broadcasts from players with fewer slots than the Collection Log Threshold",
            section = filterSectionCollectionLog,
            position = 5
    )
    default boolean enableCollectionLogThreshold()
    {
        return false;
    }

    @ConfigItem(
            keyName = "filterCollectionLogThreshold",
            name = "Collection Log Threshold",
            description = "When the threshold is enabled, broadcasts from players with fewer collection log slots than this are removed. (Default: 1444)",
            section = filterSectionCollectionLog,
            position = 6
    )
    default int collectionLogThreshold()
    {
        return 1444;
    }

    // PvM
    @ConfigItem(
            keyName = "filterRaidDrop",
            name = "Filter Raid Drops",
            description = "Remove raid drop broadcasts from clan chat",
            section = filterSectionPvm,
            position = 0
    )
    default boolean filterRaidDrop()
    {
        return false;
    }

    @ConfigItem(
            keyName = "raidLootGpThreshold",
            name = "Raid Loot GP Threshold",
            description = "Raid loot worth less than this, based on its Grand Exchange price, is removed. The default removes all of it. (Default: 2,147,483,647)",
            section = filterSectionPvm,
            position = 1
    )
    default int raidLootGpThreshold()
    {
        return Integer.MAX_VALUE;
    }

    @ConfigItem(
            keyName = "filterRegularDrops",
            name = "Filter Regular Drops",
            description = "Remove regular drop broadcasts from clan chat",
            section = filterSectionPvm,
            position = 2
    )
    default boolean filterRegularDrops()
    {
        return false;
    }

    @ConfigItem(
            keyName = "lootGpThreshold",
            name = "Loot GP Threshold",
            description = "Drops worth less than this are removed. The default removes all of them. (Default: 2,147,483,647)",
            section = filterSectionPvm,
            position = 3
    )
    default int lootGpThreshold()
    {
        return Integer.MAX_VALUE;
    }

    @ConfigItem(
            keyName = "filterRareDrops",
            name = "Filter Rare Drops",
            description = "Remove rare drop broadcasts from clan chat",
            section = filterSectionPvm,
            position = 4
    )
    default boolean filterRareDrops()
    {
        return false;
    }

    // Skilling
    @ConfigItem(
            keyName = "filterTotalLevelMilestone",
            name = "Filter Total Level Milestones",
            description = "Remove total level milestone broadcasts from clan chat",
            section = filterSectionSkilling,
            position = 0
    )
    default boolean filterTotalLevelMilestone()
    {
        return false;
    }

    @ConfigItem(
            keyName = "totalLevelThreshold",
            name = "Total Level Threshold",
            description = "Total level milestones below this are removed. (Default: 2376)",
            section = filterSectionSkilling,
            position = 1
    )
    default int totalLevelThreshold()
    {
        return 2376;
    }

    @ConfigItem(
            keyName = "filterLevelUp",
            name = "Filter Level Ups",
            description = "Remove skill level up broadcasts from clan chat",
            section = filterSectionSkilling,
            position = 3
    )
    default boolean filterLevelUp()
    {
        return false;
    }

    @ConfigItem(
            keyName = "filterMaxTotal",
            name = "Filter Max Total Level",
            description = "Remove broadcasts for reaching the highest possible total level from clan chat",
            section = filterSectionSkilling,
            position = 2
    )
    default boolean filterMaxTotal()
    {
        return false;
    }

    @ConfigItem(
            keyName = "levelThreshold",
            name = "Level Threshold",
            description = "Level ups below this level are removed. The default removes all of them. (Default: 100)",
            section = filterSectionSkilling,
            position = 4
    )
    default int levelThreshold()
    {
        return 100;
    }

    @ConfigItem(
            keyName = "filterXpMilestone",
            name = "Filter XP Milestones",
            description = "Remove skill experience milestone broadcasts from clan chat",
            section = filterSectionSkilling,
            position = 5
    )
    default boolean filterXpMilestone()
    {
        return false;
    }

    @ConfigItem(
            keyName = "xpMilestoneThreshold",
            name = "XP Milestone Threshold",
            description = "XP milestones below this amount of XP are removed. The default removes all of them. (Default: 2,147,483,647)",
            section = filterSectionSkilling,
            position = 6
    )
    default int xpMilestoneThreshold()
    {
        return Integer.MAX_VALUE;
    }

    // PvP
    @ConfigItem(
            keyName = "filterPlayerDied",
            name = "Filter Player Deaths",
            description = "Remove player death broadcasts from clan chat",
            section = filterSectionPvp,
            position = 0
    )
    default boolean filterPlayerDied()
    {
        return false;
    }

    @ConfigItem(
            keyName = "playerDiedThreshold",
            name = "Player Death Threshold",
            description = "Deaths that lost less than this much loot are removed. The default removes all of them. (Default: 2,147,483,647)",
            section = filterSectionPvp,
            position = 1
    )
    default int playerDiedThreshold()
    {
        return Integer.MAX_VALUE;
    }

    @ConfigItem(
            keyName = "filterPlayerKill",
            name = "Filter Player Kills",
            description = "Remove player kill broadcasts from clan chat",
            section = filterSectionPvp,
            position = 2
    )
    default boolean filterPlayerKill()
    {
        return false;
    }

    @ConfigItem(
            keyName = "playerKillThreshold",
            name = "Player Kill Threshold",
            description = "Kills that gained less than this much loot are removed. The default removes all of them. (Default: 2,147,483,647)",
            section = filterSectionPvp,
            position = 3
    )
    default int playerKillThreshold()
    {
        return Integer.MAX_VALUE;
    }

    // Miscellaneous
    @ConfigItem(
            keyName = "excludedPlayerNames",
            name = "Player Names to Always Include",
            description = "Comma-separated list of player names whose broadcasts will always be shown regardless of other filter settings (e.g. Biceps Btw, store biceps). Case insensitive.",
            section = miscellaneous,
            position = 0,
            hidden = false
    )
    default String alwaysIncludedPlayerNames()
    {
        return "";
    }

    @ConfigItem(
            keyName = "filterLeaguesBroadcasts",
            name = "Filter Leagues Broadcasts",
            description = "Remove Leagues game mode broadcasts from clan chat",
            section = miscellaneous,
            position = 1,
            hidden = false
    )
    default boolean filterLeaguesBroadcasts()
    {
        return false;
    }
}