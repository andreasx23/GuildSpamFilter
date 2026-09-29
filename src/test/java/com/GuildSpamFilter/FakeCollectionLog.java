package com.GuildSpamFilter;

import net.runelite.api.Client;
import net.runelite.api.EnumComposition;
import net.runelite.api.ItemComposition;
import net.runelite.api.StructComposition;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Builds a collection log in a mocked client's game cache, laid out the way the real game stores it:
 * enum 2102 lists the tab structs, each tab struct has a name (param 682) and an enum of page structs (param 683),
 * and each page struct has a name (param 689) and an enum of item ids (param 690).
 */
class FakeCollectionLog
{
    private final Map<String, Map<String, String[]>> tabs = new LinkedHashMap<>();
    private final Map<String, Integer> itemIds = new HashMap<>();
    private Map<String, String[]> currentTab;
    private int nextId = 1_000_000;

    FakeCollectionLog tab(String name)
    {
        currentTab = new LinkedHashMap<>();
        tabs.put(name, currentTab);
        return this;
    }

    FakeCollectionLog page(String name, String... itemNames)
    {
        currentTab.put(name, itemNames);
        return this;
    }

    void installOn(Client client)
    {
        int[] tabStructIds = new int[tabs.size()];
        int tabIndex = 0;
        for (Map.Entry<String, Map<String, String[]>> tab : tabs.entrySet())
        {
            int[] pageStructIds = new int[tab.getValue().size()];
            int pageIndex = 0;
            for (Map.Entry<String, String[]> page : tab.getValue().entrySet())
            {
                int[] pageItemIds = new int[page.getValue().length];
                for (int i = 0; i < pageItemIds.length; i++)
                {
                    String itemName = page.getValue()[i];
                    pageItemIds[i] = nextId++;
                    itemIds.putIfAbsent(itemName, pageItemIds[i]);
                    ItemComposition item = mock(ItemComposition.class);
                    when(item.getName()).thenReturn(itemName);
                    when(client.getItemDefinition(pageItemIds[i])).thenReturn(item);
                }

                int itemsEnumId = addEnum(client, nextId++, pageItemIds);
                pageStructIds[pageIndex++] = addStruct(client, 689, page.getKey(), 690, itemsEnumId);
            }

            int pagesEnumId = addEnum(client, nextId++, pageStructIds);
            tabStructIds[tabIndex++] = addStruct(client, 682, tab.getKey(), 683, pagesEnumId);
        }

        addEnum(client, 2102, tabStructIds);
    }

    /** The item id given to an item name, available after {@link #installOn}. */
    int itemId(String itemName)
    {
        Integer itemId = itemIds.get(itemName);
        if (itemId == null)
        {
            throw new IllegalArgumentException("No item named " + itemName + " in the fake collection log");
        }

        return itemId;
    }

    private static int addEnum(Client client, int enumId, int[] values)
    {
        EnumComposition enumComposition = mock(EnumComposition.class);
        when(enumComposition.getIntVals()).thenReturn(values);
        when(client.getEnum(enumId)).thenReturn(enumComposition);
        return enumId;
    }

    private int addStruct(Client client, int nameParamId, String name, int enumParamId, int enumId)
    {
        int structId = nextId++;
        StructComposition struct = mock(StructComposition.class);
        when(struct.getStringValue(nameParamId)).thenReturn(name);
        when(struct.getIntValue(enumParamId)).thenReturn(enumId);
        when(client.getStructComposition(structId)).thenReturn(struct);
        return structId;
    }
}
