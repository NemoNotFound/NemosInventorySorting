package com.nemonotfound.nemos.inventory.sorting.service;

import net.minecraft.world.inventory.AbstractContainerMenu;

public class InventoryService {

    private static InventoryService INSTANCE;

    private final SortingService sortingService;
    private final MergingService mergeService;
    private final SplitQuickMoveService splitQuickMoveService;

    private InventoryService(MergingService mergeService, SortingService sortingService, SplitQuickMoveService splitQuickMoveService) {
        this.mergeService = mergeService;
        this.sortingService = sortingService;
        this.splitQuickMoveService = splitQuickMoveService;
    }

    public static InventoryService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new InventoryService(
                    MergingService.getInstance(),
                    SortingService.getInstance(),
                    SplitQuickMoveService.getInstance()
            );
        }

        return INSTANCE;
    }

    public void handleSorting(AbstractContainerMenu menu, int startIndex, int endIndex) { //TODO: Improve efficiency
        var slotItemsToMerge = sortingService.sortSlotItems(menu, startIndex, endIndex);
        var mergedItems = mergeService.mergeAllItems(menu, slotItemsToMerge);

        var slotItemsToSort = mergedItems ? sortingService.sortSlotItems(menu, startIndex, endIndex) : slotItemsToMerge;
        var slotSwapMap = sortingService.retrieveSlotSwaps(slotItemsToSort, startIndex, endIndex);
        sortingService.sortItemsInInventory(menu, slotSwapMap);
    }

    public void handleSplitQuickMove(AbstractContainerMenu menu, int slot) {
        splitQuickMoveService.handleSplitQuickMove(menu, slot);
    }
}
