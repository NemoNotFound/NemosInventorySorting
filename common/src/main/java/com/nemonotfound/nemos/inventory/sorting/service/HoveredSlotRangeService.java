package com.nemonotfound.nemos.inventory.sorting.service;

import com.nemonotfound.nemos.inventory.sorting.model.SlotRange;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;

import java.util.Optional;

public class HoveredSlotRangeService {

    private static final int HOTBAR_SIZE = 9;
    public static final HoveredSlotRangeService INSTANCE = new HoveredSlotRangeService();

    private HoveredSlotRangeService() {
    }

    public Optional<SlotRange> getSlotRange(AbstractContainerMenu menu, Slot hoveredSlot, boolean storageSortable, boolean includeHotbar) {
        return getSlotRange(menu.slots.size(), hoveredSlot.index, menu instanceof InventoryMenu, storageSortable, includeHotbar);
    }

    Optional<SlotRange> getSlotRange(int slotCount, int hoveredSlotIndex, boolean inventoryMenu, boolean storageSortable, boolean includeHotbar) {
        if (hoveredSlotIndex < 0 || hoveredSlotIndex >= slotCount) {
            return Optional.empty();
        }

        if (inventoryMenu) {
            var end = InventoryMenu.INV_SLOT_END + HOTBAR_SIZE;
            if (hoveredSlotIndex < InventoryMenu.INV_SLOT_START || hoveredSlotIndex >= end) {
                return Optional.empty();
            }
            return Optional.of(new SlotRange(InventoryMenu.INV_SLOT_START, includeHotbar ? end : InventoryMenu.INV_SLOT_END));
        }

        var containerSize = slotCount - QuickMoveTargetResolver.PLAYER_INVENTORY_SLOT_COUNT;
        if (containerSize <= 0) {
            return Optional.empty();
        }
        if (hoveredSlotIndex < containerSize) {
            return storageSortable ? Optional.of(new SlotRange(0, containerSize)) : Optional.empty();
        }
        return Optional.of(new SlotRange(containerSize, includeHotbar ? slotCount : slotCount - HOTBAR_SIZE));
    }
}
