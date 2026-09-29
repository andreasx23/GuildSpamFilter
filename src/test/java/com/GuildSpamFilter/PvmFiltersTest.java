package com.GuildSpamFilter;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.when;

public class PvmFiltersTest extends FilterTestBase
{
    private static final String TWISTED_BOW_RAID_DROP = "Biceps Btw received special loot from a raid: Twisted bow (1,500,000,000 coins).";
    private static final String SCROLL_RAID_DROP = "Biceps Btw received special loot from a raid: Dexterous prayer scroll (20,000,000 coins).";

    private static final String WHIP_DROP = "Biceps Btw received a drop: Abyssal whip (1,500,000 coins).";
    private static final String BONES_DROP = "Biceps Btw received a drop: Dragon bones (2,500 coins).";

    @Before
    public void setGrandExchangePrices()
    {
        // The raid loot filter looks up the item's price instead of reading it from the broadcast
        setPrice("Twisted bow", 1_500_000_000L);
        setPrice("Dexterous prayer scroll", 20_000_000L);
        setPrice("Scythe of vitur (uncharged)", 1_000_000_000L);
        setPrice("Metamorphic dust", 5_000_000L);
    }

    private void setPrice(String itemName, long price)
    {
        when(itemManager.getItemPrice(collectionLog.itemId(itemName))).thenReturn(price);
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

    @Test
    public void coversEveryItemOnTheCollectionLogRaidsTab()
    {
        when(config.filterRaidDrop()).thenReturn(true);
        when(config.raidLootGpThreshold()).thenReturn(1_000_000_000);

        assertHidden("Biceps Btw received special loot from a raid: Metamorphic dust (5,000,000 coins).");
    }

    @Test
    public void leavesItemsFromOtherCollectionLogTabsAlone()
    {
        when(config.filterRaidDrop()).thenReturn(true);

        assertShown("Biceps Btw received special loot from a raid: Abyssal whip (1,500,000 coins).");
    }

    @Test
    public void usesTheCurrentPriceWhenTheBroadcastArrives()
    {
        when(config.filterRaidDrop()).thenReturn(true);
        when(config.raidLootGpThreshold()).thenReturn(1_000_000_000);

        setPrice("Twisted bow", 900_000_000L);
        assertHidden(TWISTED_BOW_RAID_DROP);

        setPrice("Twisted bow", 1_100_000_000L);
        assertShown(TWISTED_BOW_RAID_DROP);
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
