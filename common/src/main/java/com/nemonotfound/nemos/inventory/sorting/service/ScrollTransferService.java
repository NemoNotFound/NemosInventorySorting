package com.nemonotfound.nemos.inventory.sorting.service;

import com.nemonotfound.nemos.inventory.sorting.models.ContainerInputContext;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ScrollTransferService {

    private static ScrollTransferService INSTANCE;

    private final ContainerInputService containerInputService;
    private final QuickMoveTargetResolver quickMoveTargetResolver;

    private ScrollTransferService(ContainerInputService containerInputService, QuickMoveTargetResolver quickMoveTargetResolver) {
        this.containerInputService = containerInputService;
        this.quickMoveTargetResolver = quickMoveTargetResolver;
    }

    public static ScrollTransferService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ScrollTransferService(ContainerInputService.getInstance(), QuickMoveTargetResolver.getInstance());
        }

        return INSTANCE;
    }

    public boolean handleSingleItemScrollMove(AbstractContainerMenu menu, int hoveredSlot, double scrollDelta) {
        if (scrollDelta == 0 || menu instanceof CreativeModeInventoryScreen.ItemPickerMenu) {
            return false;
        }

        return containerInputService.getContext()
                .map(context -> handleSingleItemScrollMove(menu, hoveredSlot, scrollDelta, context))
                .orElse(false);
    }

    private boolean handleSingleItemScrollMove(AbstractContainerMenu menu, int hoveredSlot, double scrollDelta, ContainerInputContext context) {
        var sourceSlot = getSourceSlot(menu, hoveredSlot, scrollDelta);

        if (sourceSlot.isEmpty() || !canMoveFromSource(sourceSlot.get().getItem())) {
            return false;
        }

        var targetSlot = getTargetSlot(menu, hoveredSlot, scrollDelta, sourceSlot.get());

        return targetSlot.filter(slot -> moveSingleItem(menu, context, sourceSlot.get().index, slot.index)).isPresent();

    }

    private Optional<Slot> getSourceSlot(AbstractContainerMenu menu, int hoveredSlot, double scrollDelta) {
        if (isHoveredSlotSource(menu, hoveredSlot, scrollDelta)) {
            return Optional.of(menu.getSlot(hoveredSlot));
        }

        var targetSlot = menu.getSlot(hoveredSlot);
        var sourceSlots = quickMoveTargetResolver.getQuickMoveTargetSlots(menu, hoveredSlot);

        return getSourceSlotForTarget(menu, targetSlot, sourceSlots);
    }

    private Optional<Slot> getTargetSlot(AbstractContainerMenu menu, int hoveredSlot, double scrollDelta, Slot sourceSlot) {
        if (!isHoveredSlotSource(menu, hoveredSlot, scrollDelta)) {
            return Optional.of(menu.getSlot(hoveredSlot))
                    .filter(slot -> canMoveToTarget(slot, sourceSlot.getItem()));
        }

        var targetSlots = quickMoveTargetResolver.getQuickMoveTargetSlots(menu, hoveredSlot);

        return getFirstMatchingTarget(menu, sourceSlot.getItem(), sourceSlot.index, Set.of(), targetSlots)
                .or(() -> getFirstEmptyTarget(menu, sourceSlot.getItem(), sourceSlot.index, Set.of(), targetSlots));
    }

    private boolean isHoveredSlotSource(AbstractContainerMenu menu, int hoveredSlot, double scrollDelta) {
        var scrollsInventoryToOtherSide = scrollsInventoryToOtherSide(menu, scrollDelta);
        var hoveredSlotIsInventorySide = isInventorySide(menu, hoveredSlot);

        return scrollsInventoryToOtherSide == hoveredSlotIsInventorySide;
    }

    private boolean scrollsInventoryToOtherSide(AbstractContainerMenu menu, double scrollDelta) {
        if (menu instanceof InventoryMenu) {
            return scrollDelta < 0;
        }

        return scrollDelta > 0;
    }

    private boolean isInventorySide(AbstractContainerMenu menu, int slot) {
        if (menu instanceof InventoryMenu) {
            return slot >= InventoryMenu.INV_SLOT_START && slot < InventoryMenu.INV_SLOT_END;
        }

        var containerSize = menu.slots.size() - QuickMoveTargetResolver.PLAYER_INVENTORY_SLOT_COUNT;

        return containerSize > 0 && slot >= containerSize;
    }

    private Optional<Slot> getSourceSlotForTarget(AbstractContainerMenu menu, Slot targetSlot, List<Integer> sourceSlots) {
        var targetStack = targetSlot.getItem();

        if (!targetStack.is(Items.AIR)) {
            return sourceSlots.stream()
                    .map(menu::getSlot)
                    .filter(slot -> canMoveFromSource(slot.getItem()))
                    .filter(slot -> ItemStack.isSameItemSameComponents(slot.getItem(), targetStack))
                    .findFirst();
        }

        return sourceSlots.stream()
                .map(menu::getSlot)
                .filter(slot -> canMoveFromSource(slot.getItem()))
                .filter(slot -> targetSlot.mayPlace(slot.getItem()))
                .findFirst();
    }

    private Optional<Slot> getFirstMatchingTarget(AbstractContainerMenu menu, ItemStack sourceStack, int sourceSlot, Set<Integer> excludedSlots, List<Integer> targetSlots) {
        return targetSlots.stream()
                .map(menu::getSlot)
                .filter(slot -> slot.index != sourceSlot)
                .filter(slot -> !excludedSlots.contains(slot.index))
                .filter(slot -> canMoveToMatchingTarget(slot, sourceStack))
                .findFirst();
    }

    private Optional<Slot> getFirstEmptyTarget(AbstractContainerMenu menu, ItemStack sourceStack, int sourceSlot, Set<Integer> excludedSlots, List<Integer> targetSlots) {
        return targetSlots.stream()
                .map(menu::getSlot)
                .filter(slot -> slot.index != sourceSlot)
                .filter(slot -> !excludedSlots.contains(slot.index))
                .filter(slot -> canMoveToEmptyTarget(slot, sourceStack))
                .findFirst();
    }

    private boolean canMoveFromSource(ItemStack sourceStack) {
        return !sourceStack.is(Items.AIR) && sourceStack.getCount() > 1 && sourceStack.getMaxStackSize() > 1;
    }

    private boolean canMoveToTarget(Slot targetSlot, ItemStack sourceStack) {
        return canMoveToMatchingTarget(targetSlot, sourceStack) || canMoveToEmptyTarget(targetSlot, sourceStack);
    }

    private boolean canMoveToMatchingTarget(Slot targetSlot, ItemStack sourceStack) {
        return targetSlot.isActive()
                && targetSlot.mayPlace(sourceStack)
                && ItemStack.isSameItemSameComponents(targetSlot.getItem(), sourceStack)
                && !isFull(targetSlot, sourceStack);
    }

    private boolean canMoveToEmptyTarget(Slot targetSlot, ItemStack sourceStack) {
        return targetSlot.isActive()
                && targetSlot.mayPlace(sourceStack)
                && targetSlot.getItem().is(Items.AIR);
    }

    private boolean moveSingleItem(AbstractContainerMenu menu, ContainerInputContext context, int sourceSlot, int targetSlot) {
        var temporaryCarriedSlot = getTemporaryCarriedSlot(menu, menu.getCarried(), sourceSlot, targetSlot);

        if (!menu.getCarried().is(Items.AIR) && temporaryCarriedSlot.isEmpty()) {
            return false;
        }

        temporaryCarriedSlot.ifPresent(slot -> containerInputService.leftClickPickup(menu, context, slot.index));

        containerInputService.rightCLickPickup(menu, context, sourceSlot);
        containerInputService.rightCLickPickup(menu, context, targetSlot);
        returnCarriedStackToSource(menu, context, sourceSlot);

        temporaryCarriedSlot
                .filter(_ -> menu.getCarried().is(Items.AIR))
                .ifPresent(slot -> containerInputService.leftClickPickup(menu, context, slot.index));

        return true;
    }

    private Optional<Slot> getTemporaryCarriedSlot(AbstractContainerMenu menu, ItemStack carriedStack, int sourceSlot, int targetSlot) {
        if (carriedStack.is(Items.AIR)) {
            return Optional.empty();
        }

        var excludedSlots = Set.of(sourceSlot, targetSlot);
        var targetSlots = quickMoveTargetResolver.getQuickMoveTargetSlots(menu, sourceSlot);
        var nonTargetSlots = quickMoveTargetResolver.getAllSlots(menu).stream()
                .filter(slot -> !targetSlots.contains(slot))
                .toList();

        return getFirstEmptyTarget(menu, carriedStack, sourceSlot, excludedSlots, nonTargetSlots)
                .or(() -> getFirstEmptyTarget(menu, carriedStack, sourceSlot, excludedSlots, quickMoveTargetResolver.getAllSlots(menu)));
    }

    private void returnCarriedStackToSource(AbstractContainerMenu menu, ContainerInputContext context, int sourceSlot) {
        if (!menu.getCarried().is(Items.AIR)) {
            containerInputService.leftClickPickup(menu, context, sourceSlot);
        }
    }

    private boolean isFull(Slot slot, ItemStack itemStack) {
        return slot.getItem().getCount() >= slot.getMaxStackSize(itemStack);
    }
}
