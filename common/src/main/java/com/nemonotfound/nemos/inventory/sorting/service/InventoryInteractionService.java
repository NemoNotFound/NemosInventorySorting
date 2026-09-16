package com.nemonotfound.nemos.inventory.sorting.service;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class InventoryInteractionService {

    private static final long SCROLL_COOLDOWN = 50_000_000L;
    private static InventoryInteractionService INSTANCE;

    private final QuickMoveTargetResolver targetResolver = QuickMoveTargetResolver.INSTANCE;
    private final ScrollTransferTargetService scrollTargetService = ScrollTransferTargetService.INSTANCE;
    private AbstractContainerMenu lastScrollMenu;
    private long nextScrollTime;

    public static InventoryInteractionService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new InventoryInteractionService();
        }
        return INSTANCE;
    }

    public boolean splitQuickMove(AbstractContainerMenu menu, Slot source) {
        if (source == null || !canInteract(menu) || source.getItem().getCount() < 2 || source.getItem().getMaxStackSize() == 1) {
            return false;
        }

        var temporarySlot = storeCarriedStack(menu, source.index, targetResolver.getTargetSlots(menu, source.index));
        if (!menu.getCarried().isEmpty()) {
            return false;
        }

        click(menu, source.index, 1);
        distributeCarried(menu, source.index, temporarySlot.map(slot -> Set.of(slot.index)).orElseGet(Set::of));
        returnCarried(menu, source.index);
        restoreCarriedStack(menu, temporarySlot.orElse(null));
        return true;
    }

    public boolean scrollTransfer(AbstractContainerMenu menu, Slot hoveredSlot, double delta, boolean shiftDown) {
        if (!canInteract(menu) || delta == 0 || hoveredSlot.getItem().isEmpty()) {
            return false;
        }

        var now = System.nanoTime();
        if (menu == lastScrollMenu && now < nextScrollTime) {
            return false;
        }

        var transfer = scrollTargetService.getTransfer(menu, hoveredSlot, delta, shiftDown);
        if (transfer.isEmpty() || !performTransfer(menu, transfer.get())) {
            return false;
        }

        lastScrollMenu = menu;
        nextScrollTime = now + SCROLL_COOLDOWN;
        return true;
    }

    private boolean performTransfer(AbstractContainerMenu menu, ScrollTransferTargetService.Transfer transfer) {
        if (transfer.result()) {
            if (!menu.getCarried().isEmpty()) {
                return false;
            }

            click(menu, transfer.source().index, 0);
            click(menu, transfer.target().index, 0);
            return menu.getCarried().isEmpty();
        }

        var temporarySlot = scrollTargetService.getTemporaryCarriedSlot(menu, menu.getCarried(), transfer.source().index, transfer.target().index);
        if (!menu.getCarried().isEmpty() && temporarySlot.isEmpty()) {
            return false;
        }

        temporarySlot.ifPresent(slot -> click(menu, slot.index, 0));
        click(menu, transfer.source().index, 1);
        click(menu, transfer.target().index, 1);
        returnCarried(menu, transfer.source().index);
        restoreCarriedStack(menu, temporarySlot.orElse(null));
        return true;
    }

    private Optional<Slot> storeCarriedStack(AbstractContainerMenu menu, int source, List<Integer> targetSlots) {
        if (menu.getCarried().isEmpty()) {
            return Optional.empty();
        }

        var nonTargetSlots = targetResolver.allSlots(menu).stream().filter(slot -> !targetSlots.contains(slot)).toList();
        var temporarySlot = firstEmpty(menu, menu.getCarried(), source, Set.of(), nonTargetSlots)
                .or(() -> firstEmpty(menu, menu.getCarried(), source, Set.of(), targetResolver.allSlots(menu)));
        temporarySlot.ifPresent(slot -> click(menu, slot.index, 0));
        return temporarySlot;
    }

    private void distributeCarried(AbstractContainerMenu menu, int source, Set<Integer> excluded) {
        var targets = targetResolver.getTargetSlots(menu, source);
        for (var index : targets) {
            var target = menu.getSlot(index);
            if (menu.getCarried().isEmpty()) {
                return;
            }
            if (index != source && !excluded.contains(index) && canFill(target, menu.getCarried())) {
                click(menu, index, 0);
            }
        }

        if (!menu.getCarried().isEmpty()) {
            firstEmpty(menu, menu.getCarried(), source, excluded, targets).ifPresent(slot -> click(menu, slot.index, 0));
        }
    }

    private Optional<Slot> firstEmpty(AbstractContainerMenu menu, ItemStack stack, int source, Set<Integer> excluded, List<Integer> slots) {
        return slots.stream().map(menu::getSlot)
                .filter(slot -> slot.index != source && !excluded.contains(slot.index))
                .filter(slot -> slot.isActive() && slot.mayPlace(stack) && slot.getItem().isEmpty())
                .findFirst();
    }

    private boolean canFill(Slot slot, ItemStack stack) {
        return slot.isActive() && slot.mayPlace(stack)
                && ItemStack.isSameItemSameTags(slot.getItem(), stack)
                && slot.getItem().getCount() < slot.getMaxStackSize(stack);
    }

    private boolean canInteract(AbstractContainerMenu menu) {
        var minecraft = Minecraft.getInstance();
        return minecraft.player != null && minecraft.gameMode != null
                && !(menu instanceof CreativeModeInventoryScreen.ItemPickerMenu);
    }

    private void restoreCarriedStack(AbstractContainerMenu menu, Slot temporarySlot) {
        if (menu.getCarried().isEmpty() && temporarySlot != null) {
            click(menu, temporarySlot.index, 0);
        }
    }

    private void returnCarried(AbstractContainerMenu menu, int source) {
        if (!menu.getCarried().isEmpty()) {
            click(menu, source, 0);
        }
    }

    private void click(AbstractContainerMenu menu, int slot, int button) {
        var minecraft = Minecraft.getInstance();
        minecraft.gameMode.handleInventoryMouseClick(menu.containerId, slot, button, ClickType.PICKUP, minecraft.player);
    }
}
