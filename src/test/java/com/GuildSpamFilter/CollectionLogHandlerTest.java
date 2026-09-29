package com.GuildSpamFilter;

import com.GuildSpamFilter.Handlers.CollectionLogHandler;
import com.GuildSpamFilter.Models.Categori;
import com.GuildSpamFilter.Models.Section;
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
    private ArrayList<Categori> categories;

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

        categories = new CollectionLogHandler().ReadData(client);
    }

    @Test
    public void readsTheTabsInGameOrder()
    {
        List<String> names = new ArrayList<>();
        for (Categori categori : categories)
        {
            names.add(categori.name);
        }

        assertEquals(Arrays.asList("Bosses", "Raids", "Clues", "Minigames", "Other"), names);
    }

    @Test
    public void readsEachPageWithItsItemNames()
    {
        List<Section> bossPages = category("Bosses").sections;

        assertEquals(2, bossPages.size());
        assertEquals("Abyssal Sire", bossPages.get(0).name);
        assertEquals(Arrays.asList("Abyssal orphan", "Abyssal whip"), bossPages.get(0).collectionLogs);
        assertEquals("Kraken", bossPages.get(1).name);
        assertEquals(Arrays.asList("Pet kraken", "Trident of the seas (full)"), bossPages.get(1).collectionLogs);
    }

    @Test
    public void collectsEveryItemInATabInLowercase()
    {
        Categori bosses = category("Bosses");

        assertEquals(4, bosses.allItems.size());
        assertTrue(bosses.allItems.contains("abyssal whip"));
        assertTrue(bosses.allItems.contains("trident of the seas (full)"));
        assertFalse(bosses.allItems.contains("Abyssal whip"));
    }

    @Test
    public void keepsItemsInEveryTabTheyAppearIn()
    {
        assertTrue(category("Bosses").allItems.contains("pet kraken"));
        assertTrue(category("Other").allItems.contains("pet kraken"));
        assertTrue(category("Raids").allItems.contains("olmlet"));
        assertTrue(category("Other").allItems.contains("olmlet"));
    }

    private Categori category(String name)
    {
        for (Categori categori : categories)
        {
            if (categori.name.equals(name))
            {
                return categori;
            }
        }

        throw new AssertionError("No collection log tab named " + name);
    }
}
