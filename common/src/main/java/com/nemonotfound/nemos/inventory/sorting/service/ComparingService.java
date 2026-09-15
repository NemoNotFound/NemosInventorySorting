package com.nemonotfound.nemos.inventory.sorting.service;

import com.nemonotfound.nemos.inventory.sorting.models.ItemSorting;
import com.nemonotfound.nemos.inventory.sorting.models.SlotItem;

import java.util.List;

public class ComparingService {

    private static ComparingService INSTANCE;

    private final ItemOrderService itemOrderService;
    private final ItemSortingFactory itemSortingFactory;

    ComparingService(ItemOrderService itemOrderService, ItemSortingFactory itemSortingFactory) {
        this.itemOrderService = itemOrderService;
        this.itemSortingFactory = itemSortingFactory;
    }

    public static ComparingService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ComparingService(ItemOrderService.getInstance(), ItemSortingFactory.INSTANCE);
        }

        return INSTANCE;
    }

    public List<SlotItem> sort(List<SlotItem> slotItems) {
        var itemOrder = itemOrderService.getItemOrder();

        return slotItems.stream()
                .map(slotItem -> new SortableSlotItem(slotItem, itemSortingFactory.create(slotItem.itemStack(), itemOrder)))
                .sorted((first, second) -> ItemSorting.COMPARATOR.compare(first.itemSorting(), second.itemSorting()))
                .map(SortableSlotItem::slotItem)
                .toList();
    }

    private record SortableSlotItem(SlotItem slotItem, ItemSorting itemSorting) {
    }
}
