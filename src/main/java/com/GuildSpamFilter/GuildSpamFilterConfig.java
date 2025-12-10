package com.GuildSpamFilter;

import com.GuildSpamFilter.Configs.AchievementDiariesEnum;
import com.GuildSpamFilter.Configs.CombatDiariesEnum;
import com.GuildSpamFilter.Configs.PersonalBestEnum;
import net.runelite.client.config.*;

@ConfigGroup("GuildSpamFilter")
public interface GuildSpamFilterConfig extends Config
{
    @ConfigSection(
            position = 0,
            closedByDefault = false,
            name = "General Filters",
            description = "Filter common clan broadcasts including personal bests, pets, quest completions, and member activity"
    )
    final String filterSectionGeneral = "General Filters";

    @ConfigSection(
            position = 1,
            closedByDefault = true,
            name = "Collection Log Filters",
            description = "Filter collection log completion broadcasts from bosses, raids, clues, minigames, and other activities"
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
            description = "Filter PvM broadcasts including raid drops, boss drops, and rare item notifications"
    )
    final String filterSectionPvm = "PvM Filters";

    @ConfigSection(
            position = 4,
            closedByDefault = true,
            name = "PvP Filters",
            description = "Filter PvP broadcasts including player kills, deaths, and loot notifications"
    )
    final String filterSectionPvp = "PvP Filters";

    @ConfigSection(
            position = 5,
            closedByDefault = true,
            name = "Miscellaneous",
            description = "Additional filtering options including player whitelists and miscellaneous broadcasts"
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
    default boolean filterPb()
    {
        return false;
    }

    @ConfigItem(
            keyName = "pbToIncludeOrExcludeEnum",
            name = "Personal Best Mode",
            description = "Set the personal best filtering mode. Include mode hides only PBs matching your list. Exclude mode shows only PBs matching your list. (Default: Exclude all except)",
            section = filterSectionGeneral,
            position = 1
    )
    default PersonalBestEnum pbToIncludeOrExcludeEnum()
    {
        return PersonalBestEnum.EXCLUDE_ALL_EXCEPT;
    }

    @ConfigItem(
            keyName = "pbsToIncludeOrExclude",
            name = "Personal Bests to Include or Exclude",
            description = "Comma-separated list of personal bests to include or exclude based on the selected mode (e.g: Chambers of Xeric, theatre of blood). Case insensitive.",
            section = filterSectionGeneral,
            position = 2
    )
    default String pbsToIncludeOrExclude()
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
            description = "Set the minimum achievement diary difficulty to filter broadcasts (Default: All)",
            section = filterSectionGeneral,
            position = 9
    )
    default AchievementDiariesEnum achievementDiariesThreshold()
    {
        return AchievementDiariesEnum.ALL;
    }

    @ConfigItem(
            keyName = "filterCombatDiaries",
            name = "Filter Combat Diaries",
            description = "Remove combat diary completion broadcasts from clan chat",
            section = filterSectionGeneral,
            position = 10
    )
    default boolean filterCombatDiaries()
    {
        return false;
    }

    @ConfigItem(
            keyName = "filterCombatDiaryTasks",
            name = "Filter Combat Diary Tasks",
            description = "Remove combat diary task completion broadcasts from clan chat",
            section = filterSectionGeneral,
            position = 11
    )
    default boolean filterCombatDiaryTasks()
    {
        return false;
    }

    @ConfigItem(
            keyName = "combatDiariesThreshold",
            name = "Combat Diary Threshold",
            description = "Set the minimum combat diary difficulty to filter broadcasts (Default: All)",
            section = filterSectionGeneral,
            position = 12
    )
    default CombatDiariesEnum combatDiariesThreshold()
    {
        return CombatDiariesEnum.ALL;
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
    default boolean filterCombatLevelUpMessage()
    {
        return false;
    }

    @ConfigItem(
            keyName = "filterCombatLevelUpThreshold",
            name = "Combat Level Up Threshold",
            description = "Set the minimum combat level required to filter broadcasts (Default: 127)",
            section = filterSectionGeneral,
            position = 15
    )
    default int filterCombatLevelUpThreshold()
    {
        return 127;
    }

    @ConfigItem(
            keyName = "filterDefaultMessage",
            name = "Filter Default Login Message",
            description = "Remove default login message from clan chat broadcasts",
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
            description = "Comma-separated list of custom terms to filter from broadcasts (e.g: Chambers of Xeric, theatre of blood). Any message containing these terms will be removed. Case insensitive.",
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
            name = "Filter Collection Log Boss Drops",
            description = "Remove collection log boss drop broadcasts from clan chat",
            section = filterSectionCollectionLog,
            position = 0
    )
    default boolean filterCollectionLogBosses()
    {
        return false;
    }

    @ConfigItem(
            keyName = "filterCollectionLogRaids",
            name = "Filter Collection Log Raid Drops",
            description = "Remove collection log raid drop broadcasts from clan chat",
            section = filterSectionCollectionLog,
            position = 1
    )
    default boolean filterCollectionLogRaids()
    {
        return false;
    }

    @ConfigItem(
            keyName = "filterCollectionLogClues",
            name = "Filter Collection Log Clue Drops",
            description = "Remove collection log clue scroll drop broadcasts from clan chat",
            section = filterSectionCollectionLog,
            position = 2
    )
    default boolean filterCollectionLogClues()
    {
        return false;
    }

    @ConfigItem(
            keyName = "filterCollectionLogMinigames",
            name = "Filter Collection Log Minigame Drops",
            description = "Remove collection log minigame drop broadcasts from clan chat",
            section = filterSectionCollectionLog,
            position = 3
    )
    default boolean filterCollectionLogMinigames()
    {
        return false;
    }

    @ConfigItem(
            keyName = "filterCollectionLogOther",
            name = "Filter Collection Log Other Drops",
            description = "Remove collection log miscellaneous drop broadcasts from clan chat",
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
            description = "Enable filtering based on collection log slot count threshold",
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
            description = "Set the minimum collection log slot count required to filter broadcasts (Default: 1444)",
            section = filterSectionCollectionLog,
            position = 6
    )
    default int filterCollectionLogThreshold()
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
            description = "Set the minimum GP value required for raid loot to be filtered (Default: 2,147,483,647)",
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
            description = "Set the minimum GP value required for loot to be filtered (Default: 2,147,483,647)",
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
            position = 2
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
            description = "Set the minimum total level required to filter milestone broadcasts (Default: 2376)",
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
            position = 2
    )
    default boolean filterLevelUp()
    {
        return false;
    }

    @ConfigItem(
            keyName = "filterMaxTotal",
            name = "Filter Max Total Messages",
            description = "Remove maximum total level achievement broadcasts from clan chat",
            section = filterSectionSkilling,
            position = 3
    )
    default boolean filterMaxTotal()
    {
        return false;
    }

    @ConfigItem(
            keyName = "levelThreshold",
            name = "Level Threshold",
            description = "Set the minimum skill level required to filter level up broadcasts (Default: 100)",
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
            description = "Set the minimum skill experience required to filter milestone broadcasts (Default: 2,147,483,647)",
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
            description = "Set the minimum GP value lost required to filter death broadcasts (Default: 2,147,483,647)",
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
            description = "Set the minimum GP value gained required to filter kill broadcasts (Default: 2,147,483,647)",
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
            description = "Comma-separated list of player names whose broadcasts will always be shown regardless of other filter settings (e.g: Biceps Btw, store biceps). Case insensitive.",
            section = miscellaneous,
            position = 0,
            hidden = false
    )
    default String excludedPlayerNames()
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