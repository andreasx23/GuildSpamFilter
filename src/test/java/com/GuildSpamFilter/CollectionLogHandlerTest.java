package com.GuildSpamFilter;

import com.GuildSpamFilter.Handlers.CollectionLogHandler;
import com.GuildSpamFilter.Models.Categori;
import com.GuildSpamFilter.Models.Section;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CollectionLogHandlerTest
{
    private final ArrayList<Categori> categories = new CollectionLogHandler().ReadData();

    @Test
    public void readsTheFiveCollectionLogCategories()
    {
        List<String> names = new ArrayList<>();
        for (Categori categori : categories)
        {
            names.add(categori.name);
        }

        assertEquals(Arrays.asList("Bosses", "Raids", "Clues", "Minigames", "Other"), names);
    }

    @Test
    public void knowsItemsFromEachCategoryInLowercase()
    {
        assertTrue(category("Bosses").allItems.contains("abyssal whip"));
        assertTrue(category("Raids").allItems.contains("twisted bow"));
        assertTrue(category("Clues").allItems.contains("mole slippers"));
        assertTrue(category("Minigames").allItems.contains("fighter hat"));
        assertTrue(category("Other").allItems.contains("golden tench"));
    }

    @Test
    public void everySectionHasANameAndItems()
    {
        for (Categori categori : categories)
        {
            assertFalse(categori.name + " has no sections", categori.sections.isEmpty());
            for (Section section : categori.sections)
            {
                assertFalse("A section in " + categori.name + " has no name", section.name.trim().isEmpty());
                assertFalse(categori.name + "/" + section.name + " has no items", section.collectionLogs.isEmpty());
            }
        }
    }

    @Test
    public void itemNamesHaveNoStrayWhitespace()
    {
        for (Categori categori : categories)
        {
            for (Section section : categori.sections)
            {
                for (String item : section.collectionLogs)
                {
                    String where = categori.name + "/" + section.name + ": '" + item + "'";
                    assertFalse("Empty item name in " + where, item.trim().isEmpty());
                    assertEquals("Extra spaces around " + where, item.trim(), item);
                }
            }
        }
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

        fail("No collection log category named " + name);
        return null;
    }
}
