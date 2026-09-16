package com.nemonotfound.nemos.inventory.sorting.service;

import com.nemonotfound.nemos.inventory.sorting.helper.SortOrder;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.IdentityHashMap;
import java.util.Map;

public class ItemOrderService {

    public static final ItemOrderService INSTANCE = new ItemOrderService(Minecraft.getInstance());

    private final Minecraft minecraft;

    private ItemOrderService(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    public Map<Item, Integer> getItemOrder() {
        rebuildCreativeTabs();

        var itemOrder = new IdentityHashMap<Item, Integer>();
        var searchItems = CreativeModeTabs.searchTab().getSearchTabDisplayItems().stream()
                .map(ItemStack::getItem)
                .toList();

        for (var index = 0; index < searchItems.size(); index++) {
            itemOrder.putIfAbsent(searchItems.get(index), index);
        }

        if (itemOrder.isEmpty()) {
            var fallback = SortOrder.getSortOrder();
            for (var index = 0; index < fallback.size(); index++) {
                itemOrder.putIfAbsent(fallback.get(index), index);
            }
        }

        return itemOrder;
    }

    private void rebuildCreativeTabs() {
        if (minecraft.player == null || minecraft.level == null) {
            return;
        }

        var showOperatorItems = minecraft.player.canUseGameMasterBlocks()
                && minecraft.options.operatorItemsTab().get();
        CreativeModeTabs.tryRebuildTabContents(
                minecraft.level.enabledFeatures(),
                showOperatorItems,
                minecraft.level.registryAccess()
        );
    }
}
