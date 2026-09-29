package com.GuildSpamFilter;

import com.GuildSpamFilter.Handlers.CollectionLogHandler;
import com.GuildSpamFilter.Models.CollectionLogItem;
import com.GuildSpamFilter.Models.CollectionLogPage;
import com.GuildSpamFilter.Models.CollectionLogTab;
import net.runelite.api.Client;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class CollectionLogHandlerTest
{
    private FakeCollectionLog collectionLog;
    private ArrayList<CollectionLogTab> tabs;

    @Before
    public void readCollectionLog()
    {
        Client client = mock(Client.class);
        collectionLog = new FakeCollectionLog()
                .tab("Bosses")
                .page("Abyssal Sire", "Abyssal orphan", "Abyssal whip")
                .page("Kraken", "Pet kraken", "Trident of the seas (full)")
                .tab("Raids").page("Chambers of Xeric", "Olmlet", "Twisted bow")
                .tab("Clues").page("Hard Treasure Trails", "3rd age amulet")
                .tab("Minigames").page("Barbarian Assault", "Fighter hat")
                .tab("Other")
                .page("Aerial Fishing", "Golden tench")
                .page("All Pets", "Pet kraken", "Olmlet");
        collectionLog.installOn(client);

        tabs = new CollectionLogHandler().readData(client);
    }

    @Test
    public void readsTheTabsInGameOrder()
    {
        List<String> names = new ArrayList<>();
        for (CollectionLogTab tab : tabs)
        {
            names.add(tab.name);
        }

        assertEquals(Arrays.asList("Bosses", "Raids", "Clues", "Minigames", "Other"), names);
    }

    @Test
    public void readsEachPageWithItsItems()
    {
        List<CollectionLogPage> bossPages = tab("Bosses").pages;

        assertEquals(2, bossPages.size());
        assertEquals("Abyssal Sire", bossPages.get(0).name);
        assertEquals(Arrays.asList("Abyssal orphan", "Abyssal whip"), itemNames(bossPages.get(0)));
        assertEquals("Kraken", bossPages.get(1).name);
        assertEquals(Arrays.asList("Pet kraken", "Trident of the seas (full)"), itemNames(bossPages.get(1)));
    }

    @Test
    public void keepsEachItemsIdFromTheGame()
    {
        CollectionLogItem whip = tab("Bosses").pages.get(0).items.get(1);

        assertEquals("Abyssal whip", whip.name);
        assertEquals(collectionLog.itemId("Abyssal whip"), whip.id);
    }

    @Test
    public void collectsEveryItemInATabInLowercase()
    {
        CollectionLogTab bosses = tab("Bosses");

        assertEquals(4, bosses.lowercaseItemNames.size());
        assertTrue(bosses.lowercaseItemNames.contains("abyssal whip"));
        assertTrue(bosses.lowercaseItemNames.contains("trident of the seas (full)"));
        assertFalse(bosses.lowercaseItemNames.contains("Abyssal whip"));
    }

    @Test
    public void keepsItemsInEveryTabTheyAppearIn()
    {
        assertTrue(tab("Bosses").lowercaseItemNames.contains("pet kraken"));
        assertTrue(tab("Other").lowercaseItemNames.contains("pet kraken"));
        assertTrue(tab("Raids").lowercaseItemNames.contains("olmlet"));
        assertTrue(tab("Other").lowercaseItemNames.contains("olmlet"));
    }

    private static List<String> itemNames(CollectionLogPage page)
    {
        List<String> names = new ArrayList<>();
        for (CollectionLogItem item : page.items)
        {
            names.add(item.name);
        }

        return names;
    }

    private CollectionLogTab tab(String name)
    {
        for (CollectionLogTab tab : tabs)
        {
            if (tab.name.equals(name))
            {
                return tab;
            }
        }

        throw new AssertionError("No collection log tab named " + name);
    }
}
