package com.nemonotfound.nemos.inventory.sorting.service;

import com.nemonotfound.nemos.inventory.sorting.models.ItemSorting;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.joining;

public class ItemSortingFactory {

    private static final int MAX_CONTAINER_DEPTH = 16;

    public static final ItemSortingFactory INSTANCE = new ItemSortingFactory(TooltipService.INSTANCE);

    private final TooltipService tooltipService;

    ItemSortingFactory(TooltipService tooltipService) {
        this.tooltipService = tooltipService;
    }

    public ItemSorting create(ItemStack itemStack, Map<Item, Integer> itemOrder) {
        var tooltipComponents = tooltipService.retrieveTooltipLines(itemStack);

        return new ItemSorting(
                itemOrder.getOrDefault(itemStack.getItem(), Integer.MAX_VALUE),
                itemStack.getCount(),
                itemStack.getItemName().getString(),
                tooltipService.retrieveEnchantmentNames(tooltipComponents),
                tooltipService.retrieveJukeboxSongName(tooltipComponents),
                tooltipService.retrievePotionName(tooltipComponents),
                componentDataSortKey(itemStack.getComponents())
        );
    }

    static String componentDataSortKey(DataComponentMap components) {
        return componentDataSortKey(
                components,
                item -> BuiltInRegistries.ITEM.getKey(item).toString()
        );
    }

    static String componentDataSortKey(DataComponentMap components, Function<Item, String> itemIdProvider) {
        return componentDataSortKey(components, 0, itemIdProvider);
    }

    private static String componentDataSortKey(
            DataComponentMap components,
            int containerDepth,
            Function<Item, String> itemIdProvider
    ) {
        return components.stream()
                .map(component -> component.type() + "=>" + componentValueSortKey(component.value(), containerDepth, itemIdProvider))
                .sorted()
                .collect(joining("|"));
    }

    private static String componentValueSortKey(Object value, int containerDepth, Function<Item, String> itemIdProvider) {
        if (value instanceof ItemContainerContents contents) {
            return containerContentsSortKey(contents, containerDepth, itemIdProvider);
        }

        return String.valueOf(value);
    }

    private static String containerContentsSortKey(
            ItemContainerContents contents,
            int containerDepth,
            Function<Item, String> itemIdProvider
    ) {
        if (containerDepth >= MAX_CONTAINER_DEPTH) {
            return "<max-depth>";
        }

        var itemStacks = contents.itemCopies().toList();

        return IntStream.range(0, itemStacks.size())
                .mapToObj(slot -> slot + ":" + containedItemSortKey(itemStacks.get(slot), containerDepth + 1, itemIdProvider))
                .collect(joining(",", "[", "]"));
    }

    private static String containedItemSortKey(
            ItemStack itemStack,
            int containerDepth,
            Function<Item, String> itemIdProvider
    ) {
        if (itemStack.isEmpty()) {
            return "_";
        }

        return itemIdProvider.apply(itemStack.getItem())
                + "*" + itemStack.getCount()
                + "{" + componentDataSortKey(itemStack.getComponents(), containerDepth, itemIdProvider) + "}";
    }
}
