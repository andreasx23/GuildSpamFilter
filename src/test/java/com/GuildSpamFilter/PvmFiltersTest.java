package com.GuildSpamFilter;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.when;

public class PvmFiltersTest extends FilterTestBase
{
    private static final int TWISTED_BOW = 20997;
    private static final int DEXTEROUS_PRAYER_SCROLL = 21034;
    private static final int SCYTHE_OF_VITUR = 22486;

    private static final String TWISTED_BOW_RAID_DROP = "Biceps Btw received special loot from a raid: Twisted bow (1,500,000,000 coins).";
    private static final String SCROLL_RAID_DROP = "Biceps Btw received special loot from a raid: Dexterous prayer scroll (20,000,000 coins).";

    private static final String WHIP_DROP = "Biceps Btw received a drop: Abyssal whip (1,500,000 coins).";
    private static final String BONES_DROP = "Biceps Btw received a drop: Dragon bones (2,500 coins).";

    @Before
    public void setGrandExchangePrices()
    {
        // The raid loot filter looks up the item's price instead of reading it from the broadcast
        when(itemManager.getItemPrice(TWISTED_BOW)).thenReturn(1_500_000_000L);
        when(itemManager.getItemPrice(DEXTEROUS_PRAYER_SCROLL)).thenReturn(20_000_000L);
        when(itemManager.getItemPrice(SCYTHE_OF_VITUR)).thenReturn(1_000_000_000L);
    }

    // Raid loot

    @Test
    public void hidesEveryRaidDropWithTheDefaultThreshold()
    {
        when(config.filterRaidDrop()).thenReturn(true);

        assertHidden(TWISTED_BOW_RAID_DROP);
        assertHidden(SCROLL_RAID_DROP);
    }

    @Test
    public void raidLootThresholdKeepsItemsWorthThatMuchOrMore()
    {
        when(config.filterRaidDrop()).thenReturn(true);
        when(config.raidLootGpThreshold()).thenReturn(1_000_000_000);

        assertShown(TWISTED_BOW_RAID_DROP);
        assertHidden(SCROLL_RAID_DROP);
    }

    @Test
    public void handlesRaidItemNamesWithBrackets()
    {
        when(config.filterRaidDrop()).thenReturn(true);
        when(config.raidLootGpThreshold()).thenReturn(1_200_000_000);

        assertHidden("Biceps Btw received special loot from a raid: Scythe of vitur (uncharged) (1,000,000,000 coins).");
    }

    // Regular and rare drops

    @Test
    public void hidesEveryDropWithTheDefaultThreshold()
    {
        when(config.filterRegularDrops()).thenReturn(true);

        assertHidden(WHIP_DROP);
        assertHidden(BONES_DROP);
    }

    @Test
    public void lootThresholdKeepsDropsWorthThatMuchOrMore()
    {
        when(config.filterRegularDrops()).thenReturn(true);
        when(config.lootGpThreshold()).thenReturn(1_000_000);

        assertShown(WHIP_DROP);
        assertHidden(BONES_DROP);
    }

    @Test
    public void handlesDropNamesWithBrackets()
    {
        String onyxBolts = "Biceps Btw received a drop: 10 x Onyx bolts (e) (1,234,567 coins).";
        when(config.filterRegularDrops()).thenReturn(true);

        when(config.lootGpThreshold()).thenReturn(1_000_000);
        assertShown(onyxBolts);

        when(config.lootGpThreshold()).thenReturn(2_000_000);
        assertHidden(onyxBolts);
    }

    @Test
    public void hidesRareDrops()
    {
        when(config.filterRareDrops()).thenReturn(true);

        assertHidden("Biceps Btw received a rare drop: Dragon warhammer.");
    }
}
