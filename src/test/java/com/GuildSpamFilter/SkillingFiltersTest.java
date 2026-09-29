package com.GuildSpamFilter;

import org.junit.Test;

import static org.mockito.Mockito.when;

public class SkillingFiltersTest extends FilterTestBase
{
    // Level-up wording not yet checked against a real in-game broadcast
    private static final String FISHING_70 = "Biceps Btw has reached Fishing level 70.";
    private static final String FISHING_90 = "Biceps Btw has reached Fishing level 90.";

    private static final String MAX_TOTAL = "Biceps Btw has reached the highest possible total level of 2376.";

    // Level-ups

    @Test
    public void hidesEveryLevelUpWithTheDefaultThreshold()
    {
        when(config.filterLevelUp()).thenReturn(true);

        assertHidden(FISHING_70);
        assertHidden(FISHING_90);
    }

    @Test
    public void levelUpThresholdKeepsThatLevelAndHigher()
    {
        when(config.filterLevelUp()).thenReturn(true);
        when(config.levelThreshold()).thenReturn(80);

        assertHidden(FISHING_70);
        assertShown(FISHING_90);
    }

    @Test
    public void levelUpFilterLeavesTotalAndCombatLevelsAlone()
    {
        when(config.filterLevelUp()).thenReturn(true);

        assertShown("Biceps Btw has reached a total level of 2000.");
        assertShown("Biceps Btw has reached combat level 100.");
    }

    // XP milestones

    @Test
    public void hidesEveryXpMilestoneWithTheDefaultThreshold()
    {
        when(config.filterXpMilestone()).thenReturn(true);

        assertHidden("Biceps Btw has reached 200,000,000 XP in Fishing.");
    }

    @Test
    public void xpMilestoneThresholdKeepsThatAmountAndMore()
    {
        when(config.filterXpMilestone()).thenReturn(true);
        when(config.xpMilestoneThreshold()).thenReturn(100_000_000);

        assertHidden("Biceps Btw has reached 50,000,000 XP in Fishing.");
        assertShown("Biceps Btw has reached 100,000,000 XP in Fishing.");
        assertShown("Biceps Btw has reached 200,000,000 XP in Fishing.");
    }

    // Total level

    @Test
    public void totalLevelThresholdKeepsThatLevelAndHigher()
    {
        when(config.filterTotalLevelMilestone()).thenReturn(true);
        when(config.totalLevelThreshold()).thenReturn(2000);

        assertHidden("Biceps Btw has reached a total level of 1900.");
        assertShown("Biceps Btw has reached a total level of 2000.");
    }

    @Test
    public void readsTotalLevelsWithAThousandsSeparator()
    {
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
