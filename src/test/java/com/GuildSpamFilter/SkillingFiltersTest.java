package com.GuildSpamFilter;

import org.junit.Test;

import static org.mockito.Mockito.when;

public class SkillingFiltersTest extends FilterTestBase
{
    // Real broadcasts, seen in in-game screenshots from 2024 to 2026
    private static final String DEFENCE_80 = "Shadow IR has reached Defence level 80.";
    private static final String FISHING_90 = "Iron Deleted has reached Fishing level 90.";
    private static final String AGILITY_99 = "Csurma has reached Agility level 99.";
    private static final String TOTAL_2000 = "Radmer has reached a total level of 2000.";
    private static final String TOTAL_2125 = "PapPresseren has reached a total level of 2125.";
    private static final String MAX_TOTAL = "Biceps Btw has reached the highest possible total level of 2376!";
    private static final String XP_15M = "Izuria has reached 15,000,000 XP in Mining.";
    private static final String XP_95M = "Snorhax has reached 95,000,000 XP in Firemaking.";

    // Level-ups

    @Test
    public void hidesEveryLevelUpWithTheDefaultThreshold()
    {
        when(config.filterLevelUp()).thenReturn(true);

        assertHidden(DEFENCE_80);
        assertHidden(FISHING_90);
        assertHidden(AGILITY_99);
    }

    @Test
    public void levelUpThresholdKeepsThatLevelAndHigher()
    {
        when(config.filterLevelUp()).thenReturn(true);
        when(config.levelThreshold()).thenReturn(90);

        assertHidden(DEFENCE_80);
        assertShown(FISHING_90);
        assertShown(AGILITY_99);
    }

    @Test
    public void levelUpFilterLeavesTotalAndCombatLevelsAlone()
    {
        when(config.filterLevelUp()).thenReturn(true);
        when(config.levelThreshold()).thenReturn(99);

        assertShown(TOTAL_2000);
        // Total level milestones go as low as 100
        assertShown("Proever Igen has reached a total level of 100.");
        assertShown("snolpiksvin has reached combat level 100.");
        assertShown(MAX_TOTAL);
    }

    // XP milestones

    @Test
    public void hidesEveryXpMilestoneWithTheDefaultThreshold()
    {
        when(config.filterXpMilestone()).thenReturn(true);

        assertHidden(XP_15M);
        assertHidden(XP_95M);
    }

    @Test
    public void xpMilestoneThresholdKeepsThatAmountAndMore()
    {
        when(config.filterXpMilestone()).thenReturn(true);
        when(config.xpMilestoneThreshold()).thenReturn(50_000_000);

        assertHidden(XP_15M);
        assertShown(XP_95M);
    }

    // Total level

    @Test
    public void totalLevelThresholdKeepsThatLevelAndHigher()
    {
        when(config.filterTotalLevelMilestone()).thenReturn(true);
        when(config.totalLevelThreshold()).thenReturn(2100);

        assertHidden(TOTAL_2000);
        assertShown(TOTAL_2125);
    }

    @Test
    public void readsTotalLevelsWithAThousandsSeparator()
    {
        // Real broadcasts have no thousands separator, but the parser accepts one in case that changes
        when(config.filterTotalLevelMilestone()).thenReturn(true);
        when(config.totalLevelThreshold()).thenReturn(2000);

        assertHidden("Biceps Btw has reached a total level of 1,900.");
        assertShown("Biceps Btw has reached a total level of 2,000.");
    }

    @Test
    public void hidesMaxTotalLevel()
    {
        when(config.filterMaxTotal()).thenReturn(true);

        assertHidden(MAX_TOTAL);
    }

    @Test
    public void totalLevelFilterLeavesMaxTotalAlone()
    {
        when(config.filterTotalLevelMilestone()).thenReturn(true);

        assertShown(MAX_TOTAL);
    }
}
