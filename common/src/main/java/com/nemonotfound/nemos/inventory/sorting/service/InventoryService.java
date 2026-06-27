package com.nemonotfound.nemos.inventory.sorting.service;

import net.minecraft.world.inventory.AbstractContainerMenu;

public class InventoryService {

    private static InventoryService INSTANCE;

    private final SortingService sortingService;
    private final MergingService mergeService;
    private final SplitQuickMoveService splitQuickMoveService;
    private final ScrollTransferService scrollTransferService;

    private InventoryService(MergingService mergeService, SortingService sortingService, SplitQuickMoveService splitQuickMoveService, ScrollTransferService scrollTransferService) {
        this.mergeService = mergeService;
        this.sortingService = sortingService;
        this.splitQuickMoveService = splitQuickMoveService;
        this.scrollTransferService = scrollTransferService;
    }

    public static InventoryService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new InventoryService(
                    MergingService.getInstance(),
                    SortingService.getInstance(),
                    SplitQuickMoveService.getInstance(),
                    ScrollTransferService.getInstance()
            );
        }

        return INSTANCE;
    }

    public void handleSorting(AbstractContainerMenu menu, int startIndex, int endIndex) { //TODO: Improve efficiency
        var containerId = menu.containerId;

        var slotItemsToMerge = sortingService.sortSlotItems(menu, startIndex, endIndex);
        mergeService.mergeAllItems(menu, slotItemsToMerge, containerId);

        var slotItemsToSort = sortingService.sortSlotItems(menu, startIndex, endIndex);
        var slotSwapMap = sortingService.retrieveSlotSwapMap(slotItemsToSort, startIndex, endIndex);
        sortingService.sortItemsInInventory(menu, slotSwapMap, containerId);
    }

    public void handleSplitQuickMove(AbstractContainerMenu menu, int slot) {
        splitQuickMoveService.handleSplitQuickMove(menu, slot);
    }

    public boolean handleSingleItemScrollMove(AbstractContainerMenu menu, int slot, double scrollDelta) {
        return scrollTransferService.handleSingleItemScrollMove(menu, slot, scrollDelta);
    }
}
