package com.GuildSpamFilter;

import com.GuildSpamFilter.Configs.AchievementDiaryTier;
import com.GuildSpamFilter.Configs.CombatAchievementTier;
import com.GuildSpamFilter.Handlers.CollectionLogHandler;
import com.GuildSpamFilter.Models.CollectionLogItem;
import com.GuildSpamFilter.Models.CollectionLogPage;
import com.GuildSpamFilter.Models.CollectionLogTab;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.http.api.item.ItemPrice;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/*
   Shout out to Spam Filter for giving me a baseline on how to implement this simple Clan (broadcast) Spam Filter
   https://github.com/jackriccomini/spamfilter-plugin-runelite/blob/846413d594416195047e5d2ea233dce7a12fd85c/src/main/java/com/jackriccomini/spamfilter/SpamFilterPlugin.java
*/

@Slf4j
@PluginDescriptor(
        name = "Clan Spam Filter",
        tags = {
                "Spam filter",
                "Clan spam filter",
                "Spam",
                "Filter",
                "Guild Spam Filter",
                "Guild",
                "Clan",
                "Less clutter",
                "Organized chat",
                "Selective filtering",
                "Chat customization",
                "Filter clan messages",
                "Remove spam messages",
                "Chat cleanup",
                "Broadcast blocker"
        },
        description = "Clan chat filter to hide unwanted broadcasts and reduce chat clutter. Customize which messages to show or hide including: drops (with GP thresholds), personal bests, pets, level ups, XP milestones, collection log items, achievement diaries, Combat Achievements, raid loot, quest completions, and more. Features always-included players, custom filters, and granular threshold controls for a cleaner clan chat experience."
)
public class GuildSpamFilterPlugin extends Plugin
{
    private static final String EASY = "Easy";
    private static final String RAID_LOOT_TEXT = "received special loot from a raid";
    private static final String CABBAGE = "Cabbage";

    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private GuildSpamFilterConfig config;
    private HashSet<String> personalBestsToIncludeOrExclude;
    private HashSet<String> customFilters;
    private ArrayList<Pattern> customRegexFilters;
    private HashSet<String> alwaysIncludedPlayerNames;
    private ArrayList<CollectionLogTab> collectionLogTabs;
    // Lowercase item name to item id, for every item on the collection log's Raids tab
    private HashMap<String, Integer> raidItemIds;

    @Inject
    private ItemManager itemManager;

