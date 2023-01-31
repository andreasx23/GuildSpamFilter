package com.GuildSpamFilter.Models;

import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Section
{
    @JsonProperty("Name")
    public String name;
    @JsonProperty("CollectionLogs")
    public ArrayList<String> collectionLogs = new ArrayList<>();
}
