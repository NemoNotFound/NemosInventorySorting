package com.nemonotfound.nemos.inventory.sorting.service;

import com.nemonotfound.nemos.inventory.sorting.Constants;
import com.nemonotfound.nemos.inventory.sorting.models.SlotItem;
import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static com.nemonotfound.nemos.inventory.sorting.Constants.MAX_SORTING_CYCLES;

public class SortingService {

    private static SortingService INSTANCE;

    private final SlotSwapService slotSwapService;
    private final ComparingService comparingService;
    private final Minecraft minecraft;

    private SortingService(SlotSwapService slotSwapService, ComparingService comparingService, Minecraft minecraft) {
        this.slotSwapService = slotSwapService;
        this.comparingService = comparingService;
        this.minecraft = minecraft;
    }


    public static SortingService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SortingService(SlotSwapService.getInstance(), ComparingService.INSTANCE, Minecraft.getInstance());
        }

        return INSTANCE;
    }

    public @NotNull List<SlotItem> sortSlotItems(AbstractContainerMenu menu, int startIndex, int endIndex) {
        return IntStream.range(startIndex, endIndex)
                .filter(index -> !LockedSlotService.INSTANCE.isLocked(index, startIndex))
                .mapToObj(index -> new SlotItem(index, menu.slots.get(index).getItem()))
                .filter(slotItem -> !slotItem.itemStack().isEmpty())
                .sorted(comparingService.compare())
                .toList();
    }

    public Map<Integer, Integer> retrieveSlotSwaps(List<SlotItem> slotItems, int startIndex, int endIndex) {
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

    public void sortItemsInInventory(AbstractContainerMenu menu, Map<Integer, Integer> slotSwapMap) {
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

            slotSwapService.performSlotSwap(
                    menu,
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
