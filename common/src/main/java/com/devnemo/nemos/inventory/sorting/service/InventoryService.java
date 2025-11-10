package com.devnemo.nemos.inventory.sorting.service;

import net.minecraft.world.inventory.AbstractContainerMenu;

public class InventoryService {

    private static InventoryService INSTANCE;

    private final SortingService sortingService;
    private final MergingService mergeService;

    private InventoryService(MergingService mergeService, SortingService sortingService) {
        this.mergeService = mergeService;
        this.sortingService = sortingService;
    }

    public static InventoryService getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new InventoryService(MergingService.getInstance(), SortingService.getInstance());
        }

        return INSTANCE;
    }

    public void handleSorting(AbstractContainerMenu menu, int startIndex, int endIndex) {
        var containerId = menu.containerId;

        handleMerging(menu, startIndex, endIndex, containerId);

        var slotItemsToSort = sortingService.sortSlotItems(menu, startIndex, endIndex);
        var slotSwapMap = sortingService.retrieveSlotSwapMap(slotItemsToSort, startIndex);
        sortingService.sortItemsInInventory(menu, slotSwapMap, containerId);

        handleMerging(menu, startIndex, endIndex, containerId);
    }

    private void handleMerging(AbstractContainerMenu menu, int startIndex, int endIndex, int containerId) {
        var slotItemsToMerge = sortingService.sortSlotItems(menu, startIndex, endIndex);
        mergeService.mergeAllItems(menu, slotItemsToMerge, containerId);
    }
}
