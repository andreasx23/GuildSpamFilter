package com.GuildSpamFilter;

import com.GuildSpamFilter.Configs.AchievementDiariesEnum;
import com.GuildSpamFilter.Configs.CombatDiariesEnum;
import com.google.gson.Gson;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import javax.inject.Inject;
import java.util.HashSet;

/*
   Shout out to Spam Filter for giving me a baseline on how to implement this simple Clan (broadcast) Spam Filter
   https://github.com/jackriccomini/spamfilter-plugin-runelite/blob/846413d594416195047e5d2ea233dce7a12fd85c/src/main/java/com/jackriccomini/spamfilter/SpamFilterPlugin.java
*/

@Slf4j
@PluginDescriptor(
        name = "Clan Spam Filter",
        tags = { "Spam filter", "Clan spam filter", "Spam", "Filter", "Guild Spam Filter", "Guild", "Clan" },
        description = "Remove broadcasts from clan chat"
)
public class GuildSpamFilterPlugin extends Plugin
{
    @Inject
    private Client client;
    @Inject
    private GuildSpamFilterConfig config;
    private HashSet<String> pbsToIncludeOrExclude;
    private HashSet<String> customFilters;

    @Provides
    GuildSpamFilterConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(GuildSpamFilterConfig.class);
    }

    @Override
    protected void startUp() throws RuntimeException
    {
        log.info("Clan Spam Filter started!");
        pbsToIncludeOrExclude = new HashSet<String>();
        customFilters = new HashSet<String>();
        UpdatePbsToIncludeOrExclude();
        UpdateCustomFilters();
    }

    @Override
    protected void shutDown()
    {
        log.info("Clan Spam Filter stopped!");
        pbsToIncludeOrExclude = null;
        customFilters = null;
    }

    private void UpdatePbsToIncludeOrExclude()
    {
        pbsToIncludeOrExclude.clear();
        String[] values = config.pbsToIncludeOrExclude().split(",");
        if (values.length > 0)
        {
            for (String value: values)
            {
                pbsToIncludeOrExclude.add(value.trim().toLowerCase());
            }
        }
        log.debug("New list: " + String.join(", ", pbsToIncludeOrExclude));
    }

    private void UpdateCustomFilters()
    {
        customFilters.clear();
        String[] values = config.customFilters().split(",");
        if (values.length > 0)
        {
            for (String value: values)
            {
                customFilters.add(value.trim().toLowerCase());
            }
        }
        log.debug("New list: " + String.join(", ", customFilters));
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event)
    {
        if (event.getKey().equals("pbsToIncludeOrExclude"))
        {
            UpdatePbsToIncludeOrExclude();
        }
        else if (event.getKey().equals("customFilters"))
        {
            UpdateCustomFilters();
        }
    }

    @Subscribe
    public void onScriptCallbackEvent(ScriptCallbackEvent event)
    {
        if (!event.getEventName().equals("chatFilterCheck")) return;

        int[] intStack = client.getIntStack();
        int intStackSize = client.getIntStackSize();
        String[] stringStack = client.getStringStack();
        int stringStackSize = client.getStringStackSize();

        if (intStack.length < intStackSize - 3 || stringStack.length < stringStackSize - 1) return;

        final int messageType = intStack[intStackSize - 2];
        ChatMessageType chatMessageType = ChatMessageType.of(messageType);
        if (chatMessageType != ChatMessageType.CLAN_MESSAGE) return;

        String message = stringStack[stringStackSize - 1];

        log.debug("Broadcast message: " + message);

        if (config.filterPb() && message.contains("has achieved a new"))
        {
            if (!pbsToIncludeOrExclude.isEmpty())
            {
                log.debug("PBs to include or exclude was not empty. Mode was set to: " + config.pbToIncludeOrExcludeEnum());
                String lowercaseMessage = message.toLowerCase();
                switch (config.pbToIncludeOrExcludeEnum())
                {
                    case EXCLUDE_ALL_EXCEPT:
                    {
                        boolean found = false;
                        for (String text: pbsToIncludeOrExclude)
                        {
                            if (lowercaseMessage.contains(text))
                            {
                                found = true;
                                break;
                            }
                        }
                        if (found) return;
                        break;
                    }
                    case INCLUDE_ALL_EXCEPT:
                    {
                        boolean found = false;
                        for (String text: pbsToIncludeOrExclude)
                        {
                            if (lowercaseMessage.contains(text))
                            {
                                found = true;
                                break;
                            }
                        }
                        if (!found) return;
                        break;
                    }
                }
            }
            log.debug("New pb detected removing it..");
            intStack[intStackSize - 3] = 0;
        }
        else if (config.filterRaidDrop() && message.contains("received special loot"))
        {
            log.debug("New raid loot detected..");
            int index = message.indexOf("(") + 1;
            int index2 = message.indexOf(")");
            String part = message.substring(index, index2).replace(",", "").replace("coins", "").trim();
            long gpValue = Long.parseLong(part);
            if (gpValue < config.raidLootGpThreshold() || gpValue == Integer.MAX_VALUE && gpValue == config.raidLootGpThreshold())
            {
                log.debug("Raid loot was below threshold: " + gpValue + " removing it..");
                intStack[intStackSize - 3] = 0;
            }
        }
        else if (config.filterRegularDrops() && message.contains("received a drop"))
        {
            log.debug("New drop detected..");
            int index = message.indexOf("(") + 1;
            int index2 = message.indexOf(")");
            String part = message.substring(index, index2).replace(",", "").replace("coins", "").trim();
            long gpValue = Long.parseLong(part);
            if (gpValue < config.lootGpThreshold() || gpValue == Integer.MAX_VALUE && gpValue == config.lootGpThreshold())
            {
                log.debug("Loot was below threshold: " + gpValue + " removing it..");
                intStack[intStackSize - 3] = 0;
            }
        }
        else if (config.filterPets() && (message.contains("has a funny feeling") || message.contains("acquired something special")))
        {
            log.debug("New pet detected removing it..");
            intStack[intStackSize - 3] = 0;
        }
        else if (config.filterTotalLevelMilestone() && message.contains("has reached a total"))
        {
            log.debug("New total level detected removing it..");
            String textToFind = "total level of ";
            int index = message.indexOf(textToFind) + textToFind.length();
            String part = message.substring(index, message.length() - 1);
            long totalLevel = Long.parseLong(part);
            if (totalLevel < config.totalLevelThreshold())
            {
                log.debug("Total Level was below threshold: " + totalLevel + " removing it..");
                intStack[intStackSize - 3] = 0;
            }
        }
        else if (config.filterLevelUp() && message.contains("has reached"))
        {
            log.debug("New level up detected..");
            int index = message.indexOf("level") + 6;
            String part = message.substring(index, message.length() - 1);
            long level = Long.parseLong(part);
            if (level < config.levelThreshold())
            {
                log.debug("Level was below threshold: " + level + " removing it..");
                intStack[intStackSize - 3] = 0;
            }
        }
        else if (config.filterCollectionLog() && message.contains("a new collection log"))
        {
            log.debug("New collection log item detected removing it..");
            intStack[intStackSize - 3] = 0;
        }
        else if (config.filterNewClanMember() && message.contains("has been invited into the"))
        {
            log.debug("New clan member detected removing it..");
            intStack[intStackSize - 3] = 0;
        }
        else if (config.filterXpMilestone() && message.contains("XP in"))
        {
            log.debug("New XP milestone detected..");
            int index = message.indexOf("reached") + 8;
            int index2 = message.indexOf("XP in") - 1;
            String part = message.substring(index, index2).replace(",", "");
            long xp = Long.parseLong(part);
            if (xp < config.xpMilestoneThreshold())
            {
                log.debug("XP milestone was below threshold: " + xp + " removing it..");
                intStack[intStackSize - 3] = 0;
            }
        }
        else if (config.filterDefaultMessage() && message.contains("To talk in your"))
        {
            log.debug("New default message detected removing it..");
            intStack[intStackSize - 3] = 0;
        }
        else if (config.filterRareDrops() && message.contains("received a rare drop"))
        {
            log.debug("New rare drop detected removing it..");
            intStack[intStackSize - 3] = 0;
        }
        else if (config.filterQuestComplete() && message.contains("has completed a quest"))
        {
            log.debug("New quest completion detected removing it..");
            intStack[intStackSize - 3] = 0;
        }
        else if (config.filterHardcoreDeath() && message.contains("and lost their hardcore"))
        {
            log.debug("New hardcore death detected removing it..");
            intStack[intStackSize - 3] = 0;
        }
        else if (config.filterClanMemberKicked() && message.contains("has expelled"))
        {
            log.debug("New kicked clan member detected removing it..");
            intStack[intStackSize - 3] = 0;
        }
//        else if (config.filterPlayerDied() && message.contains("has died")) // I need the correct text to filter by
//        {
//            log.debug("New hardcore death detected removing it..");
//            intStack[intStackSize - 3] = 0;
//        }
//        else if (config.filterPlayerKill() && message.contains("has killed")) // I need the correct text to filter by
//        {
//            log.debug("New hardcore death detected removing it..");
//            intStack[intStackSize - 3] = 0;
//        }
//        else if (config.filterMemberLeftClan() && message.contains("has left")) // I need the correct text to filter by
//        {
//            log.debug("New members who left the clan detected removing it..");
//            intStack[intStackSize - 3] = 0;
//        }
        else if (config.filterCombatDiaries() && message.contains("Combat Achievement"))
        {
            log.debug("New combat achievement diaries detected..");
            int index = message.indexOf("the") + 4;
            String part = message.substring(index);
            int index2 = part.indexOf(" ");
            String combatDiaryLevel = part.substring(0, index2);
            if (config.combatDiariesThreshold() == CombatDiariesEnum.ALL)
            {
                log.debug("Combat Achievement Diaries Threshold was set to: " + config.achievementDiariesThreshold() + "and diary was: " + combatDiaryLevel + " removing it..");
                intStack[intStackSize - 3] = 0;
            }
            else if (config.combatDiariesThreshold() == CombatDiariesEnum.GRANDMASTER &&
                    (combatDiaryLevel.equals(CombatDiariesEnum.MASTER.toString()) ||
                    combatDiaryLevel.equals(CombatDiariesEnum.ELITE.toString()) ||
                            combatDiaryLevel.equals(CombatDiariesEnum.HARD.toString()) ||
                            combatDiaryLevel.equals(CombatDiariesEnum.MEDIUM.toString()) ||
                            combatDiaryLevel.equals("Easy")))
            {
                log.debug("Combat Achievement Diaries Threshold was set to: " + config.achievementDiariesThreshold() + "and diary was: " + combatDiaryLevel + " removing it..");
                intStack[intStackSize - 3] = 0;
            }
            else if (config.combatDiariesThreshold() == CombatDiariesEnum.MASTER &&
                    (combatDiaryLevel.equals(CombatDiariesEnum.ELITE.toString()) ||
                            combatDiaryLevel.equals(CombatDiariesEnum.HARD.toString()) ||
                            combatDiaryLevel.equals(CombatDiariesEnum.MEDIUM.toString()) ||
                            combatDiaryLevel.equals("Easy")))
            {
                log.debug("Combat Achievement Diaries Threshold was set to: " + config.achievementDiariesThreshold() + "and diary was: " + combatDiaryLevel + " removing it..");
                intStack[intStackSize - 3] = 0;
            }
            else if (config.combatDiariesThreshold() == CombatDiariesEnum.ELITE &&
                    (combatDiaryLevel.equals(CombatDiariesEnum.HARD.toString()) ||
                            combatDiaryLevel.equals(CombatDiariesEnum.MEDIUM.toString()) ||
                            combatDiaryLevel.equals("Easy")))
            {
                log.debug("Combat Achievement Diaries Threshold was set to: " + config.achievementDiariesThreshold() + "and diary was: " + combatDiaryLevel + " removing it..");
                intStack[intStackSize - 3] = 0;
            }
            else if (config.combatDiariesThreshold() == CombatDiariesEnum.HARD &&
                    (combatDiaryLevel.equals(CombatDiariesEnum.MEDIUM.toString()) ||
                            combatDiaryLevel.equals("Easy")))
            {
                log.debug("Combat Achievement Diaries Threshold was set to: " + config.achievementDiariesThreshold() + "and diary was: " + combatDiaryLevel + " removing it..");
                intStack[intStackSize - 3] = 0;
            }
            else if (config.combatDiariesThreshold() == CombatDiariesEnum.MEDIUM &&
                    (combatDiaryLevel.equals("Easy")))
            {
                log.debug("Combat Achievement Diaries Threshold was set to: " + config.achievementDiariesThreshold() + "and diary was: " + combatDiaryLevel + " removing it..");
                intStack[intStackSize - 3] = 0;
            }
        }
        else if (config.filterAchievementDiaries() &&
                (message.contains("Easy") ||
                        message.contains(AchievementDiariesEnum.MEDIUM.toString()) ||
                        message.contains(AchievementDiariesEnum.HARD.toString()) ||
                        message.contains(AchievementDiariesEnum.ELITE.toString())))
        {
            log.debug("New achievement diaries detected..");
            int index = message.indexOf("the") + 4;
            String part = message.substring(index);
            int index2 = part.indexOf(" ");
            String achievementDiaryLevel = part.substring(0, index2);
            if (config.achievementDiariesThreshold() == AchievementDiariesEnum.ALL)
            {
                log.debug("Achievement Diaries Threshold was set to: " + config.achievementDiariesThreshold() + "and diary was: " + achievementDiaryLevel + " removing it..");
                intStack[intStackSize - 3] = 0;
            }
            else if (config.achievementDiariesThreshold() == AchievementDiariesEnum.ELITE &&
                    (achievementDiaryLevel.equals("Easy") ||
                            achievementDiaryLevel.equals(AchievementDiariesEnum.MEDIUM.toString()) ||
                            achievementDiaryLevel.equals(AchievementDiariesEnum.HARD.toString())))
            {
                log.debug("Achievement Diaries Threshold was set to: " + config.achievementDiariesThreshold() + "and diary was: " + achievementDiaryLevel + " removing it..");
                intStack[intStackSize - 3] = 0;
            }
            else if (config.achievementDiariesThreshold() == AchievementDiariesEnum.HARD &&
                    (achievementDiaryLevel.equals("Easy") ||
                            achievementDiaryLevel.equals(AchievementDiariesEnum.MEDIUM.toString())))
            {
                log.debug("Achievement Diaries Threshold was set to: " + config.achievementDiariesThreshold() + "and diary was: " + achievementDiaryLevel + " removing it..");
                intStack[intStackSize - 3] = 0;
            }
            else if (config.achievementDiariesThreshold() == AchievementDiariesEnum.MEDIUM &&
                    (achievementDiaryLevel.equals("Easy")))
            {
                log.debug("Achievement Diaries Threshold was set to: " + config.achievementDiariesThreshold() + "and diary was: " + achievementDiaryLevel + " removing it..");
                intStack[intStackSize - 3] = 0;
            }
        }
        else if (!customFilters.isEmpty())
        {
            log.debug("Custom filter was not empty scanning..");
            String lowercaseMessage = message.toLowerCase();
            boolean found = false;
            for (String text: customFilters)
            {
                if (lowercaseMessage.contains(text))
                {
                    found = true;
                    break;
                }
            }

            if (found)
            {
                log.debug("Custom filter match found removing it..");
                intStack[intStackSize - 3] = 0;
            }
        }
        stringStack[stringStackSize - 1] = message;
    }
}
