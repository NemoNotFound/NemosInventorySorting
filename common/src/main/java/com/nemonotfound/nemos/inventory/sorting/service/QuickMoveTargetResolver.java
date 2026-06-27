package com.nemonotfound.nemos.inventory.sorting.service;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class QuickMoveTargetResolver {

    public static final int PLAYER_INVENTORY_SLOT_COUNT = 36;

    private static QuickMoveTargetResolver INSTANCE;

    public static QuickMoveTargetResolver getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new QuickMoveTargetResolver();
        }

        return INSTANCE;
    }

    public List<Integer> getQuickMoveTargetSlots(AbstractContainerMenu menu, int sourceSlot) {
        if (menu instanceof InventoryMenu) {
            if (sourceSlot >= InventoryMenu.USE_ROW_SLOT_START && sourceSlot < InventoryMenu.USE_ROW_SLOT_END) {
                return getSlotRange(InventoryMenu.INV_SLOT_START, InventoryMenu.INV_SLOT_END);
            }

            if (sourceSlot >= InventoryMenu.INV_SLOT_START && sourceSlot < InventoryMenu.INV_SLOT_END) {
                return getSlotRangeReversed(InventoryMenu.USE_ROW_SLOT_START, InventoryMenu.USE_ROW_SLOT_END);
            }

            return getAllSlots(menu);
        }

        var containerSize = menu.slots.size() - PLAYER_INVENTORY_SLOT_COUNT;

        if (containerSize <= 0) {
            return getAllSlots(menu);
        }

        if (sourceSlot < containerSize) {
            return getSlotRangeReversed(containerSize, menu.slots.size());
        }

        return getSlotRange(0, containerSize);
    }

    public List<Integer> getAllSlots(AbstractContainerMenu menu) {
        return getSlotRange(0, menu.slots.size());
    }

    private List<Integer> getSlotRange(int startInclusive, int endExclusive) {
        return IntStream.range(startInclusive, endExclusive)
                .boxed()
                .collect(Collectors.toList());
    }

    private List<Integer> getSlotRangeReversed(int startInclusive, int endExclusive) {
        return IntStream.iterate(endExclusive - 1, slot -> slot >= startInclusive, slot -> slot - 1)
                .boxed()
                .collect(Collectors.toList());
    }
}
