package com.nemonotfound.nemos.inventory.sorting.client.service;

import com.nemonotfound.nemos.inventory.sorting.Constants;
import com.nemonotfound.nemos.inventory.sorting.client.model.ItemData;
import com.nemonotfound.nemos.inventory.sorting.client.model.SlotItem;
import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import java.util.List;

import static com.nemonotfound.nemos.inventory.sorting.Constants.MAX_MERGING_CYCLES;
import static java.util.stream.Collectors.groupingBy;

public class MergingService {

    private static MergingService INSTANCE;
    private final SlotSwappingService inventorySwapService;
    private final Minecraft minecraft;

    private MergingService(SlotSwappingService inventorySwapService, Minecraft minecraft) {
        this.inventorySwapService = inventorySwapService;
        this.minecraft = minecraft;
    }

    public static MergingService getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new MergingService(SlotSwappingService.getInstance(), Minecraft.getInstance());
        }

        return INSTANCE;
    }

    public void mergeAllItems(AbstractContainerMenu menu, List<SlotItem> sortedSlotItems, int containerId) {
        var groupedItemMap = sortedSlotItems.stream()
                .collect(groupingBy(itemMap -> new ItemData(
                        itemMap.itemStack().getComponents(),
                        itemMap.itemStack().getItem().getName(itemMap.itemStack())
                )));

        groupedItemMap.forEach((key, slotItems) -> mergeItems(menu, slotItems, containerId));
    }

    private void mergeItems(AbstractContainerMenu menu, List<SlotItem> slotItems, int containerId) {
        if (slotItems.size() <= 1) {
            return;
        }

        var leftSlotIndex = 0;
        var rightSlotIndex = slotItems.size() - 1;
        var remainingCycles = MAX_MERGING_CYCLES;

        while (leftSlotIndex < rightSlotIndex && remainingCycles-- > 0) {
            var leftSlotItem = slotItems.get(leftSlotIndex);
            var rightSlotItem = slotItems.get(rightSlotIndex);
            var leftSlot = menu.slots.get(leftSlotItem.slotIndex());
            var rightSlot = menu.slots.get(rightSlotItem.slotIndex());
            var leftItem = leftSlot.getItem();

            if (!isFullStack(leftItem)) {
                inventorySwapService.performSlotSwap(
                        minecraft.gameMode,
                        containerId,
                        rightSlotItem.slotIndex(),
                        leftSlotItem.slotIndex(),
                        minecraft.player
                );
            } else {
                leftSlotIndex++;
            }

            if (rightSlot.getItem().isEmpty()) {
                rightSlotIndex--;
            }
        }

        if (remainingCycles <= 0) {
            Constants.LOG.warn("Merging items exceeded cycle limit. Please report this.");
        }
    }

    private boolean isFullStack(ItemStack itemStack) {
        return itemStack.getCount() >= itemStack.getMaxStackSize();
    }
}
