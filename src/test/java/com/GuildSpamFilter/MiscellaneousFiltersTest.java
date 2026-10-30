package com.GuildSpamFilter;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class MiscellaneousFiltersTest extends FilterTestBase
{
    private static final String BICEPS_DROP = "Biceps Btw received a drop: Abyssal whip (1,500,000 coins).";
    private static final String STORE_DROP = "Store Biceps received a drop: Abyssal whip (1,500,000 coins).";
    private static final String TANZANITE_DROP = "Biceps Btw received a drop: Tanzanite fang (4,000,000 coins).";
    private static final String TANZANITE_DROP_WORTH_40M = "Biceps Btw received a drop: Tanzanite fang (40,000,000 coins).";
    private static final String VORKATH_PERSONAL_BEST = "Biceps Btw has achieved a new Vorkath personal best: 1:05.40";

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
    public void alwaysIncludedPlayersAcceptAnySeparator()
    {
        when(config.filterRegularDrops()).thenReturn(true);

        for (String list : listsOf("Biceps Btw", "Store Biceps"))
        {
            setAlwaysIncludedPlayers(list);
            assertFalse("Always-included players " + describeList(list), isHidden(BICEPS_DROP) || isHidden(STORE_DROP));
        }
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
    public void customFiltersAcceptAnySeparator()
    {
        for (String list : listsOf("tanzanite", "Vorkath"))
        {
            setCustomFilters(list);
            assertTrue("Custom filters " + describeList(list), isHidden(TANZANITE_DROP) && isHidden(VORKATH_PERSONAL_BEST));
            assertShown(BICEPS_DROP);
        }
    }

    @Test
    public void customFiltersMatchNamesWithNonBreakingSpaces()
    {
        setCustomFilters("Biceps Btw");

        // The game writes spaces in player names as non-breaking spaces
        assertHidden("Biceps Btw received a drop: Abyssal whip (1,500,000 coins).");
    }

    @Test
    public void alwaysIncludedPlayersWinOverCustomFilters()
    {
        setAlwaysIncludedPlayers("Biceps Btw");
        setCustomFilters("whip");

        assertShown(BICEPS_DROP);
        assertHidden(STORE_DROP);
    }

    // Custom regex filters

    @Test
    public void customRegexFiltersHideBroadcastsWithAMatch()
    {
        setCustomRegexFilters("has reached (Fishing|Cooking) level");

        assertHidden("Biceps Btw has reached Fishing level 90.");
        assertHidden("Biceps Btw has reached Cooking level 90.");
        assertShown("Biceps Btw has reached Mining level 90.");
    }

    @Test
    public void customRegexFiltersIgnoreCase()
    {
        setCustomRegexFilters("ABYSSAL WHIP");

        assertHidden(BICEPS_DROP);
    }

    @Test
    public void customRegexFiltersAreOnePerLineSoTheyCanContainCommas()
    {
        // Drops worth 1,000,000 to 9,999,999 coins, and every Vorkath personal best
        setCustomRegexFilters("\\(\\d,\\d{3},\\d{3} coins\\)\r\n\n  Vorkath personal best  \n");

        assertHidden(BICEPS_DROP);
        assertShown(TANZANITE_DROP_WORTH_40M);
        assertHidden(VORKATH_PERSONAL_BEST);
    }

    @Test
    public void customRegexFiltersSkipInvalidExpressions()
    {
        setCustomRegexFilters("(unclosed\nwhip");

        assertWarned("Ignoring custom regex filter \"(unclosed\"");
        assertHidden(BICEPS_DROP);
    }

    @Test
    public void customRegexFiltersMatchNamesWithNonBreakingSpaces()
    {
        setCustomRegexFilters("^Biceps Btw received");

        assertHidden("Biceps Btw received a drop: Abyssal whip (1,500,000 coins).");
    }

    @Test
    public void alwaysIncludedPlayersWinOverCustomRegexFilters()
    {
        setAlwaysIncludedPlayers("Biceps Btw");
        setCustomRegexFilters("whip");

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
