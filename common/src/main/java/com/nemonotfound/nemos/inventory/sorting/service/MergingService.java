package com.nemonotfound.nemos.inventory.sorting.service;

import com.nemonotfound.nemos.inventory.sorting.Constants;
import com.nemonotfound.nemos.inventory.sorting.models.SlotItem;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

import static com.nemonotfound.nemos.inventory.sorting.Constants.MAX_MERGING_CYCLES;
import static java.util.stream.Collectors.groupingBy;

public class MergingService {

    private static MergingService INSTANCE;
    private final SlotSwapService slotSwapService;
    private final Minecraft minecraft;

    private MergingService(SlotSwapService slotSwapService, Minecraft minecraft) {
        this.slotSwapService = slotSwapService;
        this.minecraft = minecraft;
    }

    public static MergingService getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new MergingService(SlotSwapService.getInstance(), Minecraft.getInstance());
        }

        return INSTANCE;
    }

    public boolean mergeAllItems(AbstractContainerMenu menu, List<SlotItem> sortedSlotItems) {
        var groupedItemMap = sortedSlotItems.stream()
                .filter(slotItem -> slotItem.itemStack().getMaxStackSize() > 1)
                .collect(groupingBy(slotItem -> ItemGroup.from(slotItem.itemStack())));

        return groupedItemMap.values().stream()
                .filter(slotItems -> slotItems.size() > 1)
                .map(slotItems -> mergeItems(menu, slotItems))
                .reduce(false, Boolean::logicalOr);
    }

    private boolean mergeItems(AbstractContainerMenu menu, List<SlotItem> slotItems) {
        var mergedItems = false;
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
                slotSwapService.mergeStack(
                        menu,
                        rightSlotItem.slotIndex(),
                        leftSlotItem.slotIndex()
                );
                mergedItems = true;
            } else {
                leftSlotIndex++;
            }

            if (rightSlot.getItem().isEmpty()) {
                rightSlotIndex--;
            }
        }

        if (remainingCycles <= 0) {
            Constants.LOGGER.warn("Merging items exceeded cycle limit. Please report this.");
        }

        return mergedItems;
    }

    private boolean isFullStack(ItemStack itemStack) {
        return itemStack.getCount() >= itemStack.getMaxStackSize();
    }

    record ItemGroup(Item item, DataComponentMap components) {

        static ItemGroup from(ItemStack itemStack) {
            return new ItemGroup(itemStack.getItem(), itemStack.getComponents());
        }
    }
}
