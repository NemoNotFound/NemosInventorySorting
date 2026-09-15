package com.nemonotfound.nemos.inventory.sorting.service;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.IdentityHashMap;
import java.util.Map;

public class ItemOrderService {

    private static ItemOrderService INSTANCE;

    private final Minecraft minecraft;

    private ItemOrderService(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    public static ItemOrderService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ItemOrderService(Minecraft.getInstance());
        }

        return INSTANCE;
    }

    public Map<Item, Integer> getItemOrder() {
        rebuildCreativeTabContents();

        var itemOrder = new IdentityHashMap<Item, Integer>();
        var searchTabItems = BuiltInRegistries.CREATIVE_MODE_TAB.stream()
                .filter(tab -> tab.getType() == CreativeModeTab.Type.SEARCH)
                .flatMap(tab -> tab.getSearchTabDisplayItems().stream())
                .map(ItemStack::getItem)
                .toList();

        for (int index = 0; index < searchTabItems.size(); index++) {
            itemOrder.putIfAbsent(searchTabItems.get(index), index);
        }

        return itemOrder;
    }

    private void rebuildCreativeTabContents() {
        var player = minecraft.player;

        if (player == null || minecraft.level == null) {
            return;
        }

        var level = minecraft.level;
        var hasPermissions = player.canUseGameMasterBlocks() && minecraft.options.operatorItemsTab().get();
        var parameters = new CreativeModeTab.ItemDisplayParameters(level.enabledFeatures(), hasPermissions, level.registryAccess());

        CreativeModeTabs.buildAllTabContents(parameters);
    }
}
