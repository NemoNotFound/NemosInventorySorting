package com.nemonotfound.nemos.inventory.sorting.service;

import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MergingServiceTest {

    @Test
    void groupsStacksOfTheSameItemAndComponentsForMerging() {
        var item = mock(Item.class);
        var components = mock(DataComponentMap.class);
        var firstStack = stack(item, components, 32);
        var secondStack = stack(item, components, 32);

        assertThat(MergingService.ItemGroup.from(firstStack))
                .isEqualTo(MergingService.ItemGroup.from(secondStack));
    }

    @Test
    void doesNotGroupDifferentComponentlessItemsTogether() {
        var components = DataComponentMap.EMPTY;
        var firstStack = stack(mock(Item.class), components, 32);
        var secondStack = stack(mock(Item.class), components, 32);

        assertThat(MergingService.ItemGroup.from(firstStack))
                .isNotEqualTo(MergingService.ItemGroup.from(secondStack));
    }

    private ItemStack stack(Item item, DataComponentMap components, int count) {
        var itemStack = mock(ItemStack.class);

        when(itemStack.getItem()).thenReturn(item);
        when(itemStack.getComponents()).thenReturn(components);
        when(itemStack.getCount()).thenReturn(count);

        return itemStack;
    }
}
