package com.GuildSpamFilter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.google.inject.Guice;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.ItemComposition;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import org.junit.After;
import org.junit.Before;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.function.BooleanSupplier;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Runs the real plugin with the game client, item prices, collection log and config replaced by fakes,
 * and sends broadcasts through it the same way the game's chat script does.
 */
public abstract class FilterTestBase
{
    protected Client client;
    protected ClientThread clientThread;
    protected ItemManager itemManager;
    protected GuildSpamFilterConfig config;
    protected FakeCollectionLog collectionLog;
    protected GuildSpamFilterPlugin plugin;
    // Everything the plugin logs is at debug level. These messages mean something went wrong, so a test fails if the
    // plugin logs one that the test didn't expect with assertLoggedProblem
    private static final List<String> PROBLEMS = Arrays.asList(
            "Couldn't check broadcast",
            "Ignoring custom regex filter",
            "Unknown collection log tab",
            "Found no raid items");

    private ListAppender<ILoggingEvent> pluginLogs;

    @Before
    public void startPlugin() throws Exception
    {
        client = mock(Client.class);
        clientThread = mock(ClientThread.class);
        itemManager = mock(ItemManager.class);
        // Every item is tradeable, with no Grand Exchange price or high-alch value, unless a test changes it
        ItemComposition tradeableItem = mock(ItemComposition.class);
        when(tradeableItem.isTradeable()).thenReturn(true);
        when(itemManager.getItemComposition(anyInt())).thenReturn(tradeableItem);
        // Every setting keeps its real default value unless a test changes it
        config = mock(GuildSpamFilterConfig.class, CALLS_REAL_METHODS);

        when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
        // Run client thread tasks straight away, like the client does when it's already on the client thread
        doAnswer(invocation -> ((BooleanSupplier) invocation.getArgument(0)).getAsBoolean())
                .when(clientThread).invoke(any(BooleanSupplier.class));
        doAnswer(invocation ->
        {
            ((Runnable) invocation.getArgument(0)).run();
            return null;
        }).when(clientThread).invoke(any(Runnable.class));

        // The plugin shows broadcasts it fails to read and logs the problem instead of throwing, so catch those here
        pluginLogs = new ListAppender<>();
        pluginLogs.start();
        pluginLogger().addAppender(pluginLogs);
        // Everything the plugin logs is at debug level, which the logging setup on the classpath would drop
        pluginLogger().setLevel(Level.DEBUG);

        collectionLog = new FakeCollectionLog()
                .tab("Bosses").page("Abyssal Sire", "Abyssal orphan", "Abyssal whip")
                .tab("Raids")
                .page("Chambers of Xeric", "Olmlet", "Twisted bow", "Dexterous prayer scroll", "Metamorphic dust")
                .page("Theatre of Blood", "Scythe of vitur (uncharged)")
                .page("The Fractured Archive", "Ascension shards", "Fragment of whispering wind", "Archivist's folio (inert)")
                .tab("Clues")
                .page("Beginner Treasure Trails", "Mole slippers")
                .page("Easy Treasure Trails", "Blue skirt (g)")
                .page("Hard Treasure Trails", "3rd age amulet")
                .tab("Minigames").page("Barbarian Assault", "Fighter hat")
                .tab("Other").page("Aerial Fishing", "Golden tench");
        collectionLog.installOn(client);

        plugin = new GuildSpamFilterPlugin();
        Guice.createInjector(binder ->
        {
            binder.bind(Client.class).toInstance(client);
            binder.bind(ClientThread.class).toInstance(clientThread);
            binder.bind(ItemManager.class).toInstance(itemManager);
            binder.bind(GuildSpamFilterConfig.class).toInstance(config);
        }).injectMembers(plugin);

        plugin.startUp();
    }

