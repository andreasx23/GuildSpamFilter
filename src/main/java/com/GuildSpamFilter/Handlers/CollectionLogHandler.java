package com.GuildSpamFilter.Handlers;

import com.GuildSpamFilter.Models.CollectionLogItem;
import com.GuildSpamFilter.Models.CollectionLogPage;
import com.GuildSpamFilter.Models.CollectionLogTab;
import net.runelite.api.Client;
import net.runelite.api.StructComposition;

import java.util.ArrayList;

/*
   Reads the collection log from the game's own cache, the same data the in-game collection log is built from:
   one enum lists the tabs (Bosses, Raids, ...), each tab has an enum of pages, and each page has an enum of item ids.
   Must be called on the client thread once the game has loaded.
*/
public class CollectionLogHandler
{
    private static final int TABS_ENUM_ID = 2102;
    private static final int TAB_NAME_PARAM_ID = 682;
    private static final int TAB_PAGES_ENUM_PARAM_ID = 683;
    private static final int PAGE_NAME_PARAM_ID = 689;
    private static final int PAGE_ITEMS_ENUM_PARAM_ID = 690;

    public ArrayList<CollectionLogTab> readData(Client client)
    {
        ArrayList<CollectionLogTab> tabs = new ArrayList<>();

        for (int tabStructId : client.getEnum(TABS_ENUM_ID).getIntVals())
        {
            StructComposition tabStruct = client.getStructComposition(tabStructId);
            CollectionLogTab tab = new CollectionLogTab();
            tab.name = tabStruct.getStringValue(TAB_NAME_PARAM_ID);

            for (int pageStructId : client.getEnum(tabStruct.getIntValue(TAB_PAGES_ENUM_PARAM_ID)).getIntVals())
            {
                StructComposition pageStruct = client.getStructComposition(pageStructId);
                CollectionLogPage page = new CollectionLogPage();
                page.name = pageStruct.getStringValue(PAGE_NAME_PARAM_ID);

                for (int itemId : client.getEnum(pageStruct.getIntValue(PAGE_ITEMS_ENUM_PARAM_ID)).getIntVals())
                {
                    String itemName = client.getItemDefinition(itemId).getName();
                    page.items.add(new CollectionLogItem(itemId, itemName));
                    tab.lowercaseItemNames.add(itemName.toLowerCase());
                }

                tab.pages.add(page);
            }

            tabs.add(tab);
        }

        return tabs;
    }
}
