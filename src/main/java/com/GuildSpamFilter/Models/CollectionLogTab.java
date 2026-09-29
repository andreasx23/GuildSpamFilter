package com.GuildSpamFilter.Models;

import java.util.ArrayList;
import java.util.HashSet;

public class CollectionLogTab
{
    // The tab names exactly as the game's collection log names them
    public static final String BOSSES = "Bosses";
    public static final String RAIDS = "Raids";
    public static final String CLUES = "Clues";
    public static final String MINIGAMES = "Minigames";
    public static final String OTHER = "Other";

    public String name;
    public ArrayList<CollectionLogPage> pages = new ArrayList<>();
    // Every item name on this tab's pages, lowercase so broadcasts match regardless of capitalization
    public HashSet<String> lowercaseItemNames = new HashSet<>();
}
