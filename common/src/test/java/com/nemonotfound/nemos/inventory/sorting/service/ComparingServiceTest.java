package com.nemonotfound.nemos.inventory.sorting.service;

import com.nemonotfound.nemos.inventory.sorting.models.ItemSorting;
import com.nemonotfound.nemos.inventory.sorting.models.SlotItem;
import net.minecraft.world.item.ItemStack;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ComparingServiceTest {

    @Test
    void createsSortMetadataOncePerStack() {
        var itemOrderService = mock(ItemOrderService.class);
        var itemSortingFactory = mock(ItemSortingFactory.class);
        var service = new ComparingService(itemOrderService, itemSortingFactory);
        var firstStack = mock(ItemStack.class);
        var secondStack = mock(ItemStack.class);
        var firstSlot = new SlotItem(0, firstStack);
        var secondSlot = new SlotItem(1, secondStack);
        var itemOrder = Map.<net.minecraft.world.item.Item, Integer>of();

        when(itemOrderService.getItemOrder()).thenReturn(itemOrder);
        when(itemSortingFactory.create(firstStack, itemOrder)).thenReturn(itemSorting(2));
        when(itemSortingFactory.create(secondStack, itemOrder)).thenReturn(itemSorting(1));

        assertThat(service.sort(java.util.List.of(firstSlot, secondSlot)))
                .containsExactly(secondSlot, firstSlot);
        verify(itemSortingFactory).create(firstStack, itemOrder);
        verify(itemSortingFactory).create(secondStack, itemOrder);
    }

    private ItemSorting itemSorting(int itemOrder) {
        return new ItemSorting(itemOrder, 1, "Item", "", "", "", "");
    }
}
