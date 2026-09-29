package com.GuildSpamFilter;

import org.junit.Test;

import static org.mockito.Mockito.when;

public class MiscellaneousFiltersTest extends FilterTestBase
{
    private static final String BICEPS_DROP = "Biceps Btw received a drop: Abyssal whip (1,500,000 coins).";
    private static final String STORE_DROP = "Store Biceps received a drop: Abyssal whip (1,500,000 coins).";

    // Player names to always include

    @Test
    public void alwaysIncludedPlayersAreNeverHidden()
    {
        when(config.filterRegularDrops()).thenReturn(true);
        setAlwaysIncludedPlayers("Biceps Btw");

        assertShown(BICEPS_DROP);
        assertHidden(STORE_DROP);
    }

    @Test
    public void alwaysIncludedPlayersAcceptSeveralNamesInAnyCase()
    {
        when(config.filterRegularDrops()).thenReturn(true);
        setAlwaysIncludedPlayers("biceps btw, STORE BICEPS");

        assertShown(BICEPS_DROP);
        assertShown(STORE_DROP);
    }

    @Test
    public void alwaysIncludedPlayersMatchNamesWithNonBreakingSpaces()
    {
        when(config.filterRegularDrops()).thenReturn(true);
        setAlwaysIncludedPlayers("Biceps Btw");

        // The game writes spaces in player names as non-breaking spaces
        assertShown("Biceps Btw received a drop: Abyssal whip (1,500,000 coins).");
    }

    @Test
    public void alwaysIncludedPlayersMatchBroadcastsWithAnIconInFront()
    {
        when(config.filterRegularDrops()).thenReturn(true);
        setAlwaysIncludedPlayers("Biceps Btw");

        assertShown("<img=41>Biceps Btw received a drop: Abyssal whip (1,500,000 coins).");
    }

    @Test
    public void alwaysIncludedPlayerDoesNotCoverLongerNames()
    {
        when(config.filterRegularDrops()).thenReturn(true);
        setAlwaysIncludedPlayers("Bob");

        assertHidden("Bobby received a drop: Abyssal whip (1,500,000 coins).");
    }

    // Custom filters

    @Test
    public void customFiltersHideBroadcastsContainingAnyEntry()
    {
        setCustomFilters("tanzanite, Vorkath");

        assertHidden("Biceps Btw received a drop: Tanzanite fang (4,000,000 coins).");
        assertHidden("Biceps Btw has achieved a new Vorkath personal best: 1:05.40");
        assertShown(BICEPS_DROP);
    }

    @Test
    public void customFiltersIgnoreCase()
    {
        setCustomFilters("ABYSSAL WHIP");

        assertHidden(BICEPS_DROP);
    }

    @Test
    public void alwaysIncludedPlayersWinOverCustomFilters()
    {
        setAlwaysIncludedPlayers("Biceps Btw");
        setCustomFilters("whip");

        assertShown(BICEPS_DROP);
        assertHidden(STORE_DROP);
    }

    // Leagues

    @Test
    public void hidesLeaguesBroadcasts()
    {
        when(config.filterLeaguesBroadcasts()).thenReturn(true);

        assertHidden("<img=22>Biceps Btw has unlocked the Tier 3 relic.");
        assertShown(BICEPS_DROP);
    }
}
