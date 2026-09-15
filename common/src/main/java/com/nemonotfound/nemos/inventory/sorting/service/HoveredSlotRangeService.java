package com.nemonotfound.nemos.inventory.sorting.service;

import com.nemonotfound.nemos.inventory.sorting.models.SlotRange;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;

import java.util.Optional;

import static net.minecraft.world.inventory.InventoryMenu.INV_SLOT_END;
import static net.minecraft.world.inventory.InventoryMenu.INV_SLOT_START;

public class HoveredSlotRangeService {

    private static final int HOTBAR_SLOT_COUNT = 9;

    private static HoveredSlotRangeService INSTANCE;

    public static HoveredSlotRangeService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HoveredSlotRangeService();
        }

        return INSTANCE;
    }

    public Optional<SlotRange> getSlotRange(
            AbstractContainerMenu menu,
            Slot hoveredSlot,
            boolean storageSortable,
            boolean includeHotbar
    ) {
        return getSlotRange(
                menu.slots.size(),
                hoveredSlot.index,
                menu instanceof InventoryMenu,
                storageSortable,
                includeHotbar
        );
    }

    Optional<SlotRange> getSlotRange(
            int slotCount,
            int hoveredSlotIndex,
            boolean inventoryMenu,
            boolean storageSortable,
            boolean includeHotbar
    ) {
        if (hoveredSlotIndex < 0 || hoveredSlotIndex >= slotCount) {
            return Optional.empty();
        }

        if (inventoryMenu) {
            return getInventorySlotRange(hoveredSlotIndex, includeHotbar);
        }

        return getContainerSlotRange(slotCount, hoveredSlotIndex, storageSortable, includeHotbar);
    }

    private Optional<SlotRange> getInventorySlotRange(int hoveredSlotIndex, boolean includeHotbar) {
        var inventoryWithHotbarEnd = INV_SLOT_END + HOTBAR_SLOT_COUNT;

        if (hoveredSlotIndex < INV_SLOT_START || hoveredSlotIndex >= inventoryWithHotbarEnd) {
            return Optional.empty();
        }

        return Optional.of(new SlotRange(
                INV_SLOT_START,
                includeHotbar ? inventoryWithHotbarEnd : INV_SLOT_END
        ));
    }

    private Optional<SlotRange> getContainerSlotRange(
            int slotCount,
            int hoveredSlotIndex,
            boolean storageSortable,
            boolean includeHotbar
    ) {
        var containerSize = slotCount - QuickMoveTargetResolver.PLAYER_INVENTORY_SLOT_COUNT;

        if (containerSize <= 0) {
            return Optional.empty();
        }

        if (hoveredSlotIndex < containerSize) {
            return storageSortable
                    ? Optional.of(new SlotRange(0, containerSize))
                    : Optional.empty();
        }

        return Optional.of(new SlotRange(
                containerSize,
                includeHotbar ? slotCount : slotCount - HOTBAR_SLOT_COUNT
        ));
    }
}
