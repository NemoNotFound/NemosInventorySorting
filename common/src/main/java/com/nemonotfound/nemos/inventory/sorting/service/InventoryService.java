package com.nemonotfound.nemos.inventory.sorting.service;

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

    public void handleSorting(AbstractContainerMenu menu, int startIndex, int endIndex) { //TODO: Improve efficiency
        var containerId = menu.containerId;

        var slotItemsToMerge = sortingService.sortSlotItems(menu, startIndex, endIndex);
        mergeService.mergeAllItems(menu, slotItemsToMerge, containerId);

        var slotItemsToSort = sortingService.sortSlotItems(menu, startIndex, endIndex);
        var slotSwapMap = sortingService.retrieveSlotSwapMap(slotItemsToSort, startIndex, endIndex);
        sortingService.sortItemsInInventory(menu, slotSwapMap, containerId);
    }
}
