package com.nemonotfound.nemos.inventory.sorting.service;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;

import java.util.List;
import java.util.stream.IntStream;

public class QuickMoveTargetResolver {

    public static final int PLAYER_INVENTORY_SLOT_COUNT = 36;
    public static final QuickMoveTargetResolver INSTANCE = new QuickMoveTargetResolver();

    private QuickMoveTargetResolver() {
    }

    public List<Integer> getTargetSlots(AbstractContainerMenu menu, int sourceSlot) {
        if (menu instanceof InventoryMenu) {
            if (sourceSlot >= InventoryMenu.USE_ROW_SLOT_START && sourceSlot < InventoryMenu.USE_ROW_SLOT_END) {
                return range(InventoryMenu.INV_SLOT_START, InventoryMenu.INV_SLOT_END);
            }

            if (sourceSlot >= InventoryMenu.INV_SLOT_START && sourceSlot < InventoryMenu.INV_SLOT_END) {
                return reversedRange(InventoryMenu.USE_ROW_SLOT_START, InventoryMenu.USE_ROW_SLOT_END);
            }

            return allSlots(menu);
        }

        var containerSize = menu.slots.size() - PLAYER_INVENTORY_SLOT_COUNT;
        if (containerSize <= 0) {
            return allSlots(menu);
        }

        return sourceSlot < containerSize
                ? reversedRange(containerSize, menu.slots.size())
                : range(0, containerSize);
    }

    public List<Integer> allSlots(AbstractContainerMenu menu) {
        return range(0, menu.slots.size());
    }

    private List<Integer> range(int start, int end) {
        return IntStream.range(start, end).boxed().toList();
    }

    private List<Integer> reversedRange(int start, int end) {
        return IntStream.iterate(end - 1, slot -> slot >= start, slot -> slot - 1).boxed().toList();
    }
}