    @Provides
    GuildSpamFilterConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(GuildSpamFilterConfig.class);
    }

    @Override
    protected void startUp() throws RuntimeException, IOException
    {
        log.info("Clan Spam Filter started");
        collectionLogTabs = new ArrayList<CollectionLogTab>();
        raidItemIds = new HashMap<String, Integer>();

        clientThread.invoke(this::loadCollectionLog);
        updatePersonalBestsToIncludeOrExclude();
        updateCustomFilters();
        updateCustomRegexFilters();
        updateAlwaysIncludedPlayerNames();

        clientThread.invoke(client::refreshChat);
    }

    @Override
    protected void shutDown()
    {
        log.info("Clan Spam Filter stopped");
        personalBestsToIncludeOrExclude = null;
        customFilters = null;
        customRegexFilters = null;
        alwaysIncludedPlayerNames = null;
        collectionLogTabs = null;
        raidItemIds = null;

        clientThread.invoke(client::refreshChat);
    }

    private boolean loadCollectionLog()
    {
        // The collection log is read from the game's cache, which isn't available until the game has loaded.
        // Returning false makes the client thread try again on the next tick.
        if (client.getGameState().getState() < GameState.LOGIN_SCREEN.getState())
        {
            return false;
        }

        CollectionLogHandler collectionLogHandler = new CollectionLogHandler();
        collectionLogTabs = collectionLogHandler.readData(client);

        int itemCount = 0;
        raidItemIds = new HashMap<String, Integer>();
        for (CollectionLogTab tab : collectionLogTabs)
        {
            itemCount += tab.lowercaseItemNames.size();

            if (!CollectionLogTab.KNOWN_NAMES.contains(tab.name))
            {
                log.warn("Unknown collection log tab \"{}\", so its items can't be filtered", tab.name);
            }

            if (tab.name.equals(CollectionLogTab.RAIDS))
            {
                for (CollectionLogPage page : tab.pages)
                {
                    for (CollectionLogItem item : page.items)
                    {
                        raidItemIds.putIfAbsent(item.name.toLowerCase(), item.id);
                    }
                }
            }
        }

        if (raidItemIds.isEmpty())
        {
            log.warn("Found no raid items in the collection log, so raid loot won't be filtered");
        }

        log.info("Loaded {} collection log items in {} tabs, including {} raid items",
                itemCount, collectionLogTabs.size(), raidItemIds.size());
        return true;
    }

    private void updatePersonalBestsToIncludeOrExclude()
    {
        personalBestsToIncludeOrExclude = readList(config.personalBestsToIncludeOrExclude());
        log.debug("Personal bests to include or exclude: {}", personalBestsToIncludeOrExclude);
    }

    private void updateCustomFilters()
    {
        customFilters = readList(config.customFilters());
        log.debug("Custom filters: {}", customFilters);
    }

    private void updateAlwaysIncludedPlayerNames()
    {
        alwaysIncludedPlayerNames = readList(config.alwaysIncludedPlayerNames());
        log.debug("Always-included players: {}", alwaysIncludedPlayerNames);
    }

    private void updateCustomRegexFilters()
    {
        customRegexFilters = new ArrayList<Pattern>();
        // One per line rather than comma-separated, since commas are part of regular expressions, like \d{1,3}
        for (String value : config.customRegexFilters().split("[\\r\\n]+"))
        {
            value = value.trim();
            if (value.length() > 0)
            {
                try
                {
                    customRegexFilters.add(Pattern.compile(value, Pattern.CASE_INSENSITIVE));
                }
                catch (PatternSyntaxException e)
                {
                    log.warn("Ignoring custom regex filter \"{}\", which isn't a valid regular expression: {}",
                            value, e.getDescription());
                }
            }
        }

        log.debug("Custom regex filters: {}", customRegexFilters);
    }

    // Lowercase entries of a list setting. They can be separated by commas, new lines or both, with or without
    // spaces, so "a,b", "a, b" and "a,\nb" all give the same list
    private static HashSet<String> readList(String text)
    {
        HashSet<String> values = new HashSet<String>();
        for (String value : text.split("[,\\r\\n]"))
        {
            value = withNormalSpaces(value).trim()
                                           .toLowerCase();
            if (value.length() > 0)
            {
                values.add(value);
            }
        }

        return values;
    }

    // The game writes spaces in player names as non-breaking spaces, which users can't tell apart from normal ones
    private static String withNormalSpaces(String text)
    {
        return text.replace(' ', ' ');
    }

    private boolean isBroadcastMessageForPlayer(String playerName, String broadcastMessage)
    {
        if (broadcastMessage.length() <= playerName.length())
        {
            return false;
        }

        for (int i = 0; i < playerName.length(); i++)
        {
            char currentPlayerChar = playerName.charAt(i);
            if (currentPlayerChar == ' ')
            {
                continue;
            }

            char currentBroadcastMessageChar = broadcastMessage.charAt(i);
            if (currentPlayerChar != currentBroadcastMessageChar)
            {
                return false;
            }
        }

        // The broadcast's name has to end here too, so "Bob" doesn't also match "Bobby"
        return Character.isSpaceChar(broadcastMessage.charAt(playerName.length()));
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event)
    {
        if (!GuildSpamFilterConfig.GROUP.equals(event.getGroup()))
        {
            return;
        }

        // Settings change on the Swing thread, but broadcasts are filtered on the client thread,
        // so the lists are updated there too, where nothing can be reading them halfway through
        clientThread.invoke(() ->
        {
            // These are the settings' keyNames, which aren't always the same as their method names
            if (event.getKey()
                     .equals("pbsToIncludeOrExclude"))
            {
                updatePersonalBestsToIncludeOrExclude();
            }
            else if (event.getKey()
                          .equals("customFilters"))
            {
                updateCustomFilters();
            }
            else if (event.getKey()
                          .equals("customRegexFilters"))
            {
                updateCustomRegexFilters();
            }
            else if (event.getKey()
                          .equals("excludedPlayerNames"))
            {
                updateAlwaysIncludedPlayerNames();
            }

            client.refreshChat();
        });
    }

    @Subscribe
    public void onScriptCallbackEvent(ScriptCallbackEvent event)
    {
        if (!event.getEventName()
                  .equals("chatFilterCheck"))
        {
            return;
        }

        int[] intStack = client.getIntStack();
        int intStackSize = client.getIntStackSize();
        Object[] objectStack = client.getObjectStack();
        int objectStackSize = client.getObjectStackSize();

        if (intStack.length < intStackSize - 3 || objectStack.length < objectStackSize - 1)
        {
            return;
        }

        final int messageType = intStack[intStackSize - 2];
        ChatMessageType chatMessageType = ChatMessageType.of(messageType);
        if (chatMessageType != ChatMessageType.CLAN_MESSAGE)
        {
            return;
        }

        Object messageAsObject = objectStack[objectStackSize - 1];
        String message = ((String)messageAsObject).trim();
        log.debug("Checking broadcast: {}", message);

        boolean hide;
        try
        {
            hide = shouldFilterMessage(message);
        }
        catch (RuntimeException e)
        {
            // Show a broadcast we can't read, and say which one, instead of letting RuneLite log a full stack trace
            // for it on every chat refresh
            log.warn("Couldn't check broadcast \"{}\", so it's shown: {}", message, e.toString());
            hide = false;
        }

        if (hide)
        {
            intStack[intStackSize - 3] = 0;
        }

        objectStack[objectStackSize - 1] = message;
    }

    private boolean shouldFilterMessage(String message)
    {
        String cleanedMessage = message.replaceFirst("^<img=\\d+>\\s*", "")  // Remove <img=X> and any spaces after it
                                       .replaceFirst("^[^|]*\\|", "")        // Remove everything up to and including |
                                       .trim();

        // Always included players check
        if (isAlwaysIncludedPlayer(cleanedMessage))
        {
            return false;
        }

        // Check all filter conditions
        return filterLeaguesBroadcasts(message) ||
                filterPersonalBests(cleanedMessage) ||
                filterCabbageRaidLoot(cleanedMessage) ||
                filterUntradeableRaidLoot(cleanedMessage) ||
                filterRaidDrops(cleanedMessage) ||
                filterRegularDrops(cleanedMessage) ||
                filterPets(cleanedMessage) ||
                filterMaxTotal(cleanedMessage) ||
                filterTotalLevelMilestone(cleanedMessage) ||
                filterXpMilestone(cleanedMessage) ||
                filterLevelUp(cleanedMessage) ||
                filterCollectionLog(cleanedMessage) ||
                filterNewClanMember(cleanedMessage) ||
                filterDefaultMessage(cleanedMessage) ||
                filterRareDrops(cleanedMessage) ||
                filterQuestComplete(cleanedMessage) ||
                filterHardcoreDeath(cleanedMessage) ||
                filterClanMemberKicked(cleanedMessage) ||
                filterPlayerDied(cleanedMessage) ||
                filterPlayerKill(cleanedMessage) ||
                filterCombatLevelUp(cleanedMessage) ||
                filterCombatAchievements(cleanedMessage) ||
                filterAchievementDiaries(cleanedMessage) ||
                filterCustomFilters(cleanedMessage) ||
                filterCustomRegexFilters(cleanedMessage);
    }

    private boolean isAlwaysIncludedPlayer(String message)
    {
        if (alwaysIncludedPlayerNames.size() > 0)
        {
            String lowercaseBroadcastMessage = message.toLowerCase();
            for (String playerName : alwaysIncludedPlayerNames)
            {
                if (isBroadcastMessageForPlayer(playerName, lowercaseBroadcastMessage))
                {
                    log.debug("Showing broadcast for always-included player {}", playerName);
                    return true;
                }
            }
        }

        return false;
    }

    private boolean filterLeaguesBroadcasts(String message)
    {
        if (config.filterLeaguesBroadcasts() && message.contains("<img=22>"))
        {
            log.debug("Hiding Leagues broadcast");
            return true;
        }

        return false;
    }

    private boolean filterPersonalBests(String message)
    {
        if (config.filterPersonalBests() && message.contains("achieved a new"))
        {
            // Only the part after the player's name, so a name like "Vorkath Fan" doesn't match the list
            String partWithoutPlayerName = message.substring(message.indexOf("achieved a new"));
            String lowercaseMessage = partWithoutPlayerName.toLowerCase();

            switch (config.personalBestMode())
            {
                case EXCLUDE_ALL_EXCEPT:
                    return shouldExcludeAllExcept(lowercaseMessage);
                case INCLUDE_ALL_EXCEPT:
                    return shouldIncludeAllExcept(lowercaseMessage);
            }
        }

        return false;
    }

    private boolean shouldExcludeAllExcept(String lowercaseMessage)
    {
        boolean found = false;
        if (personalBestsToIncludeOrExclude.size() > 0)
        {
            for (String text : personalBestsToIncludeOrExclude)
            {
                if (lowercaseMessage.contains(text))
                {
                    found = true;
                    break;
                }
            }
        }

        if (!found)
        {
            log.debug("Hiding personal best that isn't in the list (Personal Best Mode: Exclude all except)");
            return true;
        }

        return false;
    }

    private boolean shouldIncludeAllExcept(String lowercaseMessage)
    {
        boolean found = false;
        if (personalBestsToIncludeOrExclude.size() > 0)
        {
            for (String text : personalBestsToIncludeOrExclude)
            {
                if (lowercaseMessage.contains(text))
                {
                    found = true;
                    break;
                }
            }
        }

        if (found)
        {
            log.debug("Hiding personal best that is in the list (Personal Best Mode: Include all except)");
            return true;
        }

        return false;
    }

    private boolean filterCabbageRaidLoot(String message)
    {
        // The Fractured Archive broadcasts Cabbages as "very valuable" special loot as a joke
        if (config.filterCabbageRaidLoot() && message.contains(RAID_LOOT_TEXT) &&
                readLoot(message).itemName.equalsIgnoreCase(CABBAGE))
        {
            log.debug("Hiding Cabbage raid loot");
            return true;
        }

        return false;
    }

    private boolean filterUntradeableRaidLoot(String message)
    {
        if (config.filterUntradeableRaidLoot() && message.contains(RAID_LOOT_TEXT))
        {
            Loot loot = readLoot(message);
            Integer itemId = raidItemIds.get(loot.itemName.toLowerCase());
            if (itemId != null && !itemManager.getItemComposition(itemId).isTradeable())
            {
                log.debug("Hiding untradeable raid loot {}", loot.itemName);
                return true;
            }
        }

        return false;
    }

    private boolean filterRaidDrops(String message)
    {
        if (config.filterRaidDrop() && message.contains(RAID_LOOT_TEXT))
        {
            Loot loot = readLoot(message);
            Integer itemId = raidItemIds.get(loot.itemName.toLowerCase());
            boolean isCabbage = loot.itemName.equalsIgnoreCase(CABBAGE);
            if (itemId == null && !isCabbage)
            {
                log.debug("Showing raid loot {}, since it isn't on the collection log's Raids tab", loot.itemName);
                return false;
            }

            // Cabbages aren't on the Raids tab, so their id is looked up by name
            long gpValue = isCabbage ? findHighAlchValue(findTradeableItemId(loot.itemName), loot) :
                    findRaidLootValue(itemId, loot);

            if (gpValue == -1)
            {
                log.debug("Hiding raid loot {} without a price (Raid Loot GP Threshold: {})",
                        loot.itemName, config.raidLootGpThreshold());
                return true;
            }

            if (isBelowThreshold(gpValue, config.raidLootGpThreshold()))
            {
                log.debug("Hiding raid loot {} x {} worth {} gp (Raid Loot GP Threshold: {})",
                        loot.quantity, loot.itemName, gpValue, config.raidLootGpThreshold());
                return true;
            }
        }

        return false;
    }

    // Returns -1 if the value can't be found
    private long findRaidLootValue(int itemId, Loot loot)
    {
        // Untradeable items have no Grand Exchange price, so they're judged by their high-alch value
        if (!itemManager.getItemComposition(itemId).isTradeable())
        {
            return findHighAlchValue(itemId, loot);
        }

        // Looked up now rather than cached, so the price is always current. Nothing has a price before RuneLite has
        // fetched prices, so fall back to the coin value in the broadcast
        long price = itemManager.getItemPrice(itemId);
        return price > 0 ? price * loot.quantity : loot.coinValue;
    }

    // Returns -1 if itemId is -1
    private long findHighAlchValue(int itemId, Loot loot)
    {
        return itemId != -1 ? (long) itemManager.getItemComposition(itemId).getHaPrice() * loot.quantity : -1;
    }

    private static final class Loot
    {
        String itemName;
        long quantity = 1;
        // -1 if the broadcast has no coin value
        long coinValue = -1;
    }

    // Reads raid loot and drop broadcasts. The game has used several formats:
    // "...received a drop: Viggora's chainmace (u) (3,950,787 coins).", "...from a raid: 884 x Iron ore (very valuable)."
    // and "...from a raid: Cabbage."
    private static Loot readLoot(String message)
    {
        Loot loot = new Loot();
        String itemPart = message.substring(message.lastIndexOf(":") + 1).trim();
        if (itemPart.endsWith("."))
        {
            itemPart = itemPart.substring(0, itemPart.length() - 1).trim();
        }

        // Remove the value the game adds after the item name, but keep brackets that are part of the name,
        // like "Scythe of vitur (uncharged)"
        int bracketIndex = itemPart.lastIndexOf("(");
        while (bracketIndex != -1 && itemPart.endsWith(")"))
        {
            String bracketPart = itemPart.substring(bracketIndex);
            if (bracketPart.contains("coins"))
            {
                loot.coinValue = readNumber(bracketPart);
            }
            else if (!bracketPart.contains("valuable"))
            {
                break;
            }

            itemPart = itemPart.substring(0, bracketIndex).trim();
            bracketIndex = itemPart.lastIndexOf("(");
        }

        int quantityIndex = itemPart.indexOf(" x ");
        if (quantityIndex != -1 && itemPart.substring(0, quantityIndex).matches("\\d[\\d,]*"))
        {
            loot.quantity = readNumber(itemPart.substring(0, quantityIndex));
            itemPart = itemPart.substring(quantityIndex + 3).trim();
        }

        loot.itemName = itemPart;
        return loot;
    }

    // A threshold at its maximum, the default, hides everything, even values too big to type into the setting
    private static boolean isBelowThreshold(long gpValue, int threshold)
    {
        return threshold == Integer.MAX_VALUE || gpValue < threshold;
    }

    private boolean filterRegularDrops(String message)
    {
        if (config.filterRegularDrops() && message.contains("received a drop"))
        {
            // Drops normally include their coin value. Without one, like "(very valuable)", use the Grand Exchange price
            Loot loot = readLoot(message);
            long gpValue = loot.coinValue != -1 ? loot.coinValue : findGrandExchangeValue(loot);

            if (gpValue != -1)
            {
                if (isBelowThreshold(gpValue, config.lootGpThreshold()))
                {
                    log.debug("Hiding drop {} x {} worth {} gp (Loot GP Threshold: {})",
                            loot.quantity, loot.itemName, gpValue, config.lootGpThreshold());
                    return true;
                }
            }
            else
            {
                log.debug("Hiding drop {} without a value", loot.itemName);
                return true;
            }
        }

        return false;
    }

    // Returns -1 if no tradeable item has exactly this name, or it has no price
    private long findGrandExchangeValue(Loot loot)
    {
        int itemId = findTradeableItemId(loot.itemName);
        long price = itemId != -1 ? itemManager.getItemPrice(itemId) : 0;
        return price > 0 ? price * loot.quantity : -1;
    }

    // Returns -1 if no tradeable item has exactly this name
    private int findTradeableItemId(String itemName)
    {
        for (ItemPrice item : itemManager.search(itemName))
        {
            if (item.getName().equalsIgnoreCase(itemName))
            {
                return item.getId();
            }
        }

        return -1;
    }

    private boolean filterPets(String message)
    {
        if (config.filterPets() &&
                (message.contains("has a funny feeling") || message.contains("acquired something special") || message.contains("something weird sneaking into")))
        {
            log.debug("Hiding pet broadcast");
            return true;
        }

        return false;
    }

    private boolean filterMaxTotal(String message)
    {
        if (config.filterMaxTotal() && message.contains("has reached the highest possible total level of"))
        {
            log.debug("Hiding max total level broadcast");
            return true;
        }

        return false;
    }

    private boolean filterTotalLevelMilestone(String message)
    {
        if (config.filterTotalLevelMilestone() && message.contains("has reached a total"))
        {
            String textToFind = "total level of ";
            int index = message.indexOf(textToFind);
            long totalLevel = index != -1 ? readNumber(message.substring(index + textToFind.length())) : -1;

            if (totalLevel != -1)
            {
                if (totalLevel < config.totalLevelThreshold())
                {
                    log.debug("Hiding total level {} (Total Level Threshold: {})", totalLevel, config.totalLevelThreshold());
                    return true;
                }
            }
            else
            {
                log.debug("Hiding total level milestone without a level");
                return true;
            }
        }

        return false;
    }

    private boolean filterXpMilestone(String message)
    {
        if (config.filterXpMilestone() && message.contains("has reached") && message.contains("XP in"))
        {
            String textToFind = "reached";
            int index = message.indexOf(textToFind);
            int index2 = message.indexOf("XP in");
            long xp = index != -1 && index2 > index ? readNumber(message.substring(index + textToFind.length(), index2)) : -1;

            if (xp != -1)
            {
                if (xp < config.xpMilestoneThreshold())
                {
                    log.debug("Hiding XP milestone of {} XP (XP Milestone Threshold: {})", xp, config.xpMilestoneThreshold());
                    return true;
                }
            }
            else
            {
                log.debug("Hiding XP milestone without an amount");
                return true;
            }
        }

        return false;
    }

    private boolean filterLevelUp(String message)
    {
        if (config.filterLevelUp() &&
                message.contains("has reached") &&
                message.contains("level") &&
                !message.contains("combat level"))
        {
            // Reads the level from both "Fishing level 90." and "a total level of 2,000."
            String textToFind = "level ";
            int index = message.lastIndexOf(textToFind);
            long level = index != -1 ? readNumber(message.substring(index + textToFind.length())) : -1;

            if (level != -1)
            {
                if (level < config.levelThreshold())
                {
                    log.debug("Hiding level up to level {} (Level Threshold: {})", level, config.levelThreshold());
                    return true;
                }
            }
            else
            {
                log.debug("Hiding level up without a level");
                return true;
            }
        }

        return false;
    }

    private boolean filterCollectionLog(String message)
    {
        if ((config.filterCollectionLogBosses() ||
                     config.filterCollectionLogRaids() ||
                     config.filterCollectionLogClues() ||
                     config.filterCollectionLogMinigames() ||
                     config.filterCollectionLogOther() ||
                     config.enableCollectionLogThreshold()) && message.contains("a new collection log item"))
        {
            int index = message.lastIndexOf("(");
            int index2 = message.lastIndexOf("/");
            long collectionLogCount = index != -1 && index2 > index ? readNumber(message.substring(index + 1, index2)) : -1;

            if (collectionLogCount != -1)
            {
                if (config.enableCollectionLogThreshold() && config.collectionLogThreshold() > collectionLogCount)
                {
                    log.debug("Hiding collection log item from a player with {} slots (Collection Log Threshold: {})",
                            collectionLogCount, config.collectionLogThreshold());
                    return true;
                }
                else
                {
                    return filterCollectionLogByTab(message);
                }
            }
            else
            {
                log.debug("Hiding collection log item without a slot count");
                return true;
            }
        }

        return false;
    }

    private boolean filterCollectionLogByTab(String message)
    {
        int index = message.indexOf(":") + 1;
        int index2 = message.lastIndexOf("(");
        String itemName = message.substring(index, index2)
                                 .trim();
        String lowercaseItemName = itemName.toLowerCase();

        for (CollectionLogTab tab : collectionLogTabs)
        {
            switch (tab.name)
            {
                case CollectionLogTab.BOSSES:
                    if (config.filterCollectionLogBosses() && tab.lowercaseItemNames.contains(lowercaseItemName))
                    {
                        log.debug("Hiding collection log item {} from the {} tab", itemName, tab.name);
                        return true;
                    }
                    break;
                case CollectionLogTab.RAIDS:
                    if (config.filterCollectionLogRaids() && tab.lowercaseItemNames.contains(lowercaseItemName))
                    {
                        log.debug("Hiding collection log item {} from the {} tab", itemName, tab.name);
                        return true;
                    }
                    break;
                case CollectionLogTab.CLUES:
                    if (config.filterCollectionLogClues() && tab.lowercaseItemNames.contains(lowercaseItemName))
                    {
                        log.debug("Hiding collection log item {} from the {} tab", itemName, tab.name);
                        return true;
                    }
                    break;
                case CollectionLogTab.MINIGAMES:
                    if (config.filterCollectionLogMinigames() && tab.lowercaseItemNames.contains(lowercaseItemName))
                    {
                        log.debug("Hiding collection log item {} from the {} tab", itemName, tab.name);
                        return true;
                    }
                    break;
                case CollectionLogTab.OTHER:
                    if (config.filterCollectionLogOther() && tab.lowercaseItemNames.contains(lowercaseItemName))
                    {
                        log.debug("Hiding collection log item {} from the {} tab", itemName, tab.name);
                        return true;
                    }
                    break;
            }
        }

        return false;
    }

    private boolean filterNewClanMember(String message)
    {
        if (config.filterNewClanMember() && message.contains("has been invited into the"))
        {
            log.debug("Hiding new clan member broadcast");
            return true;
        }

        return false;
    }

    private boolean filterDefaultMessage(String message)
    {
        if (config.filterDefaultMessage() && message.contains("start each line of chat"))
        {
            log.debug("Hiding clan login message");
            return true;
        }

        return false;
    }

    private boolean filterRareDrops(String message)
    {
        if (config.filterRareDrops() && message.contains("received a rare drop"))
        {
            log.debug("Hiding rare drop broadcast");
            return true;
        }

        return false;
    }

    private boolean filterQuestComplete(String message)
    {
        if (config.filterQuestComplete() && message.contains("has completed a quest"))
        {
            log.debug("Hiding quest completion broadcast");
            return true;
        }

        return false;
    }

    private boolean filterHardcoreDeath(String message)
    {
        if (config.filterHardcoreDeath() && message.contains("and lost their hardcore"))
        {
            log.debug("Hiding hardcore death broadcast");
            return true;
        }

        return false;
    }

    private boolean filterClanMemberKicked(String message)
    {
        if (config.filterClanMemberKicked() && message.contains("has expelled"))
        {
            log.debug("Hiding kicked clan member broadcast");
            return true;
        }

        return false;
    }

    private boolean filterPlayerDied(String message)
    {
        if (config.filterPlayerDied() && message.contains("has been defeated by"))
        {
            int left = message.indexOf("(");
            int right = message.indexOf(")");
            long value = left != -1 && right > left ? readNumber(message.substring(left + 1, right)) : -1;

            if (value != -1)
            {
                if (value < config.playerDiedThreshold())
                {
                    log.debug("Hiding player death that lost {} gp (Player Death Threshold: {})",
                            value, config.playerDiedThreshold());
                    return true;
                }
            }
            else
            {
                log.debug("Hiding player death without a value");
                return true;
            }
        }

        return false;
    }

    private boolean filterPlayerKill(String message)
    {
        if (config.filterPlayerKill() && message.contains("has defeated"))
        {
            int left = message.indexOf("(");
            int right = message.indexOf(")");
            long value = left != -1 && right > left ? readNumber(message.substring(left + 1, right)) : -1;

            if (value != -1)
            {
                if (value < config.playerKillThreshold())
                {
                    log.debug("Hiding player kill that gained {} gp (Player Kill Threshold: {})",
                            value, config.playerKillThreshold());
                    return true;
                }
            }
            else
            {
                log.debug("Hiding player kill without a value");
                return true;
            }
        }

        return false;
    }

    private boolean filterCombatLevelUp(String message)
    {
        if (config.filterCombatLevelUps() &&
                (message.contains("has reached combat level") || message.contains("highest possible combat level")))
        {
            boolean isMaxCombatMessage = message.contains("highest possible combat level");
            if (isMaxCombatMessage)
            {
                if (config.combatLevelUpThreshold() > 126)
                {
                    log.debug("Hiding max combat broadcast (Combat Level Up Threshold: {})", config.combatLevelUpThreshold());
                    return true;
                }
            }
            else
            {
                String textToFind = "combat level";
                int index = message.indexOf(textToFind);
                long combatLevel = index != -1 ? readNumber(message.substring(index + textToFind.length())) : -1;

                if (combatLevel != -1)
                {
                    if (config.combatLevelUpThreshold() > combatLevel)
                    {
                        log.debug("Hiding combat level up to {} (Combat Level Up Threshold: {})",
                                combatLevel, config.combatLevelUpThreshold());
                        return true;
                    }
                }
                else
                {
                    log.debug("Hiding combat level up without a level");
                    return true;
                }
            }
        }

        return false;
    }

    private boolean filterCombatAchievements(String message)
    {
        if ((config.filterCombatAchievementTiers() && message.contains("Combat Achievement")) ||
                (config.filterCombatAchievementTasks() && message.contains("combat task")))
        {
            return processCombatAchievementFiltering(message);
        }

        return false;
    }

    private boolean processCombatAchievementFiltering(String message)
    {
        int index = -1;
        String indexText = "";
        if (config.filterCombatAchievementTiers() && message.contains("Combat Achievement"))
        {
            // Search from the end, so a player name containing "the" isn't mistaken for it
            indexText = " the";
            index = message.lastIndexOf(indexText + " ");
        }
        else if (config.filterCombatAchievementTasks() && message.contains("combat task"))
        {
            if (message.contains("completed an"))
            {
                indexText = "completed an";
            }
            else if (message.contains("completed a"))
            {
                indexText = "completed a";
            }

            index = message.indexOf(indexText);
        }

        if (index != -1)
        {
            String part = message.substring(index + indexText.length() + 1);
            int index2 = part.indexOf(" ");
            if (index2 != -1)
            {
                String tier = part.substring(0, index2);
                CombatAchievementTier threshold = config.combatAchievementThreshold();
                int tierId = getCombatAchievementTierId(tier);
                if (tierId == -1 || threshold.getId() > tierId)
                {
                    log.debug("Hiding {} Combat Achievement (Combat Achievement Threshold: {})", tier, threshold);
                    return true;
                }
            }
            else
            {
                log.debug("Hiding Combat Achievement without a tier");
                return true;
            }
        }

        return false;
    }

    private int getCombatAchievementTierId(String tier)
    {
        // Easy isn't offered as a threshold, so it isn't part of CombatAchievementTier
        if (EASY.equalsIgnoreCase(tier))
        {
            return 0;
        }

        for (CombatAchievementTier combatAchievementTier : CombatAchievementTier.values())
        {
            if (combatAchievementTier != CombatAchievementTier.ALL && combatAchievementTier.toString().equalsIgnoreCase(tier))
            {
                return combatAchievementTier.getId();
            }
        }

        return -1;
    }

    private boolean filterAchievementDiaries(String message)
    {
        if (config.filterAchievementDiaries() && message.contains(" diary"))
        {
            String textToFind = "completed the";
            int index = message.indexOf(textToFind);
            if (index != -1)
            {
                String part = message.substring(index + textToFind.length() + 1);
                int index2 = part.indexOf(" ");
                if (index2 != -1)
                {
                    String tier = part.substring(0, index2);
                    AchievementDiaryTier threshold = config.achievementDiariesThreshold();
                    int tierId = getAchievementDiaryTierId(tier);
                    if (tierId == -1 || threshold.getId() > tierId)
                    {
                        log.debug("Hiding {} achievement diary (Achievement Diary Threshold: {})", tier, threshold);
                        return true;
                    }
                }
                else
                {
                    log.debug("Hiding achievement diary without a tier");
                    return true;
                }
            }
        }

        return false;
    }

    private int getAchievementDiaryTierId(String tier)
    {
        // Easy isn't offered as a threshold, so it isn't part of AchievementDiaryTier
        if (EASY.equalsIgnoreCase(tier))
        {
            return 0;
        }

        for (AchievementDiaryTier diaryTier : AchievementDiaryTier.values())
        {
            if (diaryTier != AchievementDiaryTier.ALL && diaryTier.toString().equalsIgnoreCase(tier))
            {
                return diaryTier.getId();
            }
        }

        return -1;
    }

    // Reads the number in text, ignoring commas and anything else around it. Returns -1 if there's no number.
    private static long readNumber(String text)
    {
        String digits = text.replaceAll("[^0-9]", "");
        if (digits.isEmpty() || digits.length() > 18)
        {
            return -1;
        }

        return Long.parseLong(digits);
    }

    private boolean filterCustomFilters(String message)
    {
        if (customFilters.size() > 0)
        {
            String lowercaseMessage = withNormalSpaces(message).toLowerCase();
            for (String text : customFilters)
            {
                if (lowercaseMessage.contains(text))
                {
                    log.debug("Hiding broadcast matching custom filter \"{}\"", text);
                    return true;
                }
            }
        }

        return false;
    }

    private boolean filterCustomRegexFilters(String message)
    {
        if (customRegexFilters.size() > 0)
        {
            String messageWithNormalSpaces = withNormalSpaces(message);
            for (Pattern pattern : customRegexFilters)
            {
                if (pattern.matcher(messageWithNormalSpaces).find())
                {
                    log.debug("Hiding broadcast matching custom regex filter \"{}\"", pattern.pattern());
                    return true;
                }
            }
        }

        return false;
    }
}