package com.nemonotfound.nemos.inventory.sorting.service;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SlotSwapServiceTest {

    @Test
    void reversesSwapWhenPartialStackTargetsFullMatchingStack() {
        var partialStack = stackWithCount(8);
        var fullStack = stackWithCount(64);

        try (var itemStack = mockStatic(ItemStack.class)) {
            itemStack.when(() -> ItemStack.isSameItemSameComponents(partialStack, fullStack)).thenReturn(true);
            itemStack.when(() -> ItemStack.isSameItemSameComponents(fullStack, partialStack)).thenReturn(true);

            assertThat(SlotSwapService.shouldReverseSwap(partialStack, fullStack)).isTrue();
            assertThat(SlotSwapService.shouldReverseSwap(fullStack, partialStack)).isFalse();
        }
    }

    @Test
    void skipsEquivalentStacks() {
        var firstStack = stackWithCount(64);
        var secondStack = stackWithCount(64);

        try (var itemStack = mockStatic(ItemStack.class)) {
            itemStack.when(() -> ItemStack.isSameItemSameComponents(firstStack, secondStack)).thenReturn(true);

            assertThat(SlotSwapService.areEquivalentStacks(firstStack, secondStack)).isTrue();
        }
    }

    @Test
    void stackMergeExecutesPickupSequenceWithoutEquivalenceCheck() {
        var service = spy(new SlotSwapService(mock(ContainerInputService.class)));
        var menu = mock(AbstractContainerMenu.class);
        LocalPlayer player = null;
        var emptyCursor = mock(ItemStack.class);

        when(menu.getCarried()).thenReturn(emptyCursor);
        when(emptyCursor.isEmpty()).thenReturn(true);
        doNothing().when(service).pickUpItem(menu, 3);
        doNothing().when(service).pickUpItem(menu, 7);

        service.mergeStack(menu, 3, 7);

        verify(service).pickUpItem(menu, 3);
        verify(service).pickUpItem(menu, 7);
    }

    private ItemStack stackWithCount(int count) {
        var itemStack = mock(ItemStack.class);

        when(itemStack.getCount()).thenReturn(count);
        when(itemStack.getMaxStackSize()).thenReturn(64);

        return itemStack;
    }
}
