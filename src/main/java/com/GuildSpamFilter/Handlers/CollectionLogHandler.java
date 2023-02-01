package com.GuildSpamFilter.Handlers;

import com.GuildSpamFilter.Models.Categori;
import com.GuildSpamFilter.Models.Section;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;

public class CollectionLogHandler
{
    private static final ObjectMapper om = new ObjectMapper();

    public Categori[] ReadData() throws IOException
    {
        try (InputStream is = this.getClass().getResourceAsStream("/CollectionLogData.json"))
        {
            Categori[] categoris = om.readValue(is, Categori[].class);
            for (Categori categori: categoris)
            {
                for (Section section : categori.sections)
                {
                    categori.allItems.addAll(section.collectionLogs);
                }
            }
            return categoris;
        }
    }
}