    @After
    public void failOnUnexpectedProblems()
    {
        pluginLogger().detachAppender(pluginLogs);
        pluginLogger().setLevel(null);
        for (ILoggingEvent event : pluginLogs.list)
        {
            String message = event.getFormattedMessage();
            if (event.getLevel().isGreaterOrEqual(Level.INFO))
            {
                fail("Plugins may only log at debug level, but this was logged at " + event.getLevel() + ": " + message);
            }

            for (String problem : PROBLEMS)
            {
                if (message.startsWith(problem))
                {
                    fail("Unexpected problem: " + message);
                }
            }
        }
    }

    /** Checks the plugin logged a problem starting with this text, and marks it as expected. */
    protected void assertLoggedProblem(String text)
    {
        boolean logged = pluginLogs.list.removeIf(event -> event.getFormattedMessage().startsWith(text));
        assertTrue("Expected a logged problem starting with: " + text, logged);
    }

    private static Logger pluginLogger()
    {
        return (Logger) LoggerFactory.getLogger(GuildSpamFilterPlugin.class);
    }

    protected boolean isHidden(String broadcast)
    {
        return isHidden(broadcast, ChatMessageType.CLAN_MESSAGE);
    }

    protected boolean isHidden(String message, ChatMessageType type)
    {
        // The chat filter script's stacks: [show message (1 or 0), message type, message id] and [message]
        int[] intStack = {1, type.getType(), 0};
        Object[] objectStack = {message};
        when(client.getIntStack()).thenReturn(intStack);
        when(client.getIntStackSize()).thenReturn(intStack.length);
        when(client.getObjectStack()).thenReturn(objectStack);
        when(client.getObjectStackSize()).thenReturn(objectStack.length);

        ScriptCallbackEvent event = new ScriptCallbackEvent();
        event.setEventName("chatFilterCheck");
        plugin.onScriptCallbackEvent(event);

        return intStack[0] == 0;
    }

    protected void assertHidden(String broadcast)
    {
        assertTrue("Expected this broadcast to be hidden: " + broadcast, isHidden(broadcast));
    }

    protected void assertShown(String broadcast)
    {
        assertFalse("Expected this broadcast to be shown: " + broadcast, isHidden(broadcast));
    }

    // Every way a user might separate two entries in a list setting: with or without spaces, tabs or non-breaking
    // spaces before and after them, and with new lines
    protected static List<String> listsOf(String first, String second)
    {
        return Arrays.asList(
                first + "," + second,
                first + ", " + second,
                first + ",\n" + second,
                first + ", \n" + second,
                first + ",\r\n" + second,
                first + "\n" + second,
                first + "\n\n" + second + "\n",
                " " + first + " ,  " + second + " ,",
                "    " + first + ", " + second + "    ",
                "\t" + first + ",\t" + second + "\t",
                " " + first + ", " + second);
    }

    // A list as the user typed it, for assertion messages
    protected static String describeList(String list)
    {
        return "\"" + list.replace("\r", "\\r")
                          .replace("\n", "\\n")
                          .replace("\t", "\\t")
                          .replace(" ", "\\u00A0") + "\"";
    }

    protected void setPersonalBestList(String value)
    {
        when(config.personalBestsToIncludeOrExclude()).thenReturn(value);
        changeSetting("pbsToIncludeOrExclude");
    }

    protected void setCustomFilters(String value)
    {
        when(config.customFilters()).thenReturn(value);
        changeSetting("customFilters");
    }

    protected void setCustomRegexFilters(String value)
    {
        when(config.customRegexFilters()).thenReturn(value);
        changeSetting("customRegexFilters");
    }

    protected void setAlwaysIncludedPlayers(String value)
    {
        when(config.alwaysIncludedPlayerNames()).thenReturn(value);
        changeSetting("excludedPlayerNames");
    }

    protected void changeSetting(String key)
    {
        changeSetting(GuildSpamFilterConfig.GROUP, key);
    }

    protected void changeSetting(String group, String key)
    {
        ConfigChanged event = new ConfigChanged();
        event.setGroup(group);
        event.setKey(key);
        plugin.onConfigChanged(event);
    }
}
