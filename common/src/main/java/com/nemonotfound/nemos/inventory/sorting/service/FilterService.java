package com.nemonotfound.nemos.inventory.sorting.service;

import com.nemonotfound.nemos.inventory.sorting.model.FilterResult;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Arrays;

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

        if (contentsMatchFilter(slotItem, filter, 0)) {
            return FilterResult.HAS_INCLUDED_ITEM;
        }

        return matchesFilters(slotItem, filter) ? FilterResult.INCLUDED : FilterResult.EXCLUDED;
    }

    private boolean contentsMatchFilter(ItemStack itemStack, String filter, int depth) {
        if (depth >= 16 || itemStack.getTag() == null) {
            return false;
        }

        var tag = itemStack.getTag();
        if (tag.contains("BlockEntityTag", Tag.TAG_COMPOUND)) {
            tag = tag.getCompound("BlockEntityTag");
        }

        if (!tag.contains("Items", Tag.TAG_LIST)) {
            return false;
        }

        return contentsMatchFilter(tag.getList("Items", Tag.TAG_COMPOUND), filter, depth);
    }

    private boolean contentsMatchFilter(ListTag items, String filter, int depth) {
        for (Tag item : items) {
            var containedStack = ItemStack.of((CompoundTag) item);
            if (matchesFilters(containedStack, filter) || contentsMatchFilter(containedStack, filter, depth + 1)) {
                return true;
            }
        }

        return false;
    }

    private boolean matchesFilters(ItemStack itemStack, String filter) {
        return Arrays.stream(filter.split(","))
                .map(String::trim)
                .anyMatch(part -> matchesFilter(itemStack, part));
    }

    private boolean matchesFilter(ItemStack itemStack, String filter) {
        if (filter.startsWith("#")) {
            return matchesTag(itemStack, filter.substring(1));
        }

        var itemNameMatchesFilter = componentMatchesFilter(itemStack.getItem().getName(itemStack), filter);
        var itemDisplayNameMatchesFilter = componentMatchesFilter(itemStack.getDisplayName(), filter);
        var tooltipMatchesFilter = tooltipMatchesFilter(itemStack, filter);

        return itemNameMatchesFilter || itemDisplayNameMatchesFilter || tooltipMatchesFilter;
    }

    private boolean matchesTag(ItemStack itemStack, String filter) {
        var identifier = ResourceLocation.tryParse(filter);
        return identifier != null && itemStack.is(TagKey.create(Registries.ITEM, identifier));
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
