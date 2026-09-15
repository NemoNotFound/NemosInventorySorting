package com.nemonotfound.nemos.inventory.sorting.service;

import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class ScrollTransferService {
    private static final long TRANSFER_COOLDOWN_NANOS = 50_000_000L;
    private static ScrollTransferService INSTANCE;

    private final ContainerInputService containerInputService;
    private final ScrollTransferTargetService targetService;
    private final ContainerItemTransferService itemTransferService;
    private AbstractContainerMenu lastTransferMenu;
    private long nextTransferTime;

    private ScrollTransferService(ContainerInputService containerInputService, ScrollTransferTargetService targetService, ContainerItemTransferService itemTransferService) {
        this.containerInputService = containerInputService;
        this.targetService = targetService;
        this.itemTransferService = itemTransferService;
    }

    public static ScrollTransferService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ScrollTransferService(ContainerInputService.getInstance(), ScrollTransferTargetService.getInstance(), ContainerItemTransferService.getInstance());
        }
        return INSTANCE;
    }

    public static double resolveScrollDelta(double scrollX, double scrollY, boolean shiftDown) {
        return scrollY != 0 ? scrollY : shiftDown ? scrollX : 0;
    }

    public boolean handleSingleItemScrollMove(AbstractContainerMenu menu, int hoveredSlotIndex, double scrollDelta, boolean allowLastItem) {
        if (!canTransfer(menu, hoveredSlotIndex, scrollDelta)) {
            return false;
        }

        var currentTime = System.nanoTime();
        if (menu == lastTransferMenu && currentTime < nextTransferTime) {
            return false;
        }

        var isTransferred = containerInputService.getContext()
                .flatMap(context -> targetService.getTransfer(menu, hoveredSlotIndex, scrollDelta, allowLastItem)
                        .map(transfer -> itemTransferService.transfer(menu, context, transfer)))
                .orElse(false);

        if (isTransferred) {
            lastTransferMenu = menu;
            nextTransferTime = currentTime + TRANSFER_COOLDOWN_NANOS;
        }
        return isTransferred;
    }

    private boolean canTransfer(AbstractContainerMenu menu, int hoveredSlotIndex, double scrollDelta) {
        return scrollDelta != 0
                && !(menu instanceof CreativeModeInventoryScreen.ItemPickerMenu)
                && !menu.getSlot(hoveredSlotIndex).getItem().isEmpty();
    }
}
