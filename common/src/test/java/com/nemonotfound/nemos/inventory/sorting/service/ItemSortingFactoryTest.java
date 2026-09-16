package com.nemonotfound.nemos.inventory.sorting.service;

import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ItemSortingFactoryTest {

    @Test
    void createsDeterministicSortKeyRegardlessOfComponentOrder() {
        var pattern = componentType("tropical_fish/pattern");
        var color = componentType("tropical_fish/base_color");
        var firstComponents = DataComponentMap.builder()
                .set(pattern, "flopper")
                .set(color, "orange")
                .build();
        var secondComponents = DataComponentMap.builder()
                .set(color, "orange")
                .set(pattern, "flopper")
                .build();

        assertThat(ItemSortingFactory.componentDataSortKey(firstComponents))
                .isEqualTo(ItemSortingFactory.componentDataSortKey(secondComponents));
    }

    @Test
    void distinguishesDifferentComponentData() {
        var pattern = componentType("tropical_fish/pattern");
        var firstComponents = DataComponentMap.builder().set(pattern, "flopper").build();
        var secondComponents = DataComponentMap.builder().set(pattern, "sunstreak").build();

        assertThat(containerComponentDataSortKey(firstComponents))
                .isNotEqualTo(containerComponentDataSortKey(secondComponents));
    }

    @Test
    void distinguishesContainerContentsBySlotAndItemCount() {
        var container = this.<ItemContainerContents>componentType("minecraft:container");
        var item = mock(Item.class);
        var oneItem = containedStack(item, 1);
        var twoItems = containedStack(item, 2);
        var firstContents = mock(ItemContainerContents.class);
        var secondContents = mock(ItemContainerContents.class);
        when(firstContents.itemCopies()).thenAnswer(_ -> Stream.of(oneItem));
        when(secondContents.itemCopies()).thenAnswer(_ -> Stream.of(twoItems));
        var firstComponents = DataComponentMap.builder().set(container, firstContents).build();
        var secondComponents = DataComponentMap.builder().set(container, secondContents).build();

        assertThat(containerComponentDataSortKey(firstComponents))
                .isNotEqualTo(containerComponentDataSortKey(secondComponents));
    }

    @Test
    void preservesEmptySlotsWhenSortingContainerContents() {
        var container = this.<ItemContainerContents>componentType("minecraft:container");
        var item = mock(Item.class);
        var emptyStack = mock(ItemStack.class);
        var itemStack = containedStack(item, 1);
        var firstContents = mock(ItemContainerContents.class);
        var secondContents = mock(ItemContainerContents.class);
        when(emptyStack.isEmpty()).thenReturn(true);
        when(firstContents.itemCopies()).thenAnswer(_ -> Stream.of(itemStack));
        when(secondContents.itemCopies()).thenAnswer(_ -> Stream.of(emptyStack, itemStack));
        var firstComponents = DataComponentMap.builder().set(container, firstContents).build();
        var secondComponents = DataComponentMap.builder().set(container, secondContents).build();

        assertThat(containerComponentDataSortKey(firstComponents))
                .isNotEqualTo(containerComponentDataSortKey(secondComponents));
    }

    @Test
    void distinguishesContainedItemsByStableItemId() {
        var container = this.<ItemContainerContents>componentType("minecraft:container");
        var firstContents = mock(ItemContainerContents.class);
        var secondContents = mock(ItemContainerContents.class);
        var firstItem = mock(Item.class);
        var secondItem = mock(Item.class);
        when(firstItem.getDescriptionId()).thenReturn("minecraft:stone");
        when(secondItem.getDescriptionId()).thenReturn("minecraft:dirt");
        when(firstContents.itemCopies()).thenAnswer(_ -> Stream.of(containedStack(firstItem, 1)));
        when(secondContents.itemCopies()).thenAnswer(_ -> Stream.of(containedStack(secondItem, 1)));
        var firstComponents = DataComponentMap.builder().set(container, firstContents).build();
        var secondComponents = DataComponentMap.builder().set(container, secondContents).build();

        assertThat(containerComponentDataSortKey(firstComponents))
                .isNotEqualTo(containerComponentDataSortKey(secondComponents));
    }

    private ItemStack containedStack(Item item, int count) {
        var itemStack = mock(ItemStack.class);

        when(itemStack.isEmpty()).thenReturn(false);
        when(itemStack.getItem()).thenReturn(item);
        when(itemStack.getCount()).thenReturn(count);
        when(itemStack.getComponents()).thenReturn(DataComponentMap.EMPTY);

        return itemStack;
    }

    private String containerComponentDataSortKey(DataComponentMap components) {
        return ItemSortingFactory.componentDataSortKey(components, Item::getDescriptionId);
    }

    @SuppressWarnings("unchecked")
    private <T> DataComponentType<T> componentType(String name) {
        var componentType = (DataComponentType<T>) mock(DataComponentType.class);

        when(componentType.toString()).thenReturn(name);

        return componentType;
    }
}
