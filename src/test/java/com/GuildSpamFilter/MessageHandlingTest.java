package com.GuildSpamFilter;

import net.runelite.api.ChatMessageType;
import net.runelite.api.events.ScriptCallbackEvent;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MessageHandlingTest extends FilterTestBase
{
    private static final String[] ONE_OF_EACH_BROADCAST = {
            "Biceps Btw has achieved a new Vorkath personal best: 1:05.40",
            "Biceps Btw has a funny feeling like they're being followed: Vorki at 50 killcount.",
            "Store Biceps has been invited into the clan by Biceps Btw.",
            "Biceps Btw has expelled Store Biceps from the clan.",
            "Biceps Btw has completed a quest: Dragon Slayer II",
            "Biceps Btw has completed the Hard Ardougne diary.",
            "Biceps Btw has unlocked the Elite tier of rewards from Combat Achievements!",
            "Biceps Btw has completed an Elite combat task: Perfect Zulrah.",
            "Biceps Btw has died and lost their hardcore ironman status!",
            "Biceps Btw has reached combat level 100.",
            "To talk in your clan's channel, start each line of chat with // or /c.",
            "Biceps Btw received a new collection log item: Abyssal whip (812/1666)",
            "Biceps Btw has reached Fishing level 90.",
            "Biceps Btw has reached 50,000,000 XP in Fishing.",
            "Biceps Btw has reached a total level of 2000.",
            "Biceps Btw has reached the highest possible total level of 2376.",
            "Biceps Btw received special loot from a raid: Twisted bow (1,500,000,000 coins).",
            "Biceps Btw received a drop: Abyssal whip (1,500,000 coins).",
            "Biceps Btw received a rare drop: Dragon warhammer.",
            "Biceps Btw has defeated Store Biceps and received (1,234,567 coins) worth of loot!",
            "Biceps Btw has been defeated by Store Biceps and lost (1,234,567 coins) worth of loot.",
            "<img=22>Biceps Btw has unlocked the Tier 3 relic.",
    };

    @Test
    public void nothingIsHiddenUntilAFilterIsTurnedOn()
    {
        for (String broadcast : ONE_OF_EACH_BROADCAST)
        {
            assertShown(broadcast);
        }
    }

    @Test
    public void messagesTypedByClanmatesAreNeverHidden()
    {
        setCustomFilters("whip");

        assertFalse(isHidden("I just got an abyssal whip!", ChatMessageType.CLAN_CHAT));
    }

    @Test
    public void ignoresOtherChatScriptEvents()
    {
        ScriptCallbackEvent event = new ScriptCallbackEvent();
        event.setEventName("someOtherEvent");
        plugin.onScriptCallbackEvent(event);

        verify(client, never()).getIntStack();
    }

    @Test
    public void startingThePluginRefiltersChat()
    {
        assertChatWasRefiltered();
    }

    @Test
    public void stoppingThePluginRefiltersChat()
    {
        clearInvocations(clientThread);

        plugin.shutDown();

        assertChatWasRefiltered();
    }

    @Test
    public void changingASettingRefiltersChat()
    {
        clearInvocations(clientThread);

        changeSetting("filterPets");

        assertChatWasRefiltered();
    }

    @Test
    public void ignoresSettingsChangedByOtherPlugins()
    {
        clearInvocations(clientThread);
        when(config.customFilters()).thenReturn("whip");

        changeSetting("someotherplugin", "customFilters");

        verify(clientThread, never()).invoke(any(Runnable.class));
        assertShown("Biceps Btw received a drop: Abyssal whip (1,500,000 coins).");
    }

    private void assertChatWasRefiltered()
    {
        ArgumentCaptor<Runnable> task = ArgumentCaptor.forClass(Runnable.class);
        verify(clientThread).invoke(task.capture());

        task.getValue().run();
        verify(client).refreshChat();
    }
}
