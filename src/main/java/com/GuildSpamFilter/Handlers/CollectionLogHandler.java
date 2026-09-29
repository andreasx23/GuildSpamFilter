package com.GuildSpamFilter.Handlers;

import com.GuildSpamFilter.Models.Categori;
import com.GuildSpamFilter.Models.Section;
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

    public ArrayList<Categori> ReadData(Client client)
    {
        ArrayList<Categori> categoris = new ArrayList<>();

        for (int tabStructId : client.getEnum(TABS_ENUM_ID).getIntVals())
        {
            StructComposition tab = client.getStructComposition(tabStructId);
            Categori categori = new Categori();
            categori.name = tab.getStringValue(TAB_NAME_PARAM_ID);

            for (int pageStructId : client.getEnum(tab.getIntValue(TAB_PAGES_ENUM_PARAM_ID)).getIntVals())
            {
                StructComposition page = client.getStructComposition(pageStructId);
                Section section = new Section();
                section.name = page.getStringValue(PAGE_NAME_PARAM_ID);

                for (int itemId : client.getEnum(page.getIntValue(PAGE_ITEMS_ENUM_PARAM_ID)).getIntVals())
                {
                    String itemName = client.getItemDefinition(itemId).getName();
                    section.collectionLogs.add(itemName);
                    categori.allItems.add(itemName.toLowerCase());
                }

                categori.sections.add(section);
            }

            categoris.add(categori);
        }

        return categoris;
    }
}
