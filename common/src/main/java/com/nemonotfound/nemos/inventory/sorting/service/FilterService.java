package com.nemonotfound.nemos.inventory.sorting.service;

import com.nemonotfound.nemos.inventory.sorting.model.FilterResult;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FilterService {

    private static FilterService INSTANCE;
    private final TooltipService tooltipService;

    public static FilterService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FilterService(TooltipService.getInstance());
        }

        return INSTANCE;
    }

    public FilterService(TooltipService tooltipService) {
        this.tooltipService = tooltipService;
    }

    public Map<FilterResult, List<Slot>> filterSlots(NonNullList<Slot> slots, String filter) {
        return slots.stream()
                .collect(Collectors.groupingBy(slot -> filterSlot(slot, filter)));
    }

    private FilterResult filterSlot(Slot slot, String filter) {
        var slotItem = slot.getItem();

        if (slotItem.is(Items.AIR)) {
            return FilterResult.EXCLUDED;
        }

        if (bundleContentsMatchFilter(slotItem, filter) || itemContainerContentsMatchFilter(slotItem, filter)) {
            return FilterResult.HAS_INCLUDED_ITEM;
        }

        return matchesFilter(slotItem, filter) ? FilterResult.INCLUDED : FilterResult.EXCLUDED;
    }

    private boolean bundleContentsMatchFilter(ItemStack itemStack, String filter) {
        if (!itemStack.has(DataComponents.BUNDLE_CONTENTS)) {
            return false;
        }

        var bundleContents = itemStack.get(DataComponents.BUNDLE_CONTENTS);
        var stream = bundleContents != null ? bundleContents.itemCopyStream() : Stream.<ItemStack>builder().build();

        return contentsMatchFilter(stream, filter);
    }

    private boolean itemContainerContentsMatchFilter(ItemStack itemStack, String filter) {
        if (!itemStack.has(DataComponents.CONTAINER)) {
            return false;
        }

        var itemContainerContents = itemStack.get(DataComponents.CONTAINER);
        var stream = itemContainerContents != null ? itemContainerContents.stream() : Stream.<ItemStack>builder().build();

        return contentsMatchFilter(stream, filter);
    }

    private boolean contentsMatchFilter(Stream<ItemStack> stream, String filter) {
        return stream.anyMatch(itemStack -> matchesFilter(itemStack, filter) || bundleContentsMatchFilter(itemStack, filter));
    }

    private boolean matchesFilter(ItemStack itemStack, String filter) {
        var itemNameMatchesFilter = componentMatchesFilter(itemStack.getItemName(), filter);
        var itemDisplayNameMatchesFilter = componentMatchesFilter(itemStack.getDisplayName(), filter);
        var tooltipMatchesFilter = tooltipMatchesFilter(itemStack, filter);

        return itemNameMatchesFilter || itemDisplayNameMatchesFilter || tooltipMatchesFilter;
    }

    private boolean tooltipMatchesFilter(ItemStack itemStack, String filter) {
        var tooltipComponents = tooltipService.retrieveTooltipLines(itemStack);
        var itemEnchantsMatchesFilter = tooltipService.retrieveEnchantmentNames(tooltipComponents)
                .toLowerCase()
                .contains(filter.toLowerCase());
        var jukeboxSongMatchesFilter = tooltipService.retrieveJukeboxSongName(tooltipComponents)
                .toLowerCase()
                .contains(filter.toLowerCase());
        var potionMatchesFilter = tooltipService.retrievePotionName(tooltipComponents)
                .toLowerCase()
                .contains(filter.toLowerCase());

        return itemEnchantsMatchesFilter || jukeboxSongMatchesFilter || potionMatchesFilter;
    }

    private boolean componentMatchesFilter(Component component, String filter) {
        return component.getString().toLowerCase().contains(filter.toLowerCase());
    }
}
