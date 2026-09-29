package com.GuildSpamFilter;

import net.runelite.api.GameState;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.function.BooleanSupplier;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CollectionLogFiltersTest extends FilterTestBase
{
    private static String collectionLog(String item, int slots)
    {
        return "Biceps Btw received a new collection log item: " + item + " (" + slots + "/1666)";
    }

    @Test
    public void readsTheCollectionLogOnceTheGameHasLoaded() throws Exception
    {
        when(config.filterCollectionLogBosses()).thenReturn(true);
        when(client.getGameState()).thenReturn(GameState.STARTING);
        clearInvocations(clientThread);

        plugin.startUp();

        ArgumentCaptor<BooleanSupplier> loadCollectionLog = ArgumentCaptor.forClass(BooleanSupplier.class);
        verify(clientThread).invoke(loadCollectionLog.capture());
        assertFalse("Should try again while the game is loading", loadCollectionLog.getValue().getAsBoolean());

        when(client.getGameState()).thenReturn(GameState.LOGIN_SCREEN);
        assertTrue(loadCollectionLog.getValue().getAsBoolean());
        assertHidden(collectionLog("Abyssal whip", 812));
    }

    @Test
    public void eachCategoryFilterHidesItemsFromThatCategory()
    {
        when(config.filterCollectionLogBosses()).thenReturn(true);
        assertHidden(collectionLog("Abyssal whip", 812));

        when(config.filterCollectionLogRaids()).thenReturn(true);
        assertHidden(collectionLog("Twisted bow", 812));

        when(config.filterCollectionLogClues()).thenReturn(true);
        assertHidden(collectionLog("Mole slippers", 812));

        when(config.filterCollectionLogMinigames()).thenReturn(true);
        assertHidden(collectionLog("Fighter hat", 812));

        when(config.filterCollectionLogOther()).thenReturn(true);
        assertHidden(collectionLog("Golden tench", 812));
    }

    @Test
    public void leavesItemsFromOtherCategoriesAlone()
    {
        when(config.filterCollectionLogRaids()).thenReturn(true);

        assertHidden(collectionLog("Twisted bow", 812));
        assertShown(collectionLog("Abyssal whip", 812));
        assertShown(collectionLog("Fighter hat", 812));
    }

    @Test
    public void handlesItemNamesWithBrackets()
    {
        when(config.filterCollectionLogClues()).thenReturn(true);

        assertHidden(collectionLog("Blue skirt (g)", 812));
    }

    @Test
    public void matchesItemNamesRegardlessOfCapitalization()
    {
        when(config.filterCollectionLogClues()).thenReturn(true);

        assertHidden(collectionLog("3rd age amulet", 812));
        assertHidden(collectionLog("3rd Age amulet", 812));
    }

    @Test
    public void slotThresholdHidesPlayersWithFewerSlots()
    {
        when(config.enableCollectionLogThreshold()).thenReturn(true);
        when(config.filterCollectionLogThreshold()).thenReturn(1000);

        assertHidden(collectionLog("Abyssal whip", 812));
        assertShown(collectionLog("Abyssal whip", 1200));
    }

    @Test
    public void slotThresholdStillAppliesCategoryFiltersAboveIt()
    {
        when(config.enableCollectionLogThreshold()).thenReturn(true);
        when(config.filterCollectionLogThreshold()).thenReturn(1000);
        when(config.filterCollectionLogBosses()).thenReturn(true);

        assertHidden(collectionLog("Abyssal whip", 1200));
        assertShown(collectionLog("Fighter hat", 1200));
    }
}
