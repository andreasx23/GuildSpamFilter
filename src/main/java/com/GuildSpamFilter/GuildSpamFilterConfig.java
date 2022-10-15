package com.GuildSpamFilter;

import net.runelite.client.config.*;

@ConfigGroup("GuildSpamFilter")
public interface GuildSpamFilterConfig extends Config
{
    @ConfigSection(
            position = 0,
            closedByDefault = false,
            name = "Filters",
            description = ""
    )
    final String filterSection = "Filters";

    @ConfigItem(
            keyName = "filterPb",
            name = "Filter Personal Bests",
            description = "Removes Personal Bests from clan broadcast",
            section = filterSection,
            position = 0
    )
    default boolean filterPb()
    {
        return false;
    }

    @ConfigItem(
            keyName = "filterRaidDrop",
            name = "Filter Raid Drops",
            description = "Removes Personal Bests from clan broadcast",
            section = filterSection,
            position = 1
    )
    default boolean filterRaidDrop()
    {
        return false;
    }

    @ConfigItem(
            keyName = "filterRegularDrops",
            name = "Filter Regular Drops",
            description = "Removes Regular Drops from clan broadcast",
            section = filterSection,
            position = 2
    )
    default boolean filterRegularDrops()
    {
        return false;
    }

    @ConfigItem(
            keyName = "filterPets",
            name = "Filter Pet Drops",
            description = "Removes Pet Drops from clan broadcast",
            section = filterSection,
            position = 3
    )
    default boolean filterPets()
    {
        return false;
    }

    @ConfigItem(
            keyName = "filterTotalLevelMilestone",
            name = "Filter Total Level Milestones",
            description = "Removes Total Level Milestones from clan broadcast",
            section = filterSection,
            position = 4
    )
    default boolean filterTotalLevelMilestone()
    {
        return false;
    }

    @ConfigItem(
            keyName = "filterLevelUp",
            name = "Filter Level Ups",
            description = "Removes Level Ups from clan broadcast",
            section = filterSection,
            position = 5
    )
    default boolean filterLevelUp()
    {
        return false;
    }

    @ConfigItem(
            keyName = "filterCollectionLog",
            name = "Filter Collection Log Drops",
            description = "Removes Collection Log Drops from clan broadcast",
            section = filterSection,
            position = 6
    )
    default boolean filterCollectionLog()
    {
        return false;
    }


    @ConfigItem(
            keyName = "filterNewClanMember",
            name = "Filter New Clan Members",
            description = "Removes New Clan Members from clan broadcast",
            section = filterSection,
            position = 7
    )
    default boolean filterNewClanMember()
    {
        return false;
    }
}
