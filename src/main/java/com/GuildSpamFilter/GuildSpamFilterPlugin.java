package com.GuildSpamFilter;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import javax.inject.Inject;
import java.util.HashSet;

/*
   Shoutout to Spam Filter for giving me a baseline on how to implement this
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
    private HashSet<String> seenMessages;

    @Provides
    GuildSpamFilterConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(GuildSpamFilterConfig.class);
    }

    @Override
    protected void startUp() throws RuntimeException
    {
        seenMessages = new HashSet<String>();
        log.info("Clan Spam Filter started!");
    }

    @Override
    protected void shutDown()
    {
        seenMessages = null;
        log.info("Clan Spam Filter stopped!");
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
        //if (!seenMessages.add(message)) return;

        log.info("Broadcast message: " + message);

        if (config.filterPb() && message.contains("has achieved a new"))
        {
            log.info("New pb detected removing it..");
            intStack[intStackSize - 3] = 0;
        }
        else if (config.filterRaidDrop() && message.contains("received special loot"))
        {
            log.info("New raid loot detected removing it..");
            intStack[intStackSize - 3] = 0;
        }
        else if (config.filterRegularDrops() && message.contains("received a drop"))
        {
            log.info("New drop detected removing it..");
            intStack[intStackSize - 3] = 0;
        }
        else if (config.filterPets() && (message.contains("has a funny feeling") || message.contains("acquired something special")))
        {
            log.info("New pet detected removing it..");
            intStack[intStackSize - 3] = 0;
        }
        else if (config.filterTotalLevelMilestone() && message.contains("has reached"))
        {
            log.info("New level up detected removing it..");
            intStack[intStackSize - 3] = 0;
        }
        else if (config.filterCollectionLog() && message.contains("received new collection"))
        {
            log.info("New collection log item detected removing it..");
            intStack[intStackSize - 3] = 0;
        }
        else if (config.filterLevelUp() && message.contains("has reached a total"))
        {
            log.info("New total level detected removing it..");
            intStack[intStackSize - 3] = 0;
        }
        else if (config.filterLevelUp() && message.contains("has reached a total"))
        {
            log.info("New total level detected removing it..");
            intStack[intStackSize - 3] = 0;
        }
        else if (config.filterNewClanMember() && message.contains("has been invited into the"))
        {
            log.info("New clan member detected removing it..");
            intStack[intStackSize - 3] = 0;
        }
        stringStack[stringStackSize - 1] = message;
    }
}
