package com.GuildSpamFilter;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class GuildSpamFilterTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(GuildSpamFilterPlugin.class);
		RuneLite.main(args);
	}
}