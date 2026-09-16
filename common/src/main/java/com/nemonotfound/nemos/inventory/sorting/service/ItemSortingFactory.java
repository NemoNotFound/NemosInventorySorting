package com.nemonotfound.nemos.inventory.sorting.service;

import com.nemonotfound.nemos.inventory.sorting.model.ItemSorting;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

public class ItemSortingFactory {

    public static final ItemSortingFactory INSTANCE = new ItemSortingFactory(TooltipService.getInstance());

    private final TooltipService tooltipService;

    private ItemSortingFactory(TooltipService tooltipService) {
        this.tooltipService = tooltipService;
    }

    public ItemSorting create(ItemStack itemStack, Map<Item, Integer> itemOrder) {
        var tooltip = tooltipService.retrieveTooltipLines(itemStack);

        return new ItemSorting(
                itemOrder.getOrDefault(itemStack.getItem(), Integer.MAX_VALUE),
                itemStack.getCount(),
                itemStack.getHoverName().getString(),
                tooltipService.retrieveEnchantmentNames(tooltip),
                tooltipService.retrieveJukeboxSongName(tooltip),
                tooltipService.retrievePotionName(tooltip),
                String.valueOf(itemStack.getTag())
        );
    }
}
