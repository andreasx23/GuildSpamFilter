package com.GuildSpamFilter;

import org.junit.Test;

import static org.mockito.Mockito.when;

public class PvpFiltersTest extends FilterTestBase
{
    private static final String BIG_KILL = "Biceps Btw has defeated Store Biceps and received (1,234,567 coins) worth of loot!";
    private static final String SMALL_KILL = "Biceps Btw has defeated Store Biceps and received (56,000 coins) worth of loot!";

    private static final String BIG_DEATH = "Biceps Btw has been defeated by Store Biceps and lost (1,234,567 coins) worth of loot.";
    private static final String SMALL_DEATH = "Biceps Btw has been defeated by Store Biceps and lost (56,000 coins) worth of loot.";

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
        when(config.playerKillThreshold()).thenReturn(1_000_000);

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
    public void killFilterLeavesDeathsAlone()
    {
        when(config.filterPlayerKill()).thenReturn(true);

        assertShown(BIG_DEATH);
    }
}
