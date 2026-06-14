package com.nemonotfound.nemos.inventory.sorting.service;

import com.nemonotfound.nemos.inventory.sorting.models.SlotItem;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class ComparingService {

    public static final ComparingService INSTANCE = new ComparingService(TooltipService.INSTANCE);

    private final TooltipService tooltipService;

    private ComparingService(TooltipService tooltipService) {
        this.tooltipService = tooltipService;
    }

    public Comparator<SlotItem> compare() {
        return comparatorByItemOrder();
    }

    private Comparator<SlotItem> comparatorByItemOrder() {
        List<Item> sortOrder = getSortOrder();

        Comparator<SlotItem> comparator = Comparator.comparingInt(
                slotItem -> IntStream.range(0, sortOrder.size())
                        .filter(i -> slotItem.itemStack().is(sortOrder.get(i)))
                        .findFirst()
                        .orElse(Integer.MAX_VALUE)
        );

        return comparatorByStackSize(comparator);
    }

    private Comparator<SlotItem> comparatorByStackSize(Comparator<SlotItem> comparator) {
        var stackSizeComparator = comparator.thenComparing(
                (Comparator<SlotItem> & Serializable) (a, b) ->
                        Integer.compare(b.itemStack().count(), a.itemStack().count())
        );

        return comparatorByName(stackSizeComparator);
    }

    private Comparator<SlotItem> comparatorByName(Comparator<SlotItem> comparator) {
        var nameComparator = comparator.thenComparing(
                slotItem -> slotItem.itemStack()
                        .getItemName()
                        .getString()
        );

        return comparatorByTooltip(nameComparator);
    }

    private Comparator<SlotItem> comparatorByTooltip(Comparator<SlotItem> comparator) {
        var enchantmentComparator = comparator.thenComparing(slotItem -> {
            var tooltipComponents = tooltipService.retrieveTooltipLines(slotItem.itemStack());

            return tooltipService.retrieveEnchantmentNames(tooltipComponents);
        });

        var jukeboxSongComparator = enchantmentComparator.thenComparing(slotItem -> {
            var tooltipComponents = tooltipService.retrieveTooltipLines(slotItem.itemStack());

            return tooltipService.retrieveJukeboxSongName(tooltipComponents);
        });

        return jukeboxSongComparator.thenComparing(slotItem -> {
            var tooltipComponents = tooltipService.retrieveTooltipLines(slotItem.itemStack());

            return tooltipService.retrievePotionName(tooltipComponents);
        });
    }

    private static List<Item> getSortOrder() {
        Minecraft minecraft = Minecraft.getInstance();

        var player = minecraft.player;

        if (player != null && minecraft.level != null) {
            var level = minecraft.level;
            var hasPermissions = player.canUseGameMasterBlocks() && minecraft.options.operatorItemsTab().get();
            var parameters = new CreativeModeTab.ItemDisplayParameters(level.enabledFeatures(), hasPermissions, level.registryAccess());

            CreativeModeTabs.buildAllTabContents(parameters);
        }

        return getSearchTabItems();
    }

    private static @NonNull List<Item> getSearchTabItems() {
        return BuiltInRegistries.CREATIVE_MODE_TAB.stream()
                .filter(tab -> tab.getType() == CreativeModeTab.Type.SEARCH)
                .flatMap(tab -> tab.getSearchTabDisplayItems().stream())
                .map(ItemStack::getItem)
                .distinct()
                .toList();
    }
}
