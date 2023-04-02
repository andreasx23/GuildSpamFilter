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
            description = "General broadcast filters"
    )
    final String filterSectionGeneral = "General Filters";

    @ConfigSection(
            position = 1,
            closedByDefault = true,
            name = "Collection Log Filters",
            description = "Collection Log broadcast filters"
    )
    final String filterSectionCollectionLog = "Collection Log Filters";

    @ConfigSection(
            position = 2,
            closedByDefault = true,
            name = "Skilling Filters",
            description = "Skilling broadcast filters"
    )
    final String filterSectionSkilling = "Skilling Filters";

    @ConfigSection(
            position = 3,
            closedByDefault = true,
            name = "PvM Filters",
            description = "PvM broadcast filters"
    )
    final String filterSectionPvm = "PvM Filters";

    @ConfigSection(
            position = 4,
            closedByDefault = true,
            name = "PvP Filters",
            description = "PvP broadcast filters"
    )
    final String filterSectionPvp = "PvP Filters";

    @ConfigSection(
            position = 5,
            closedByDefault = true,
            name = "Miscellaneous",
            description = "Miscellaneous"
    )
    final String miscellaneous = "Miscellaneous";

    // General
    @ConfigItem(
            keyName = "filterPb",
            name = "Filter Personal Bests",
            description = "Removes Personal Bests from clan broadcast",
            section = filterSectionGeneral,
            position = 0
    )
    default boolean filterPb()
    {
        return false;
    }

    @ConfigItem(
            keyName = "pbToIncludeOrExcludeEnum",
            name = "PB Mode",
            description = "Set which personal best mode. Include includes all PB's except the ones matching your comma separated list. Exclude does the reverse. (Default: Exclude all except)",
            section = filterSectionGeneral,
            position = 1
    )
    default PersonalBestEnum pbToIncludeOrExcludeEnum()
    {
        return PersonalBestEnum.EXCLUDE_ALL_EXCEPT;
    }

    @ConfigItem(
            keyName = "pbsToIncludeOrExclude",
            name = "PB's to include or exclude based on PB Mode",
            description = "Include/Exclude all except: Using comma separation e.g. 'Chambers of Xeric, Theatre of Blood' would include/exclude these PB's from Clan Broadcast (case insensitive)",
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
            description = "Removes Pet Drops from clan broadcast",
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
            description = "Removes New Clan Members from clan broadcast",
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
            description = "Removes Kicked Clan Members from clan broadcast",
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
            description = "Removes Members Who Leave from clan broadcast",
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
            name = "Filter Quest Complete",
            description = "Removes Quest Completions from clan broadcast",
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
            description = "Removes Achievement Diaries from clan broadcast",
            section = filterSectionGeneral,
            position = 8
    )
    default boolean filterAchievementDiaries()
    {
        return false;
    }


    @ConfigItem(
            keyName = "achievementDiariesThreshold",
            name = "Achievement Diaries Threshold",
            description = "Set minimum Achievement Diary threshold to filter broadcasts (Default: All)",
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
            description = "Removes Combat Diaries from clan broadcast",
            section = filterSectionGeneral,
            position = 10
    )
    default boolean filterCombatDiaries()
    {
        return false;
    }


    @ConfigItem(
            keyName = "combatDiariesThreshold",
            name = "Combat Diaries Threshold",
            description = "Set minimum Combat Diary threshold to filter broadcasts (Default: All)",
            section = filterSectionGeneral,
            position = 11
    )
    default CombatDiariesEnum combatDiariesThreshold()
    {
        return CombatDiariesEnum.ALL;
    }

    @ConfigItem(
            keyName = "filterHardcoreDeath",
            name = "Filter Hardcore Death",
            description = "Removes Hardcore Deaths from clan broadcast",
            section = filterSectionGeneral,
            position = 12
    )
    default boolean filterHardcoreDeath()
    {
        return false;
    }

    @ConfigItem(
            keyName = "filterCombatLevelUpMessage",
            name = "Filter Combat Level Up",
            description = "Removes combat level ups from clan broadcast",
            section = filterSectionGeneral,
            position = 13
    )
    default boolean filterCombatLevelUpMessage()
    {
        return false;
    }

    @ConfigItem(
            keyName = "filterCCombatLevelUpThreshold",
            name = "Combat Level Up Threshold",
            description = "Set minimum Combat Level Up threshold to filter broadcasts (Default: 127)",
            section = filterSectionGeneral,
            position = 14
    )
    default int filterCCombatLevelUpThreshold()
    {
        return 127;
    }

    @ConfigItem(
            keyName = "filterDefaultMessage",
            name = "Filter Default Message",
            description = "Removes the default message when logging in from clan broadcast",
            section = filterSectionGeneral,
            position = 15
    )
    default boolean filterDefaultMessage()
    {
        return false;
    }
    @ConfigItem(
            keyName = "customFilters",
            name = "Custom Filters",
            description = "Using comma separation e.g. 'Chambers of Xeric, Theatre of Blood' if anything matches these search terms it would be removed from Clan Broadcast (case insensitive)",
            section = filterSectionGeneral,
            position = 16
    )
    default String customFilters()
    {
        return "";
    }

    // Collection Log
    @ConfigItem(
            keyName = "filterCollectionLogBosses",
            name = "Filter Collection Log Boss Drops",
            description = "Removes Collection Log Boss Drops from clan broadcast",
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
            description = "Removes Collection Log Raid Drops from clan broadcast",
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
            description = "Removes Collection Log Clue Drops from clan broadcast",
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
            description = "Removes Collection Log Minigame Drops from clan broadcast",
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
            description = "Removes Collection Log Other Drops from clan broadcast",
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
            description = "Enables collection log threshold",
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
            description = "Set minimum Collection Log gathered threshold to filter broadcasts (Default: 1444)",
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
            description = "Removes Personal Bests from clan broadcast",
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
            description = "Set minimum Raid loot GP threshold to filter broadcasts (Default: 2,147,483,647)",
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
            description = "Removes Regular Drops from clan broadcast",
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
            description = "Set minimum loot GP threshold to filter broadcasts (Default: 2,147,483,647)",
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
            description = "Removes Rare Drops from clan broadcast",
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
            description = "Removes Total Level Milestones from clan broadcast",
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
            description = "Set minimum Total Level threshold to filter broadcasts (Default: 2278)",
            section = filterSectionSkilling,
            position = 1
    )
    default int totalLevelThreshold()
    {
        return 2278;
    }

    @ConfigItem(
            keyName = "filterLevelUp",
            name = "Filter Level Ups",
            description = "Removes Level Ups from clan broadcast",
            section = filterSectionSkilling,
            position = 2
    )
    default boolean filterLevelUp()
    {
        return false;
    }

    @ConfigItem(
            keyName = "filterMaxTotal",
            name = "Filter Max Total Message",
            description = "Removes Max Total message from clan broadcast",
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
            description = "Set minimum Level threshold to filter broadcasts (Default: 100)",
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
            description = "Removes XP Milestones from clan broadcast",
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
            description = "Set minimum XP Milestone threshold to filter broadcasts (Default: 2,147,483,647)",
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
            name = "Filter Player Death",
            description = "Removes Players who got PK'ed from clan broadcast",
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
            description = "Set minimum GP threshold to filter broadcasts (Default: 2,147,483,647)",
            section = filterSectionPvp,
            position = 1
    )
    default int playerDiedThreshold()
    {
        return Integer.MAX_VALUE;
    }

    @ConfigItem(
            keyName = "filterPlayerKill",
            name = "Filter Player Kill",
            description = "Removes Players who get a PK from clan broadcast",
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
            description = "Set minimum GP threshold to filter broadcasts (Default: 2,147,483,647)",
            section = filterSectionPvp,
            position = 3
    )
    default int playerKillThreshold()
    {
        return Integer.MAX_VALUE;
    }

    // Miscellaneous
    @ConfigItem(
            keyName = "excludeSelf",
            name = "Always allow self",
            description = "Always allow own player broadcasts to be shown in Clan",
            section = miscellaneous,
            position = 0
    )
    default boolean excludeSelf()
    {
        return false;
    }
}
