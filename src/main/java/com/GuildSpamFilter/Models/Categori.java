package com.GuildSpamFilter.Models;

import java.util.ArrayList;
import java.util.HashSet;

public class Categori
{
    public String name;
    public ArrayList<Section> sections = new ArrayList<>();
    public HashSet<String> allItems = new HashSet<>();
}