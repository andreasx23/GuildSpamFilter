package com.GuildSpamFilter.Models;

import java.util.ArrayList;
import java.util.HashSet;

public class CollectionLogTab
{
    public String name;
    public ArrayList<CollectionLogPage> pages = new ArrayList<>();
    // Every item name on this tab's pages, lowercase so broadcasts match regardless of capitalization
    public HashSet<String> lowercaseItemNames = new HashSet<>();
}
