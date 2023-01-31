package com.GuildSpamFilter.Handlers;

import com.GuildSpamFilter.Models.Categori;
import com.GuildSpamFilter.Models.Section;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;

public class CollectionLogHandler
{
    private static final ObjectMapper om = new ObjectMapper();

    public Categori[] ReadData() throws IOException
    {
        Categori[] categoris = om.readValue(new File("..\\JsonFiles\\CollectionLogData.json"), Categori[].class);
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
