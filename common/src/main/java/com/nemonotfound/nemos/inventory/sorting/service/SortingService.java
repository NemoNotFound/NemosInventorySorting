package com.nemonotfound.nemos.inventory.sorting.service;

import com.nemonotfound.nemos.inventory.sorting.Constants;
import com.nemonotfound.nemos.inventory.sorting.model.ItemSorting;
import com.nemonotfound.nemos.inventory.sorting.model.SlotItem;
import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.AbstractContainerMenu;
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
    private final ItemSortingFactory itemSortingFactory;
    private final Minecraft minecraft;

    private SortingService(SlotSwappingService inventorySwapService, ItemSortingFactory itemSortingFactory, Minecraft minecraft) {
        this.inventorySwapService = inventorySwapService;
        this.itemSortingFactory = itemSortingFactory;
        this.minecraft = minecraft;
    }


    public static SortingService getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new SortingService(SlotSwappingService.getInstance(), ItemSortingFactory.INSTANCE, Minecraft.getInstance());
        }

        return INSTANCE;
    }

    public @NotNull List<SlotItem> sortSlotItems(AbstractContainerMenu menu, int startIndex, int endIndex) {
        var itemOrder = ItemOrderService.INSTANCE.getItemOrder();

        return IntStream.range(startIndex, endIndex)
                .mapToObj(slotIndex -> new SlotItem(slotIndex, menu.slots.get(slotIndex).getItem()))
                .filter(slotItem -> !LockedSlotService.INSTANCE.isLocked(slotItem.slotIndex(), startIndex))
                .filter(slotItem -> !slotItem.itemStack().isEmpty())
                .sorted(Comparator.comparing(slotItem -> itemSortingFactory.create(slotItem.itemStack(), itemOrder), ItemSorting.COMPARATOR))
                .toList();
    }

    public Map<Integer, Integer> retrieveSlotSwapMap(List<SlotItem> slotItems, int startIndex, int endIndex) {
        Map<Integer, Integer> slotSwapMap = new LinkedHashMap<>();
        var unlockedSlots = LockedSlotService.INSTANCE.getUnlockedSlots(startIndex, endIndex);

        for (int i = 0; i < slotItems.size(); i++) {
            int newSlot = unlockedSlots.get(i);
            int currentSlot = slotItems.get(i).slotIndex();

            if (currentSlot != newSlot) {
                slotSwapMap.put(currentSlot, newSlot);
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
            Constants.LOG.warn("Slot swap cycle limit reached. Please report this");
        }
    }
}
