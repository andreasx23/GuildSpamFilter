package com.GuildSpamFilter;

import org.junit.Test;

import static org.mockito.Mockito.when;

public class PvpFiltersTest extends FilterTestBase
{
    // Real broadcasts, seen in in-game screenshots from 2023 and 2025
    private static final String BIG_KILL = "Crunch704 has defeated dahlinho and received (27,735,720 coins) worth of loot!";
    private static final String SMALL_KILL = "notredame47 has defeated II RAJPUT II and received (6,268,574 coins) worth of loot!";

    // Made up, in the same format as the real SMALL_DEATH
    private static final String BIG_DEATH = "Biceps Btw has been defeated by Store Biceps in The Wilderness and lost (1,234,567 coins) worth of loot.";
    private static final String SMALL_DEATH = "CyborgC7pher has been defeated by tom is bald in The Wilderness and lost (316,298 coins) worth of loot.";
    // A death without loot
    private static final String EMPTY_DEATH = "Latryy has been defeated by Iron TimPvm in The Wilderness. Clearly Latryy struggles with clicking.";

    @Test
    public void hidesEveryPlayerKillWithTheDefaultThreshold()
    {
        when(config.filterPlayerKill()).thenReturn(true);

        assertHidden(BIG_KILL);
        assertHidden(SMALL_KILL);
    }

    @Test
    public void playerKillThresholdKeepsKillsWorthThatMuchOrMore()
    {
        when(config.filterPlayerKill()).thenReturn(true);
        when(config.playerKillThreshold()).thenReturn(10_000_000);

        assertShown(BIG_KILL);
        assertHidden(SMALL_KILL);
    }

    @Test
    public void hidesEveryPlayerDeathWithTheDefaultThreshold()
    {
        when(config.filterPlayerDied()).thenReturn(true);

        assertHidden(BIG_DEATH);
        assertHidden(SMALL_DEATH);
    }

    @Test
    public void playerDeathThresholdKeepsDeathsWorthThatMuchOrMore()
    {
        when(config.filterPlayerDied()).thenReturn(true);
        when(config.playerDiedThreshold()).thenReturn(1_000_000);

        assertShown(BIG_DEATH);
        assertHidden(SMALL_DEATH);
    }

    @Test
    public void hidesPlayerDeathsWithoutLoot()
    {
        when(config.filterPlayerDied()).thenReturn(true);
        when(config.playerDiedThreshold()).thenReturn(1);

        assertHidden(EMPTY_DEATH);
    }

    @Test
    public void killFilterLeavesDeathsAlone()
    {
        when(config.filterPlayerKill()).thenReturn(true);

        assertShown(BIG_DEATH);
        assertShown(EMPTY_DEATH);
    }
}
