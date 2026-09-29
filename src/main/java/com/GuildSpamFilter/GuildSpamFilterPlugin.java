package com.GuildSpamFilter;

import com.GuildSpamFilter.Configs.AchievementDiaryTier;
import com.GuildSpamFilter.Configs.CombatAchievementTier;
import com.GuildSpamFilter.Handlers.CollectionLogHandler;
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

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

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

    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private GuildSpamFilterConfig config;
    private HashSet<String> personalBestsToIncludeOrExclude;
    private HashSet<String> customFilters;
    private HashSet<String> alwaysIncludedPlayerNames;
    private ArrayList<CollectionLogTab> collectionLogTabs;
    private HashMap<String, Integer> raidItemIds;
    private HashMap<String, Long> raidItemPrices;

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
        log.info("Clan Spam Filter started!");
        personalBestsToIncludeOrExclude = new HashSet<String>();
        customFilters = new HashSet<String>();
        alwaysIncludedPlayerNames = new HashSet<String>();
        raidItemIds = new HashMap<String, Integer>();
        raidItemPrices = new HashMap<String, Long>();

        collectionLogTabs = new ArrayList<CollectionLogTab>();

        clientThread.invoke(this::loadCollectionLog);
        updatePersonalBestsToIncludeOrExclude();
        updateCustomFilters();
        updateAlwaysIncludedPlayerNames();

        clientThread.invoke(client::refreshChat);
    }

    @Override
    protected void shutDown()
    {
        log.info("Clan Spam Filter stopped!");
        personalBestsToIncludeOrExclude = null;
        customFilters = null;
        alwaysIncludedPlayerNames = null;
        collectionLogTabs = null;
        raidItemIds = null;
        raidItemPrices = null;

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
        for (CollectionLogTab tab : collectionLogTabs)
        {
            itemCount += tab.lowercaseItemNames.size();
        }

        log.info("Loaded " + itemCount + " collection log items in " + collectionLogTabs.size() + " tabs");
        return true;
    }

    private void setUpRaidItemPrices()
    {
        addChambersOfXericItems();
        addTheatreOfBloodItems();
        addTombsOfAmascutItems();

        raidItemPrices.clear();
        for (Map.Entry<String, Integer> raidItem : raidItemIds.entrySet())
        {
            String itemName = raidItem.getKey();
            int itemId = raidItem.getValue();
            long itemPrice = itemManager.getItemPrice(itemId);
            raidItemPrices.put(itemName.toLowerCase(), itemPrice);
        }
    }

    private void addChambersOfXericItems()
    {
        raidItemIds.put("Twisted Bow", 20997);
        raidItemIds.put("Kodai insignia", 21043);
        raidItemIds.put("Elder maul", 21003);
        raidItemIds.put("Ancestral hat", 21018);
        raidItemIds.put("Ancestral robe bottom", 21024);
        raidItemIds.put("Ancestral robe top", 21021);
        raidItemIds.put("Dragon claws", 13652);
        raidItemIds.put("Twisted buckler", 21000);
        raidItemIds.put("Dragon hunter crossbow", 21012);
        raidItemIds.put("Dexterous prayer scroll", 21034);
        raidItemIds.put("Arcane prayer scroll", 21079);
        raidItemIds.put("Dinh's bulwark", 21015);
    }

    private void addTheatreOfBloodItems()
    {
        raidItemIds.put("Scythe of vitur (uncharged)", 22486);
        raidItemIds.put("Sanguinesti staff (uncharged)", 22481);
        raidItemIds.put("Ghrazi rapier", 22324);
        raidItemIds.put("Avernic defender hilt", 22477);
        raidItemIds.put("Justiciar chestguard", 22327);
        raidItemIds.put("Justiciar faceguard", 22326);
        raidItemIds.put("Justiciar legguards", 22328);
    }

    private void addTombsOfAmascutItems()
    {
        raidItemIds.put("Osmumten's fang", 26219);
        raidItemIds.put("Lightbearer", 25975);
        raidItemIds.put("Masori body", 27229);
        raidItemIds.put("Masori chaps", 27232);
        raidItemIds.put("Masori mask", 27226);
        raidItemIds.put("Elidinis' ward", 25985);
        raidItemIds.put("Tumeken's shadow (uncharged)", 27277);
    }

    private void updatePersonalBestsToIncludeOrExclude()
    {
        personalBestsToIncludeOrExclude.clear();
        String[] values = config.personalBestsToIncludeOrExclude()
                                .split(",");
        if (values.length > 0)
        {
            for (String value : values)
            {
                value = value.trim()
                             .toLowerCase();
                if (value.length() > 0)
                {
                    personalBestsToIncludeOrExclude.add(value);
                }
            }
        }

        log.debug("New list: " + String.join(", ", personalBestsToIncludeOrExclude));
    }

    private void updateCustomFilters()
    {
        customFilters.clear();
        String[] values = config.customFilters()
                                .split(",");
        if (values.length > 0)
        {
            for (String value : values)
            {
                value = value.trim()
                             .toLowerCase();
                if (value.length() > 0)
                {
                    customFilters.add(value);
                }
            }
        }

        log.debug("New list: " + String.join(", ", customFilters));
    }

    private void updateAlwaysIncludedPlayerNames()
    {
        alwaysIncludedPlayerNames.clear();
        String[] values = config.alwaysIncludedPlayerNames()
                                .split(",");
        if (values.length > 0)
        {
            for (String value : values)
            {
                value = value.trim()
                             .toLowerCase();
                if (value.length() > 0)
                {
                    alwaysIncludedPlayerNames.add(value);
                }
            }
        }

        log.debug("New list: " + String.join(", ", alwaysIncludedPlayerNames));
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
                      .equals("excludedPlayerNames"))
        {
            updateAlwaysIncludedPlayerNames();
        }

        clientThread.invoke(client::refreshChat);
    }

    @Subscribe
    public void onScriptCallbackEvent(ScriptCallbackEvent event)
    {
        if (!event.getEventName()
                  .equals("chatFilterCheck"))
        {
            return;
        }

        if (raidItemPrices.isEmpty())
        {
            setUpRaidItemPrices();
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
        log.debug("Broadcast message: " + message);

        // Check if message should be filtered and update stack accordingly
        if (shouldFilterMessage(message))
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
                filterCustomFilters(cleanedMessage);
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
                    log.debug("New broadcast for player detected skipping it.. Player name was: " + playerName);
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
            log.debug("Leagues broadcast detected..");
            return true;
        }

        return false;
    }

    private boolean filterPersonalBests(String message)
    {
        if (config.filterPersonalBests() && message.contains("achieved a new"))
        {
            log.debug("New personal best detected.. Mode was set to: " + config.personalBestMode());
            String partWithoutPlayerName = message.substring(12);
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
            log.debug("No match found removing it..");
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
            log.debug("Match found removing it..");
            return true;
        }

        return false;
    }

    private boolean filterRaidDrops(String message)
    {
        if (config.filterRaidDrop() && message.contains("received special loot from a raid"))
        {
            log.debug("New raid loot detected..");
            int startIndex = message.lastIndexOf(":");
            String itemStartPart = message.substring(startIndex);
            int endIndex = itemStartPart.lastIndexOf("(");
            String itemPart = itemStartPart.substring(1, endIndex);
            String item = itemPart.trim().toLowerCase();

            if (raidItemPrices.containsKey(item))
            {
                long gpValue = raidItemPrices.get(item);
                if (gpValue < config.raidLootGpThreshold() ||
                        gpValue == Integer.MAX_VALUE && gpValue == config.raidLootGpThreshold())
                {
                    log.debug("Raid loot was below threshold: " + gpValue + " removing it..");
                    return true;
                }
            }
        }

        return false;
    }

    private boolean filterRegularDrops(String message)
    {
        if (config.filterRegularDrops() && message.contains("received a drop"))
        {
            log.debug("New drop detected..");
            int index = message.lastIndexOf("(");
            int index2 = message.lastIndexOf(")");

            if (index != -1 && index2 != -1)
            {
                String part = message.substring(index + 1, index2)
                                     .replace(",", "")
                                     .replace("coins", "")
                                     .trim();
                long gpValue = Long.parseLong(part);
                if (gpValue < config.lootGpThreshold() ||
                        gpValue == Integer.MAX_VALUE && gpValue == config.lootGpThreshold())
                {
                    log.debug("Loot was below threshold: " + gpValue + " removing it..");
                    return true;
                }
            }
            else
            {
                log.debug("Loot detected removing it..");
                return true;
            }
        }

        return false;
    }

    private boolean filterPets(String message)
    {
        if (config.filterPets() &&
                (message.contains("has a funny feeling") || message.contains("acquired something special") || message.contains("something weird sneaking into")))
        {
            log.debug("New pet detected removing it..");
            return true;
        }

        return false;
    }

    private boolean filterMaxTotal(String message)
    {
        if (config.filterMaxTotal() && message.contains("has reached the highest possible total level of"))
        {
            log.debug("New max total detected removing it..");
            return true;
        }

        return false;
    }

    private boolean filterTotalLevelMilestone(String message)
    {
        if (config.filterTotalLevelMilestone() && message.contains("has reached a total"))
        {
            log.debug("New total level detected removing it..");
            String textToFind = "total level of ";
            int index = message.indexOf(textToFind);

            if (index != -1)
            {
                String part = message.substring(index + textToFind.length(), message.length() - 1);
                long totalLevel = Long.parseLong(part);
                if (totalLevel < config.totalLevelThreshold())
                {
                    log.debug("Total level was below threshold: " + totalLevel + " removing it..");
                    return true;
                }
            }
            else
            {
                log.debug("Total level detected removing it..");
                return true;
            }
        }

        return false;
    }

    private boolean filterXpMilestone(String message)
    {
        if (config.filterXpMilestone() && message.contains("has reached") && message.contains("XP in"))
        {
            log.debug("New XP milestone detected..");
            int index = message.indexOf("reached");
            int index2 = message.indexOf("XP in");

            if (index != -1 && index2 != -1)
            {
                String part = message.substring(index + 8, index2 - 1)
                                     .replace(",", "");
                long xp = Long.parseLong(part);
                if (xp < config.xpMilestoneThreshold())
                {
                    log.debug("XP milestone was below threshold: " + xp + " removing it..");
                    return true;
                }
            }
            else
            {
                log.debug("XP milestone detected removing it..");
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
            log.debug("New level up detected..");
            // Reads the level from both "Fishing level 90." and "a total level of 2,000."
            String textToFind = "level ";
            int index = message.lastIndexOf(textToFind);
            String part = index != -1
                    ? message.substring(index + textToFind.length()).replaceAll("[^0-9]", "")
                    : "";

            if (!part.isEmpty())
            {
                long level = Long.parseLong(part);
                if (level < config.levelThreshold())
                {
                    log.debug("Level was below threshold: " + level + " removing it..");
                    return true;
                }
            }
            else
            {
                log.debug("Level detected removing it..");
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

            if (index != -1 && index2 != -1)
            {
                String part = message.substring(index + 1, index2)
                                     .trim();
                int collectionLogCount = Integer.parseInt(part);

                if (config.enableCollectionLogThreshold() && config.collectionLogThreshold() > collectionLogCount)
                {
                    log.debug("Collection log count was below threshold: " + collectionLogCount + " removing it..");
                    return true;
                }
                else
                {
                    return filterCollectionLogByTab(message);
                }
            }
            else
            {
                log.debug("New collection log item detected removing it..");
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
                                 .trim()
                                 .toLowerCase();

        for (CollectionLogTab tab : collectionLogTabs)
        {
            switch (tab.name)
            {
                case "Bosses":
                    if (config.filterCollectionLogBosses() && tab.lowercaseItemNames.contains(itemName))
                    {
                        log.debug("New collection log item detected removing it..");
                        return true;
                    }
                    break;
                case "Raids":
                    if (config.filterCollectionLogRaids() && tab.lowercaseItemNames.contains(itemName))
                    {
                        log.debug("New collection log item detected removing it..");
                        return true;
                    }
                    break;
                case "Clues":
                    if (config.filterCollectionLogClues() && tab.lowercaseItemNames.contains(itemName))
                    {
                        log.debug("New collection log item detected removing it..");
                        return true;
                    }
                    break;
                case "Minigames":
                    if (config.filterCollectionLogMinigames() && tab.lowercaseItemNames.contains(itemName))
                    {
                        log.debug("New collection log item detected removing it..");
                        return true;
                    }
                    break;
                case "Other":
                    if (config.filterCollectionLogOther() && tab.lowercaseItemNames.contains(itemName))
                    {
                        log.debug("New collection log item detected removing it..");
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
            log.debug("New clan member detected removing it..");
            return true;
        }

        return false;
    }

    private boolean filterDefaultMessage(String message)
    {
        if (config.filterDefaultMessage() && message.contains("start each line of chat"))
        {
            log.debug("New default message detected removing it..");
            return true;
        }

        return false;
    }

    private boolean filterRareDrops(String message)
    {
        if (config.filterRareDrops() && message.contains("received a rare drop"))
        {
            log.debug("New rare drop detected removing it..");
            return true;
        }

        return false;
    }

    private boolean filterQuestComplete(String message)
    {
        if (config.filterQuestComplete() && message.contains("has completed a quest"))
        {
            log.debug("New quest completion detected removing it..");
            return true;
        }

        return false;
    }

    private boolean filterHardcoreDeath(String message)
    {
        if (config.filterHardcoreDeath() && message.contains("and lost their hardcore"))
        {
            log.debug("New hardcore death detected removing it..");
            return true;
        }

        return false;
    }

    private boolean filterClanMemberKicked(String message)
    {
        if (config.filterClanMemberKicked() && message.contains("has expelled"))
        {
            log.debug("New kicked clan member detected removing it..");
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
            if (left != -1 && right != -1)
            {
                String part = message.substring(left + 1, right - 6)
                                     .replace(",", "");
                int value = Integer.parseInt(part);
                if (value < config.playerDiedThreshold())
                {
                    log.debug("New player has been defeated by another player detected removing it..");
                    return true;
                }
            }
            else
            {
                log.debug("New player has been defeated by another player detected removing it..");
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
            if (left != -1 && right != -1)
            {
                String part = message.substring(left + 1, right - 6)
                                     .replace(",", "");
                int value = Integer.parseInt(part);
                if (value < config.playerKillThreshold())
                {
                    log.debug("New player kill detected removing it..");
                    return true;
                }
            }
            else
            {
                log.debug("New player kill detected removing it..");
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
                    log.debug("New max combat level up message detected removing it..");
                    return true;
                }
            }
            else
            {
                int index = message.indexOf("combat level");
                if (index != -1)
                {
                    String part = message.substring(index + 13);
                    String combatLevelText = part.substring(0, part.length() - 1);
                    int combatLevel = Integer.parseInt(combatLevelText);
                    if (config.combatLevelUpThreshold() > combatLevel)
                    {
                        log.debug("New combat level up message detected removing it..");
                        return true;
                    }
                }
                else
                {
                    log.debug("New combat level up message detected removing it..");
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
            log.debug("New Combat Achievement detected..");
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
                    log.debug("Combat Achievement threshold was set to: " +
                            threshold +
                            " and the incoming Combat Achievement was: " +
                            tier +
                            " removing it..");
                    return true;
                }
            }
            else
            {
                log.debug("Combat Achievement detected removing it..");
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
            log.debug("New achievement diary detected..");
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
                        log.debug("Achievement diary threshold was set to: " +
                                threshold +
                                " and the incoming achievement diary was: " +
                                tier +
                                " removing it..");
                        return true;
                    }
                }
                else
                {
                    log.debug("Achievement Diary detected removing it..");
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

    private boolean filterCustomFilters(String message)
    {
        if (customFilters.size() > 0)
        {
            log.debug("Custom filter was not empty scanning..");
            String lowercaseMessage = message.toLowerCase();
            for (String text : customFilters)
            {
                if (lowercaseMessage.contains(text))
                {
                    log.debug("Custom filter match found removing it..");
                    return true;
                }
            }
        }

        return false;
    }
}