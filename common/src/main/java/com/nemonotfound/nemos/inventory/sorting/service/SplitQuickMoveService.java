package com.nemonotfound.nemos.inventory.sorting.service;

import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class SplitQuickMoveService {

    private static SplitQuickMoveService INSTANCE;

    private final ContainerInputService containerInputService;
    private final QuickMoveTargetResolver quickMoveTargetResolver;

    private SplitQuickMoveService(ContainerInputService containerInputService, QuickMoveTargetResolver quickMoveTargetResolver) {
        this.containerInputService = containerInputService;
        this.quickMoveTargetResolver = quickMoveTargetResolver;
    }

    public static SplitQuickMoveService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SplitQuickMoveService(ContainerInputService.getInstance(), QuickMoveTargetResolver.getInstance());
        }

        return INSTANCE;
    }

    public void handleSplitQuickMove(AbstractContainerMenu menu, int slot) {
        if (menu instanceof CreativeModeInventoryScreen.ItemPickerMenu) {
            return;
        }

        containerInputService.getContext().ifPresent(context -> handleSplitQuickMove(menu, slot, context));
    }

    private void handleSplitQuickMove(AbstractContainerMenu menu, int sourceSlot, ContainerInputService.ContainerInputContext context) {
        var sourceStack = menu.getSlot(sourceSlot).getItem();

        if (!canSplitSourceStack(sourceStack)) {
            return;
        }

        if (menu.getCarried().is(Items.AIR)) {
            handleSplitQuickMoveWithEmptyCarriedItem(menu, context, sourceSlot, sourceStack);
            return;
        }

        handleSplitQuickMoveWithTemporarilyStoredCarriedItem(menu, context, sourceSlot);
    }

    private boolean canSplitSourceStack(ItemStack sourceStack) {
        return !sourceStack.is(Items.AIR) && sourceStack.getCount() > 1;
    }

    private void handleSplitQuickMoveWithEmptyCarriedItem(AbstractContainerMenu menu, ContainerInputService.ContainerInputContext context, int sourceSlot, ItemStack sourceStack) {
        if (sourceStack.getMaxStackSize() == 1) {
            return;
        }

        containerInputService.performRightClick(menu, context, sourceSlot);
        distributeCarriedStackToQuickMoveTarget(menu, context, sourceSlot, Set.of());
        returnCarriedStackToSource(menu, context, sourceSlot);
    }

    private void handleSplitQuickMoveWithTemporarilyStoredCarriedItem(AbstractContainerMenu menu, ContainerInputService.ContainerInputContext context, int sourceSlot) {
        var targetSlots = quickMoveTargetResolver.getQuickMoveTargetSlots(menu, sourceSlot);
        var temporaryCarriedSlot = getTemporaryCarriedSlot(menu, menu.getCarried(), sourceSlot, targetSlots);

        if (temporaryCarriedSlot.isEmpty()) {
            return;
        }

        var temporaryCarriedSlotIndex = temporaryCarriedSlot.get().index;

        containerInputService.performClick(menu, context, temporaryCarriedSlotIndex);
        containerInputService.performRightClick(menu, context, sourceSlot);
        distributeCarriedStackToQuickMoveTarget(menu, context, sourceSlot, Set.of(temporaryCarriedSlotIndex));
        returnCarriedStackToSource(menu, context, sourceSlot);

        if (menu.getCarried().is(Items.AIR)) {
            containerInputService.performClick(menu, context, temporaryCarriedSlotIndex);
        }
    }

    private Optional<Slot> getTemporaryCarriedSlot(AbstractContainerMenu menu, ItemStack carriedStack, int sourceSlot, List<Integer> targetSlots) {
        var nonTargetSlots = quickMoveTargetResolver.getAllSlots(menu).stream()
                .filter(slot -> !targetSlots.contains(slot))
                .toList();
        var temporarySlot = getFirstEmptySlot(menu, carriedStack, sourceSlot, Set.of(), nonTargetSlots);

        if (temporarySlot.isPresent()) {
            return temporarySlot;
        }

        return getFirstEmptySlot(menu, carriedStack, sourceSlot, Set.of());
    }

    private void distributeCarriedStackToQuickMoveTarget(AbstractContainerMenu menu, ContainerInputService.ContainerInputContext context, int sourceSlot, Set<Integer> excludedSlots) {
        var targetSlots = quickMoveTargetResolver.getQuickMoveTargetSlots(menu, sourceSlot);

        fillMatchingSlots(menu, context, sourceSlot, excludedSlots, targetSlots);

        if (!menu.getCarried().is(Items.AIR)) {
            getFirstEmptySlot(menu, menu.getCarried(), sourceSlot, excludedSlots, targetSlots)
                    .ifPresent(slot -> containerInputService.performClick(menu, context, slot.index));
        }
    }

    private void fillMatchingSlots(AbstractContainerMenu menu, ContainerInputService.ContainerInputContext context, int sourceSlot, Set<Integer> excludedSlots, List<Integer> targetSlots) {
        for (var fillableSlot : getFillableMatchingSlots(menu, menu.getCarried(), sourceSlot, excludedSlots, targetSlots)) {
            if (menu.getCarried().is(Items.AIR)) {
                return;
            }

            containerInputService.performClick(menu, context, fillableSlot.index);
        }
    }

    private void returnCarriedStackToSource(AbstractContainerMenu menu, ContainerInputService.ContainerInputContext context, int sourceSlot) {
        if (!menu.getCarried().is(Items.AIR)) {
            containerInputService.performClick(menu, context, sourceSlot);
        }
    }

    private List<Slot> getFillableMatchingSlots(AbstractContainerMenu menu, ItemStack carriedStack, int sourceSlot, Set<Integer> excludedSlots, List<Integer> targetSlots) {
        return targetSlots.stream()
                .map(menu::getSlot)
                .filter(Slot::isActive)
                .filter(slot -> slot.index != sourceSlot)
                .filter(slot -> !excludedSlots.contains(slot.index))
                .filter(slot -> slot.mayPlace(carriedStack))
                .filter(slot -> ItemStack.isSameItemSameComponents(slot.getItem(), carriedStack))
                .filter(slot -> !isFull(slot, carriedStack))
                .toList();
    }

    private Optional<Slot> getFirstEmptySlot(AbstractContainerMenu menu, ItemStack carriedStack, int sourceSlot, Set<Integer> excludedSlots) {
        return getFirstEmptySlot(menu, carriedStack, sourceSlot, excludedSlots, quickMoveTargetResolver.getAllSlots(menu));
    }

    private Optional<Slot> getFirstEmptySlot(AbstractContainerMenu menu, ItemStack carriedStack, int sourceSlot, Set<Integer> excludedSlots, List<Integer> targetSlots) {
        return targetSlots.stream()
                .map(menu::getSlot)
                .filter(Slot::isActive)
                .filter(slot -> slot.index != sourceSlot)
                .filter(slot -> !excludedSlots.contains(slot.index))
                .filter(slot -> slot.mayPlace(carriedStack))
                .filter(slot -> slot.getItem().is(Items.AIR))
                .findFirst();
    }

    private boolean isFull(Slot slot, ItemStack itemStack) {
        return slot.getItem().getCount() >= slot.getMaxStackSize(itemStack);
    }
}
