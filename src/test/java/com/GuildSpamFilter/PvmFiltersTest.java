package com.GuildSpamFilter;

import net.runelite.api.ItemComposition;
import net.runelite.http.api.item.ItemPrice;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PvmFiltersTest extends FilterTestBase
{
    private static final String TWISTED_BOW_RAID_DROP = "Biceps Btw received special loot from a raid: Twisted bow (1,500,000,000 coins).";
    private static final String SCROLL_RAID_DROP = "Biceps Btw received special loot from a raid: Dexterous prayer scroll (20,000,000 coins).";
    // Seen in an in-game screenshot, October 2026
    private static final String CABBAGE_RAID_DROP = "Smallman26 received special loot from a raid: Cabbage.";
    private static final String FRAGMENT_RAID_DROP = "Uganda Ulrik received special loot from a raid: Fragment of whispering wind.";

    private static final String CABBAGE = "Cabbage";
    private static final String FRAGMENT = "Fragment of whispering wind";

    private static final String WHIP_DROP = "Biceps Btw received a drop: Abyssal whip (1,500,000 coins).";
    private static final String BONES_DROP = "Biceps Btw received a drop: Dragon bones (2,500 coins).";

    // The Grand Exchange's tradeable items, which ItemManager.search looks through
    private final List<ItemPrice> tradeableItems = new ArrayList<>();

    @Before
    public void setGrandExchangePrices()
    {
        // The raid loot filter looks up the item's price instead of reading it from the broadcast
        setPrice("Twisted bow", 1_500_000_000L);
        setPrice("Dexterous prayer scroll", 20_000_000L);
        setPrice("Scythe of vitur (uncharged)", 1_000_000_000L);
        setPrice("Metamorphic dust", 5_000_000L);
        setPrice("Ascension shards", 10_000L);

        // Like the real search, this finds every item whose name contains the text
        when(itemManager.search(anyString())).thenAnswer(invocation ->
        {
            String text = invocation.<String>getArgument(0).toLowerCase();
            List<ItemPrice> results = new ArrayList<>(tradeableItems);
            results.removeIf(item -> !item.getName().toLowerCase().contains(text));
            return results;
        });
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
    public void readsRaidLootWithoutACoinValue()
    {
        // Raid broadcasts haven't always included the coin value
        when(config.filterRaidDrop()).thenReturn(true);
        when(config.raidLootGpThreshold()).thenReturn(2_000_000_000);

        assertHidden("Biceps Btw received special loot from a raid: Twisted bow.");
        assertHidden("Biceps Btw received special loot from a raid: Scythe of vitur (uncharged).");
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

    @Test
    public void readsVeryValuableRaidLoot()
    {
        // Seen in an in-game screenshot, October 2026: "...from a raid: Dragon plateskirt (very valuable)."
        when(config.filterRaidDrop()).thenReturn(true);
        when(config.raidLootGpThreshold()).thenReturn(1_200_000_000);

        assertShown("Biceps Btw received special loot from a raid: Twisted bow (very valuable).");
        assertHidden("Biceps Btw received special loot from a raid: Dexterous prayer scroll (very valuable).");
        assertHidden("Biceps Btw received special loot from a raid: Scythe of vitur (uncharged) (very valuable).");
    }

    @Test
    public void multipliesThePriceByTheQuantity()
    {
        // Seen in an in-game screenshot, October 2026: "...from a raid: 884 x Iron ore (very valuable)."
        when(config.filterRaidDrop()).thenReturn(true);
        when(config.raidLootGpThreshold()).thenReturn(5_000_000);

        assertShown("Biceps Btw received special loot from a raid: 1,000 x Ascension shards (very valuable).");
        assertHidden("Biceps Btw received special loot from a raid: 400 x Ascension shards (very valuable).");
    }

    @Test
    public void hidesRaidLootWorthMoreThanTheHighestThresholdWithTheDefault()
    {
        when(config.filterRaidDrop()).thenReturn(true);
        setPrice("Twisted bow", 3_000_000_000L);

        assertHidden(TWISTED_BOW_RAID_DROP);
    }

    @Test
    public void usesTheBroadcastValueForRaidLootWithoutAPrice()
    {
        // Nothing has a Grand Exchange price before RuneLite has fetched prices
        when(config.filterRaidDrop()).thenReturn(true);
        when(config.raidLootGpThreshold()).thenReturn(1_000_000_000);
        setPrice("Twisted bow", 0L);

        assertShown(TWISTED_BOW_RAID_DROP);
        assertHidden("Biceps Btw received special loot from a raid: Twisted bow (900,000,000 coins).");
    }

    @Test
    public void hidesRaidLootWithoutAPriceOrABroadcastValue()
    {
        when(config.filterRaidDrop()).thenReturn(true);
        when(config.raidLootGpThreshold()).thenReturn(1);
        setPrice("Twisted bow", 0L);

        assertHidden("Biceps Btw received special loot from a raid: Twisted bow.");
    }

    // Cabbage raid loot, a joke broadcast from The Fractured Archive

    @Test
    public void hidesCabbageRaidLootEvenWhenRaidDropsAreShown()
    {
        when(config.filterCabbageRaidLoot()).thenReturn(true);

        assertHidden(CABBAGE_RAID_DROP);
        assertShown(TWISTED_BOW_RAID_DROP);
    }

    @Test
    public void raidDropFilterJudgesCabbagesByTheirHighAlchValue()
    {
        setTradeablePrice(1965, CABBAGE, 20L);
        setItem(1965, true, 0);
        when(config.filterRaidDrop()).thenReturn(true);

        when(config.raidLootGpThreshold()).thenReturn(1);
        assertHidden(CABBAGE_RAID_DROP);

        when(config.raidLootGpThreshold()).thenReturn(0);
        assertShown(CABBAGE_RAID_DROP);
    }

    @Test
    public void raidDropFilterHidesCabbagesItCantFind()
    {
        when(config.filterRaidDrop()).thenReturn(true);
        when(config.raidLootGpThreshold()).thenReturn(1);

        assertHidden(CABBAGE_RAID_DROP);
    }

    // Untradeable raid loot, like The Fractured Archive's fragments

    @Test
    public void hidesUntradeableRaidLootEvenWhenRaidDropsAreShown()
    {
        setItem(collectionLog.itemId(FRAGMENT), false, 18_000);
        when(config.filterUntradeableRaidLoot()).thenReturn(true);

        assertHidden(FRAGMENT_RAID_DROP);
        assertShown(TWISTED_BOW_RAID_DROP);
        // Only items on the collection log's Raids tab are known to be untradeable or not
        assertShown(CABBAGE_RAID_DROP);
    }

    @Test
    public void raidDropFilterJudgesUntradeableRaidLootByItsHighAlchValue()
    {
        setItem(collectionLog.itemId(FRAGMENT), false, 18_000);
        when(config.filterRaidDrop()).thenReturn(true);

        when(config.raidLootGpThreshold()).thenReturn(18_000);
        assertShown(FRAGMENT_RAID_DROP);

        when(config.raidLootGpThreshold()).thenReturn(18_001);
        assertHidden(FRAGMENT_RAID_DROP);
    }

    @Test
    public void cabbageSettingHidesCabbagesWhateverTheirHighAlchValue()
    {
        setTradeablePrice(1965, CABBAGE, 20L);
        setItem(1965, true, 0);
        when(config.filterRaidDrop()).thenReturn(true);
        when(config.raidLootGpThreshold()).thenReturn(0);
        assertShown(CABBAGE_RAID_DROP);

        when(config.filterCabbageRaidLoot()).thenReturn(true);
        assertHidden(CABBAGE_RAID_DROP);
    }

    @Test
    public void untradeableSettingHidesUntradeablesWhateverTheirHighAlchValue()
    {
        setItem(collectionLog.itemId(FRAGMENT), false, 18_000);
        when(config.filterRaidDrop()).thenReturn(true);
        when(config.raidLootGpThreshold()).thenReturn(10_000);
        assertShown(FRAGMENT_RAID_DROP);

        when(config.filterUntradeableRaidLoot()).thenReturn(true);
        assertHidden(FRAGMENT_RAID_DROP);
    }

    private void setItem(int itemId, boolean tradeable, int highAlchValue)
    {
        ItemComposition item = mock(ItemComposition.class);
        when(item.isTradeable()).thenReturn(tradeable);
        when(item.getHaPrice()).thenReturn(highAlchValue);
        when(itemManager.getItemComposition(itemId)).thenReturn(item);
    }

    @Test
    public void hidesCabbageRaidLootWithACoinValue()
    {
        when(config.filterCabbageRaidLoot()).thenReturn(true);

        assertHidden("Biceps Btw received special loot from a raid: Cabbage (1 coins).");
        assertHidden("Biceps Btw received special loot from a raid: Cabbage (very valuable).");
        assertHidden("Biceps Btw received special loot from a raid: 3 x Cabbage (very valuable).");
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
    public void usesTheGrandExchangePriceForVeryValuableDrops()
    {
        // Raid loot has been broadcast as "(very valuable)" instead of a coin value, so drops might be too
        setTradeablePrice(4151, "Abyssal whip", 1_500_000L);
        setTradeablePrice(536, "Dragon bones", 2_500L);
        when(config.filterRegularDrops()).thenReturn(true);
        when(config.lootGpThreshold()).thenReturn(1_000_000);

        assertShown("Biceps Btw received a drop: Abyssal whip (very valuable).");
        assertHidden("Biceps Btw received a drop: Dragon bones (very valuable).");
        assertShown("Biceps Btw received a drop: 1,000 x Dragon bones (very valuable).");
    }

    @Test
    public void hidesVeryValuableDropsWithoutAGrandExchangePrice()
    {
        // The item search also returns items whose names only contain the search text
        setTradeablePrice(22124, "Superior dragon bones", 10_000_000L);
        when(config.filterRegularDrops()).thenReturn(true);
        when(config.lootGpThreshold()).thenReturn(1_000_000);

        assertHidden("Biceps Btw received a drop: Dragon bones (very valuable).");
        assertHidden("Biceps Btw received a drop: Viggora's chainmace (u) (very valuable).");
    }

    private void setTradeablePrice(int itemId, String itemName, long price)
    {
        ItemPrice itemPrice = new ItemPrice();
        itemPrice.setId(itemId);
        itemPrice.setName(itemName);
        tradeableItems.add(itemPrice);
        when(itemManager.getItemPrice(itemId)).thenReturn(price);
    }

    @Test
    public void hidesDropsWorthMoreThanTheHighestThresholdWithTheDefault()
    {
        when(config.filterRegularDrops()).thenReturn(true);

        assertHidden("Biceps Btw received a drop: Twisted bow (3,000,000,000 coins).");
    }

    @Test
    public void hidesDropsWithoutAValue()
    {
        when(config.filterRegularDrops()).thenReturn(true);
        when(config.lootGpThreshold()).thenReturn(1_000_000);

        assertHidden("Biceps Btw received a drop: Onyx bolts (e).");
    }

    @Test
    public void hidesRareDrops()
    {
        when(config.filterRareDrops()).thenReturn(true);

        assertHidden("Biceps Btw received a rare drop: Dragon warhammer.");
    }
}
