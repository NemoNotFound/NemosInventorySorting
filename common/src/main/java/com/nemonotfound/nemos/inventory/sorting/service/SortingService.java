package com.nemonotfound.nemos.inventory.sorting.service;

import com.nemonotfound.nemos.inventory.sorting.Constants;
import com.nemonotfound.nemos.inventory.sorting.models.SlotItem;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static com.nemonotfound.nemos.inventory.sorting.Constants.MAX_SORTING_CYCLES;

public class SortingService {

    private static SortingService INSTANCE;

    private final SlotSwappingService inventorySwapService;
    private final TooltipService tooltipService;
    private final Minecraft minecraft;

    private SortingService(SlotSwappingService inventorySwapService, TooltipService tooltipService, Minecraft minecraft) {
        this.inventorySwapService = inventorySwapService;
        this.tooltipService = tooltipService;
        this.minecraft = minecraft;
    }


    public static SortingService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SortingService(SlotSwappingService.getInstance(), TooltipService.getInstance(), Minecraft.getInstance());
        }

        return INSTANCE;
    }

    public @NotNull List<SlotItem> sortSlotItems(AbstractContainerMenu menu, int startIndex, int endIndex) {
        return IntStream.range(startIndex, endIndex)
                .filter(index -> !LockedSlotService.INSTANCE.isLocked(index, startIndex))
                .mapToObj(index -> new SlotItem(index, menu.slots.get(index).getItem()))
                .filter(slotItem -> !slotItem.itemStack().isEmpty())
                .sorted(comparatorByItemOrder())
                .toList();
    }

    protected Comparator<SlotItem> comparatorByItemOrder() {
        List<Item> sortOrder = getSearchTabItems();

        Comparator<SlotItem> comparator = Comparator.comparingInt(
                slotItem -> IntStream.range(0, sortOrder.size())
                        .filter(i -> slotItem.itemStack().is(sortOrder.get(i)))
                        .findFirst()
                        .orElse(Integer.MAX_VALUE)
        );

        return comparatorByName(comparator);
    }

    private List<Item> getSearchTabItems() {
        var player = this.minecraft.player;
        var level = player.level();
        var hasPermissions = player.canUseGameMasterBlocks() && this.minecraft.options.operatorItemsTab().get();
        var parameters = new CreativeModeTab.ItemDisplayParameters(level.enabledFeatures(), hasPermissions, level.registryAccess());

        CreativeModeTabs.buildAllTabContents(parameters);

        return BuiltInRegistries.CREATIVE_MODE_TAB.stream()
                .filter(tab -> tab.getType() == CreativeModeTab.Type.SEARCH)
                .flatMap(tab -> tab.getSearchTabDisplayItems().stream())
                .map(ItemStack::getItem)
                .distinct()
                .toList();
    }

    protected Comparator<SlotItem> comparatorByName(Comparator<SlotItem> comparator) {
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

    public Map<Integer, Integer> retrieveSlotSwapMap(List<SlotItem> slotItems, int startIndex, int endIndex) {
        Map<Integer, Integer> slotSwapMap = new LinkedHashMap<>();
        List<Integer> unlockedSlots = LockedSlotService.INSTANCE.getUnlockedSlots(startIndex, endIndex);

        for (int i = 0; i < slotItems.size(); i++) {
            int newIndex = unlockedSlots.get(i);
            int index = slotItems.get(i).slotIndex();

            if (index != newIndex) {
                slotSwapMap.put(index, newIndex);
            }
        }

        return slotSwapMap;
    }

    public void sortItemsInInventory(AbstractContainerMenu menu, Map<Integer, Integer> slotSwapMap, int containerId) {
        int remainingCyles = MAX_SORTING_CYCLES;

        while (!slotSwapMap.isEmpty() && remainingCyles-- > 0) {
            var iterator = slotSwapMap.entrySet().iterator();

            if (!iterator.hasNext()) {
                break;
            }

            var entry = iterator.next();
            int currentSlot = entry.getKey();
            int targetSlot = entry.getValue();

            if (currentSlot == targetSlot) {
                iterator.remove();
                continue;
            }

            inventorySwapService.performSlotSwap(
                    menu,
                    minecraft.gameMode,
                    containerId,
                    currentSlot,
                    targetSlot,
                    minecraft.player
            );

            if (slotSwapMap.containsKey(targetSlot)) {
                slotSwapMap.put(currentSlot, slotSwapMap.get(targetSlot));
            } else {
                iterator.remove();
            }

            slotSwapMap.put(targetSlot, targetSlot);
        }

        if (remainingCyles <= 0) {
            Constants.LOGGER.warn("Slot swap cycle limit reached. Please report this");
        }
    }
}
