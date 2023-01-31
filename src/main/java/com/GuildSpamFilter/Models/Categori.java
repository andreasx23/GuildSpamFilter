package com.GuildSpamFilter.Models;

import java.util.ArrayList;
import java.util.HashSet;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Categori
{
    @JsonProperty("Name")
    public String name;
    @JsonProperty("Sections")
    public ArrayList<Section> sections = new ArrayList<>();
    public HashSet<String> allItems = new HashSet<>();
}
