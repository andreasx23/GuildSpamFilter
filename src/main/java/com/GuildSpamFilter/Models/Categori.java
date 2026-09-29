package com.GuildSpamFilter.Models;

import java.util.ArrayList;
import java.util.HashSet;

public class Categori
{
    public String name;
    public ArrayList<Section> sections = new ArrayList<>();
    // Lowercase, so broadcasts match regardless of capitalization
    public HashSet<String> allItems = new HashSet<>();
}