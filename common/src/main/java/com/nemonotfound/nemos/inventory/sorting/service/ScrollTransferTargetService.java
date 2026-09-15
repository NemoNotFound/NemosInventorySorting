package com.nemonotfound.nemos.inventory.sorting.service;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AbstractCraftingMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ScrollTransferTargetService {
    private static ScrollTransferTargetService INSTANCE;
    private final QuickMoveTargetResolver quickMoveTargetResolver;

    private ScrollTransferTargetService(QuickMoveTargetResolver quickMoveTargetResolver) {
        this.quickMoveTargetResolver = quickMoveTargetResolver;
    }

    public static ScrollTransferTargetService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ScrollTransferTargetService(QuickMoveTargetResolver.getInstance());
        }

        return INSTANCE;
    }

    public Optional<SlotTransfer> getTransfer(AbstractContainerMenu menu, int hoveredSlotIndex, double scrollDelta, boolean allowLastItem) {
        var sourceSlot = getSourceSlot(menu, hoveredSlotIndex, scrollDelta, allowLastItem);
        if (sourceSlot.isEmpty() || !canMoveFromSource(sourceSlot.get(), allowLastItem)) {
            return Optional.empty();
        }

        return getTargetSlot(menu, hoveredSlotIndex, scrollDelta, sourceSlot.get())
                .map(targetSlot -> new SlotTransfer(sourceSlot.get(), targetSlot, isResultSlot(sourceSlot.get())));
    }

    public Optional<Slot> getTemporaryCarriedSlot(AbstractContainerMenu menu, ItemStack carriedStack, int sourceSlotIndex, int targetSlotIndex) {
        if (carriedStack.isEmpty()) {
            return Optional.empty();
        }

        var excludedSlots = Set.of(sourceSlotIndex, targetSlotIndex);
        var targetSlots = quickMoveTargetResolver.getQuickMoveTargetSlots(menu, sourceSlotIndex);
        var nonTargetSlots = quickMoveTargetResolver.getAllSlots(menu).stream()
                .filter(slot -> !targetSlots.contains(slot))
                .filter(slot -> !isCraftingMenuSlot(menu, menu.getSlot(slot)))
                .toList();
        var safeSlots = quickMoveTargetResolver.getAllSlots(menu).stream()
                .filter(slot -> !isCraftingMenuSlot(menu, menu.getSlot(slot)))
                .toList();

        return getFirstEmptyTarget(menu, carriedStack, sourceSlotIndex, excludedSlots, nonTargetSlots)
                .or(() -> getFirstEmptyTarget(menu, carriedStack, sourceSlotIndex, excludedSlots, safeSlots));
    }

    private Optional<Slot> getSourceSlot(AbstractContainerMenu menu, int hoveredSlotIndex, double scrollDelta, boolean allowLastItem) {
        if (isHoveredSlotSource(menu, hoveredSlotIndex, scrollDelta)) {
            return Optional.of(menu.getSlot(hoveredSlotIndex));
        }

        var targetSlot = menu.getSlot(hoveredSlotIndex);

        return getSourceSlotForTarget(menu, targetSlot, quickMoveTargetResolver.getQuickMoveTargetSlots(menu, hoveredSlotIndex), allowLastItem);
    }

    private Optional<Slot> getTargetSlot(AbstractContainerMenu menu, int hoveredSlotIndex, double scrollDelta, Slot sourceSlot) {
        if (isResultSlot(sourceSlot)) {
            return getResultTargetSlot(menu, hoveredSlotIndex, scrollDelta, sourceSlot);
        }

        if (!isHoveredSlotSource(menu, hoveredSlotIndex, scrollDelta)) {
            return Optional.of(menu.getSlot(hoveredSlotIndex)).filter(slot -> canMoveToTarget(slot, sourceSlot.getItem()));
        }

        var targetSlots = quickMoveTargetResolver.getQuickMoveTargetSlots(menu, hoveredSlotIndex);

        return getFirstMatchingTarget(menu, sourceSlot.getItem(), sourceSlot.index, Set.of(), targetSlots)
                .or(() -> getFirstEmptyTarget(menu, sourceSlot.getItem(), sourceSlot.index, Set.of(), targetSlots));
    }

    private Optional<Slot> getResultTargetSlot(AbstractContainerMenu menu, int hoveredSlotIndex, double scrollDelta, Slot sourceSlot) {
        if (!isHoveredSlotSource(menu, hoveredSlotIndex, scrollDelta)) {
            return Optional.of(menu.getSlot(hoveredSlotIndex)).filter(slot -> canFitResult(slot, sourceSlot.getItem()));
        }

        return quickMoveTargetResolver.getQuickMoveTargetSlots(menu, sourceSlot.index).stream()
                .map(menu::getSlot)
                .filter(slot -> canFitResult(slot, sourceSlot.getItem()))
                .findFirst();
    }

    private Optional<Slot> getSourceSlotForTarget(AbstractContainerMenu menu, Slot targetSlot, List<Integer> sourceSlots, boolean allowLastItem) {
        var targetStack = targetSlot.getItem();
        return sourceSlots.stream()
                .map(menu::getSlot)
                .filter(slot -> canMoveFromSource(slot, allowLastItem))
                .filter(slot -> targetStack.isEmpty() ? targetSlot.mayPlace(slot.getItem()) : ItemStack.isSameItemSameComponents(slot.getItem(), targetStack))
                .findFirst();
    }

    private Optional<Slot> getFirstMatchingTarget(AbstractContainerMenu menu, ItemStack sourceStack, int sourceSlotIndex, Set<Integer> excludedSlots, List<Integer> targetSlots) {
        return targetSlots.stream()
                .map(menu::getSlot)
                .filter(slot -> slot.index != sourceSlotIndex && !excludedSlots.contains(slot.index))
                .filter(slot -> canMoveToMatchingTarget(slot, sourceStack))
                .findFirst();
    }

    private Optional<Slot> getFirstEmptyTarget(AbstractContainerMenu menu, ItemStack sourceStack, int sourceSlotIndex, Set<Integer> excludedSlots, List<Integer> targetSlots) {
        return targetSlots.stream()
                .map(menu::getSlot)
                .filter(slot -> slot.index != sourceSlotIndex && !excludedSlots.contains(slot.index))
                .filter(slot -> canMoveToEmptyTarget(slot, sourceStack))
                .findFirst();
    }

    private boolean canMoveFromSource(Slot sourceSlot, boolean allowLastItem) {
        return !sourceSlot.getItem().isEmpty() && hasTransferableCount(sourceSlot.getItem(), shouldAllowLastItem(sourceSlot, allowLastItem));
    }

    private boolean canMoveToTarget(Slot targetSlot, ItemStack sourceStack) {
        return !isResultSlot(targetSlot) && (canMoveToMatchingTarget(targetSlot, sourceStack) || canMoveToEmptyTarget(targetSlot, sourceStack));
    }

    private boolean canMoveToMatchingTarget(Slot targetSlot, ItemStack sourceStack) {
        return !isResultSlot(targetSlot) && targetSlot.isActive() && targetSlot.mayPlace(sourceStack)
                && ItemStack.isSameItemSameComponents(targetSlot.getItem(), sourceStack)
                && targetSlot.getItem().getCount() < targetSlot.getMaxStackSize(sourceStack);
    }

    private boolean canMoveToEmptyTarget(Slot targetSlot, ItemStack sourceStack) {
        return targetSlot.isActive() && targetSlot.mayPlace(sourceStack) && targetSlot.getItem().isEmpty();
    }

    private boolean canFitResult(Slot targetSlot, ItemStack resultStack) {
        if (!targetSlot.isActive() || !targetSlot.mayPlace(resultStack)) {
            return false;
        }

        if (targetSlot.getItem().isEmpty()) {
            return targetSlot.getMaxStackSize(resultStack) >= resultStack.getCount();
        }

        return ItemStack.isSameItemSameComponents(targetSlot.getItem(), resultStack)
                && targetSlot.getMaxStackSize(resultStack) - targetSlot.getItem().getCount() >= resultStack.getCount();
    }

    private boolean isHoveredSlotSource(AbstractContainerMenu menu, int hoveredSlotIndex, double scrollDelta) {
        return (menu instanceof InventoryMenu ? scrollDelta < 0 : scrollDelta > 0) == isInventorySide(menu, hoveredSlotIndex);
    }

    private boolean isInventorySide(AbstractContainerMenu menu, int slotIndex) {
        if (menu instanceof InventoryMenu) {
            return slotIndex >= InventoryMenu.INV_SLOT_START && slotIndex < InventoryMenu.INV_SLOT_END;
        }

        var containerSize = menu.slots.size() - QuickMoveTargetResolver.PLAYER_INVENTORY_SLOT_COUNT;

        return containerSize > 0 && slotIndex >= containerSize;
    }

    static boolean shouldAllowLastItem(Slot sourceSlot, boolean shiftDown) {
        return shiftDown || isResultSlot(sourceSlot);
    }

    static boolean hasTransferableCount(ItemStack sourceStack, boolean allowLastItem) {
        return sourceStack.getCount() >= (allowLastItem ? 1 : 2);
    }

    static boolean isResultSlot(Slot slot) {
        return !slot.getItem().isEmpty() && !slot.mayPlace(slot.getItem());
    }

    static boolean isCraftingMenuSlot(AbstractContainerMenu menu, Slot slot) {
        return menu instanceof AbstractCraftingMenu craftingMenu
                && (craftingMenu.getResultSlot() == slot || craftingMenu.getInputGridSlots().contains(slot));
    }

    public record SlotTransfer(Slot sourceSlot, Slot targetSlot, boolean result) {
    }
}
