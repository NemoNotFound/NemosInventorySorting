package com.nemonotfound.nemos.inventory.sorting.service;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ScrollTransferTargetService {

    public static final ScrollTransferTargetService INSTANCE = new ScrollTransferTargetService(QuickMoveTargetResolver.INSTANCE);

    private final QuickMoveTargetResolver targetResolver;

    private ScrollTransferTargetService(QuickMoveTargetResolver targetResolver) {
        this.targetResolver = targetResolver;
    }

    public Optional<Transfer> getTransfer(AbstractContainerMenu menu, Slot hoveredSlot, double delta, boolean allowLastItem) {
        var source = getSource(menu, hoveredSlot, delta, allowLastItem);
        if (source.isEmpty() || !canMoveFrom(source.get(), allowLastItem)) {
            return Optional.empty();
        }

        return getTarget(menu, hoveredSlot, delta, source.get())
                .map(target -> new Transfer(source.get(), target, isResultSlot(source.get())));
    }

    public Optional<Slot> getTemporaryCarriedSlot(AbstractContainerMenu menu, ItemStack carried, int source, int target) {
        if (carried.isEmpty()) {
            return Optional.empty();
        }

        var excluded = Set.of(source, target);
        var targetSlots = targetResolver.getTargetSlots(menu, source);
        var nonTargets = targetResolver.allSlots(menu).stream().filter(slot -> !targetSlots.contains(slot)).toList();

        return firstEmpty(menu, carried, source, excluded, nonTargets)
                .or(() -> firstEmpty(menu, carried, source, excluded, targetResolver.allSlots(menu)));
    }

    private Optional<Slot> getSource(AbstractContainerMenu menu, Slot hovered, double delta, boolean allowLastItem) {
        if (isHoveredSource(menu, hovered.index, delta)) {
            return Optional.of(hovered);
        }

        return targetResolver.getTargetSlots(menu, hovered.index).stream()
                .map(menu::getSlot)
                .filter(slot -> canMoveFrom(slot, allowLastItem))
                .filter(slot -> hovered.getItem().isEmpty()
                        ? hovered.mayPlace(slot.getItem())
                        : ItemStack.isSameItemSameTags(slot.getItem(), hovered.getItem()))
                .findFirst();
    }

    private Optional<Slot> getTarget(AbstractContainerMenu menu, Slot hovered, double delta, Slot source) {
        if (!isHoveredSource(menu, hovered.index, delta)) {
            return Optional.of(hovered).filter(slot -> canAccept(slot, source.getItem(), isResultSlot(source)));
        }

        var targets = targetResolver.getTargetSlots(menu, source.index);
        return targets.stream().map(menu::getSlot)
                .filter(slot -> matches(slot, source.getItem(), isResultSlot(source)))
                .findFirst()
                .or(() -> targets.stream().map(menu::getSlot)
                        .filter(slot -> slot.getItem().isEmpty() && canAccept(slot, source.getItem(), isResultSlot(source)))
                        .findFirst());
    }

    private boolean canMoveFrom(Slot slot, boolean allowLastItem) {
        return !slot.getItem().isEmpty() && hasTransferableCount(slot.getItem(), shouldAllowLastItem(slot, allowLastItem));
    }

    private boolean matches(Slot slot, ItemStack stack, boolean entireStack) {
        return ItemStack.isSameItemSameTags(slot.getItem(), stack) && canAccept(slot, stack, entireStack);
    }

    private boolean canAccept(Slot slot, ItemStack stack, boolean entireStack) {
        if (isResultSlot(slot) || !slot.isActive() || !slot.mayPlace(stack)) {
            return false;
        }

        var available = slot.getMaxStackSize(stack) - slot.getItem().getCount();
        return (slot.getItem().isEmpty() || ItemStack.isSameItemSameTags(slot.getItem(), stack))
                && available >= (entireStack ? stack.getCount() : 1);
    }

    private Optional<Slot> firstEmpty(AbstractContainerMenu menu, ItemStack stack, int source, Set<Integer> excluded, List<Integer> slots) {
        return slots.stream().map(menu::getSlot)
                .filter(slot -> slot.index != source && !excluded.contains(slot.index))
                .filter(slot -> !isCraftingSlot(menu, slot))
                .filter(slot -> slot.isActive() && slot.mayPlace(stack) && slot.getItem().isEmpty())
                .findFirst();
    }

    private boolean isHoveredSource(AbstractContainerMenu menu, int slot, double delta) {
        return (menu instanceof InventoryMenu ? delta < 0 : delta > 0) == isInventorySide(menu, slot);
    }

    private boolean isInventorySide(AbstractContainerMenu menu, int slot) {
        if (menu instanceof InventoryMenu) {
            return slot >= InventoryMenu.INV_SLOT_START && slot < InventoryMenu.USE_ROW_SLOT_END;
        }

        return slot >= menu.slots.size() - QuickMoveTargetResolver.PLAYER_INVENTORY_SLOT_COUNT;
    }

    private boolean isCraftingSlot(AbstractContainerMenu menu, Slot slot) {
        if (menu instanceof CraftingMenu craftingMenu) {
            return slot.index <= craftingMenu.getSize();
        }

        return menu instanceof InventoryMenu && slot.index < InventoryMenu.INV_SLOT_START;
    }

    static boolean shouldAllowLastItem(Slot slot, boolean allowLastItem) {
        return allowLastItem || isResultSlot(slot);
    }

    static boolean hasTransferableCount(ItemStack stack, boolean allowLastItem) {
        return stack.getCount() >= (allowLastItem ? 1 : 2);
    }

    static boolean isResultSlot(Slot slot) {
        return !slot.getItem().isEmpty() && !slot.mayPlace(slot.getItem());
    }

    public record Transfer(Slot source, Slot target, boolean result) {
    }
}
