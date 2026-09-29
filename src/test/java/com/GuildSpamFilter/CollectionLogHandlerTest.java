package com.GuildSpamFilter;

import com.GuildSpamFilter.Handlers.CollectionLogHandler;
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
    private ArrayList<CollectionLogTab> tabs;

    @Before
    public void readCollectionLog()
    {
        Client client = mock(Client.class);
        new FakeCollectionLog()
                .tab("Bosses")
                .page("Abyssal Sire", "Abyssal orphan", "Abyssal whip")
                .page("Kraken", "Pet kraken", "Trident of the seas (full)")
                .tab("Raids").page("Chambers of Xeric", "Olmlet", "Twisted bow")
                .tab("Clues").page("Hard Treasure Trails", "3rd age amulet")
                .tab("Minigames").page("Barbarian Assault", "Fighter hat")
                .tab("Other")
                .page("Aerial Fishing", "Golden tench")
                .page("All Pets", "Pet kraken", "Olmlet")
                .installOn(client);

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
    public void readsEachPageWithItsItemNames()
    {
        List<CollectionLogPage> bossPages = tab("Bosses").pages;

        assertEquals(2, bossPages.size());
        assertEquals("Abyssal Sire", bossPages.get(0).name);
        assertEquals(Arrays.asList("Abyssal orphan", "Abyssal whip"), bossPages.get(0).itemNames);
        assertEquals("Kraken", bossPages.get(1).name);
        assertEquals(Arrays.asList("Pet kraken", "Trident of the seas (full)"), bossPages.get(1).itemNames);
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
