package com.GuildSpamFilter;

import com.GuildSpamFilter.Configs.AchievementDiariesEnum;
import com.GuildSpamFilter.Configs.CombatDiariesEnum;
import com.GuildSpamFilter.Handlers.CollectionLogHandler;
import com.GuildSpamFilter.Models.Categori;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.events.ScriptCallbackEvent;
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
        description = "Clan chat filter to hide unwanted broadcasts and reduce chat clutter. Customize which messages to show or hide including: drops (with GP thresholds), personal bests, pets, level ups, XP milestones, collection log items, achievement/combat diaries, raid loot, quest completions, and more. Features player whitelisting, custom filters, and granular threshold controls for a cleaner clan chat experience."
)
public class GuildSpamFilterPlugin extends Plugin
{
    private static final String EASY = "Easy";

    @Inject
    private Client client;
    @Inject
    private GuildSpamFilterConfig config;
    private HashSet<String> pbsToIncludeOrExclude;
    private HashSet<String> customFilters;
    private HashSet<String> alwaysIncludedPlayerNames;
    private ArrayList<Categori> categoris;
    private HashMap<String, Integer> raidItemsIds;
    private HashMap<String, Integer> raidItemPrices;

    @Inject
    private ItemManager _itemManager;

    @Provides
    GuildSpamFilterConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(GuildSpamFilterConfig.class);
    }

    @Override
    protected void startUp() throws RuntimeException, IOException
    {
        log.info("Clan Spam Filter started!");
        pbsToIncludeOrExclude = new HashSet<String>();
        customFilters = new HashSet<String>();
        alwaysIncludedPlayerNames = new HashSet<String>();
        raidItemsIds = new HashMap<String, Integer>();
        raidItemPrices = new HashMap<String, Integer>();

        CollectionLogHandler collectionLogHandler = new CollectionLogHandler();
        categoris = collectionLogHandler.ReadData();
        UpdatePbsToIncludeOrExclude();
        UpdateCustomFilters();
        UpdateAlwaysIncludedPlayerIgnsFromBroadcasts();
    }

    @Override
    protected void shutDown()
    {
        log.info("Clan Spam Filter stopped!");
        pbsToIncludeOrExclude = null;
        customFilters = null;
        alwaysIncludedPlayerNames = null;
        categoris = null;
        raidItemsIds = null;
        raidItemPrices = null;
    }

    private void SetupRaidItemPrices()
    {
        AddCoxRaidItems();
        AddTobRaidItems();
        AddToaRaidItems();

        raidItemPrices.clear();
        for (Map.Entry<String, Integer> kv : raidItemsIds.entrySet())
        {
            String key = kv.getKey();
            int value = kv.getValue();
            int itemPrice = _itemManager.getItemPrice(value);
            raidItemPrices.put(key.toLowerCase(), itemPrice);
        }
    }

    private void AddCoxRaidItems()
    {
        raidItemsIds.put("Twisted Bow", 20997);
        raidItemsIds.put("Kodai insignia", 21043);
        raidItemsIds.put("Elder maul", 21003);
        raidItemsIds.put("Ancestral hat", 21018);
        raidItemsIds.put("Ancestral robe bottom", 21024);
        raidItemsIds.put("Ancestral robe top", 21021);
        raidItemsIds.put("Dragon claws", 13652);
        raidItemsIds.put("Twisted buckler", 21000);
        raidItemsIds.put("Dragon hunter crossbow", 21012);
        raidItemsIds.put("Dexterous prayer scroll", 21034);
        raidItemsIds.put("Arcane prayer scroll", 21079);
        raidItemsIds.put("Dinh's bulwark", 21015);
    }

    private void AddTobRaidItems()
    {
        raidItemsIds.put("Scythe of vitur (uncharged)", 22486);
        raidItemsIds.put("Sanguinesti staff (uncharged)", 22481);
        raidItemsIds.put("Ghrazi rapier", 22324);
        raidItemsIds.put("Avernic defender hilt", 22477);
        raidItemsIds.put("Justiciar chestguard", 22327);
        raidItemsIds.put("Justiciar faceguard", 22326);
        raidItemsIds.put("Justiciar legguards", 22328);
    }

    private void AddToaRaidItems()
    {
        raidItemsIds.put("Osmumten's fang", 26219);
        raidItemsIds.put("Lightbearer", 25975);
        raidItemsIds.put("Masori body", 27229);
        raidItemsIds.put("Masori chaps", 27232);
        raidItemsIds.put("Masori mask", 27226);
        raidItemsIds.put("Elidinis' ward", 25985);
        raidItemsIds.put("Tumeken's shadow (uncharged)", 27277);
    }

    private void UpdatePbsToIncludeOrExclude()
    {
        pbsToIncludeOrExclude.clear();
        String[] values = config.pbsToIncludeOrExclude()
                                .split(",");
        if (values.length > 0)
        {
            for (String value : values)
            {
                value = value.trim()
                             .toLowerCase();
                if (value.length() > 0)
                {
                    pbsToIncludeOrExclude.add(value);
                }
            }
        }

        log.debug("New list: " + String.join(", ", pbsToIncludeOrExclude));
    }

    private void UpdateCustomFilters()
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

    private void UpdateAlwaysIncludedPlayerIgnsFromBroadcasts()
    {
        alwaysIncludedPlayerNames.clear();
        String[] values = config.excludedPlayerNames()
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

        return true;
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event)
    {
        if (event.getKey()
                 .equals("pbsToIncludeOrExclude"))
        {
            UpdatePbsToIncludeOrExclude();
        }
        else if (event.getKey()
                      .equals("customFilters"))
        {
            UpdateCustomFilters();
        }
        else if (event.getKey()
                      .equals("excludedPlayerNames"))
        {
            UpdateAlwaysIncludedPlayerIgnsFromBroadcasts();
        }
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
            SetupRaidItemPrices();
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
                filterCombatDiaries(cleanedMessage) ||
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
        if (config.filterPb() && message.contains("achieved a new"))
        {
            log.debug("New PB detected.. Mode was set to: " + config.pbToIncludeOrExcludeEnum());
            String partWithoutPlayerName = message.substring(12);
            String lowercaseMessage = partWithoutPlayerName.toLowerCase();

            switch (config.pbToIncludeOrExcludeEnum())
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
        if (pbsToIncludeOrExclude.size() > 0)
        {
            for (String text : pbsToIncludeOrExclude)
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
        if (pbsToIncludeOrExclude.size() > 0)
        {
            for (String text : pbsToIncludeOrExclude)
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
                Integer gpValue = raidItemPrices.get(item);
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
            int index = message.indexOf("(");
            int index2 = message.indexOf(")");

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
            String textToFind = "level of";
            int index = message.indexOf(textToFind);

            if (index != -1)
            {
                String part = message.substring(index + textToFind.length() + 1, message.length() - 1);
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
                int collectionLogs = Integer.parseInt(part);

                if (config.enableCollectionLogThreshold() && config.filterCollectionLogThreshold() > collectionLogs)
                {
                    log.debug("Collection long amount was below threshold: " + collectionLogs + " removing it..");
                    return true;
                }
                else
                {
                    return filterCollectionLogByCategory(message);
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

    private boolean filterCollectionLogByCategory(String message)
    {
        int index = message.indexOf(":") + 1;
        int index2 = message.lastIndexOf("(");
        String part = message.substring(index, index2)
                             .trim();

        for (Categori categori : categoris)
        {
            switch (categori.name)
            {
                case "Bosses":
                    if (config.filterCollectionLogBosses() && categori.allItems.contains(part))
                    {
                        log.debug("New collection log item detected removing it..");
                        return true;
                    }
                    break;
                case "Raids":
                    if (config.filterCollectionLogRaids() && categori.allItems.contains(part))
                    {
                        log.debug("New collection log item detected removing it..");
                        return true;
                    }
                    break;
                case "Clues":
                    if (config.filterCollectionLogClues() && categori.allItems.contains(part))
                    {
                        log.debug("New collection log item detected removing it..");
                        return true;
                    }
                    break;
                case "Minigames":
                    if (config.filterCollectionLogMinigames() && categori.allItems.contains(part))
                    {
                        log.debug("New collection log item detected removing it..");
                        return true;
                    }
                    break;
                case "Other":
                    if (config.filterCollectionLogOther() && categori.allItems.contains(part))
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

    private boolean filterCombatLevelUp(String message)
    {
        if (config.filterCombatLevelUpMessage() &&
                (message.contains("has reached combat level") || message.contains("highest possible combat level")))
        {
            boolean isMaxCombatMessage = message.contains("highest possible combat level");
            if (isMaxCombatMessage)
            {
                if (config.filterCombatLevelUpThreshold() > 126)
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
                    String combatLevelStr = part.substring(0, part.length() - 1);
                    int combatLevel = Integer.parseInt(combatLevelStr);
                    if (config.filterCombatLevelUpThreshold() > combatLevel)
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

    private boolean filterCombatDiaries(String message)
    {
        if ((config.filterCombatDiaries() && message.contains("Combat Achievement")) ||
                (config.filterCombatDiaryTasks() && message.contains("combat task")))
        {
            log.debug("New combat achievement diaries detected..");
            return processCombatDiaryFiltering(message);
        }

        return false;
    }

    private boolean processCombatDiaryFiltering(String message)
    {
        int index = -1;
        String indexText = "";
        if (config.filterCombatDiaries() && message.contains("Combat Achievement"))
        {
            indexText = "the";
            index = message.indexOf(indexText);
        }
        else if (config.filterCombatDiaryTasks() && message.contains("combat task"))
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
                String combatDiaryLevel = part.substring(0, index2);
                CombatDiariesEnum selectedCombatDiaryThreshold = config.combatDiariesThreshold();
                CombatDiariesEnum messageCombatDiaryLevel = CombatDiariesEnum.valueOf(combatDiaryLevel.toUpperCase());
                if (selectedCombatDiaryThreshold.getId() > messageCombatDiaryLevel.getId())
                {
                    log.debug("Combat Achievement diary threshold was set to: " +
                            selectedCombatDiaryThreshold +
                            " and the incoming Combat Achievement diary was: " +
                            messageCombatDiaryLevel +
                            " removing it..");
                    return true;
                }
            }
            else
            {
                log.debug("Combat Achievement Diary detected removing it..");
                return true;
            }
        }

        return false;
    }

    private boolean filterAchievementDiaries(String message)
    {
        if (config.filterAchievementDiaries() &&
                (message.contains(EASY) ||
                         message.contains(AchievementDiariesEnum.MEDIUM.toString()) ||
                         message.contains(AchievementDiariesEnum.HARD.toString()) ||
                         message.contains(AchievementDiariesEnum.ELITE.toString())))
        {
            log.debug("New achievement diary detected..");
            String textToFind = "the";
            int index = message.indexOf(textToFind);
            if (index != -1)
            {
                String part = message.substring(index + textToFind.length() + 1);
                int index2 = part.indexOf(" ");
                if (index2 != -1)
                {
                    String achievementDiaryLevel = part.substring(0, index2);
                    AchievementDiariesEnum achievementDiaryThreshold = config.achievementDiariesThreshold();
                    AchievementDiariesEnum messageAchievementDiaryLevel = AchievementDiariesEnum.valueOf(achievementDiaryLevel.toUpperCase());
                    if (achievementDiaryThreshold.getId() > messageAchievementDiaryLevel.getId())
                    {
                        log.debug("Achievement diary threshold was set to: " +
                                achievementDiaryThreshold +
                                " and the incoming achievement diary was: " +
                                messageAchievementDiaryLevel +
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