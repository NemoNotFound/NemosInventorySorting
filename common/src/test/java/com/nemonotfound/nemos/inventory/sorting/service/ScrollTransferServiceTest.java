package com.nemonotfound.nemos.inventory.sorting.service;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.AbstractCraftingMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ScrollTransferServiceTest {

    @Test
    void preservesLastItemWithoutShift() {
        assertThat(ScrollTransferTargetService.hasTransferableCount(stackWithCount(1), false)).isFalse();
    }

    @Test
    void allowsLastItemWithShift() {
        assertThat(ScrollTransferTargetService.hasTransferableCount(stackWithCount(1), true)).isTrue();
    }

    @Test
    void allowsNonStackableResultCount() {
        var resultStack = stackWithCount(1);
        when(resultStack.getMaxStackSize()).thenReturn(1);

        assertThat(ScrollTransferTargetService.hasTransferableCount(resultStack, true)).isTrue();
    }

    @Test
    void usesHorizontalScrollDeltaWhenShiftRemapsTheScrollWheel() {
        assertThat(ScrollTransferService.resolveScrollDelta(-1, 0, true)).isEqualTo(-1);
    }

    @Test
    void ignoresHorizontalScrollWithoutShift() {
        assertThat(ScrollTransferService.resolveScrollDelta(-1, 0, false)).isZero();
    }

    @Test
    void ignoresScrollTransferOnEmptyHoveredSlot() {
        var menu = mock(AbstractContainerMenu.class);
        var hoveredSlot = mock(Slot.class);
        var emptyStack = mock(ItemStack.class);

        when(menu.getSlot(4)).thenReturn(hoveredSlot);
        when(hoveredSlot.getItem()).thenReturn(emptyStack);
        when(emptyStack.isEmpty()).thenReturn(true);

        assertThat(ScrollTransferService.getInstance().handleSingleItemScrollMove(menu, 4, 1, false)).isFalse();
    }

    @Test
    void identifiesTakeOnlySlotAsResultSlot() {
        var resultSlot = mock(Slot.class);
        var resultStack = mock(ItemStack.class);

        when(resultSlot.getItem()).thenReturn(resultStack);
        when(resultStack.isEmpty()).thenReturn(false);
        when(resultSlot.mayPlace(resultStack)).thenReturn(false);

        assertThat(ScrollTransferTargetService.isResultSlot(resultSlot)).isTrue();
        assertThat(ScrollTransferTargetService.shouldAllowLastItem(resultSlot, false)).isTrue();
    }

    @Test
    void ordinarySlotStillRequiresShiftForLastItem() {
        var slot = mock(Slot.class);
        var stack = mock(ItemStack.class);

        when(slot.getItem()).thenReturn(stack);
        when(stack.isEmpty()).thenReturn(false);
        when(slot.mayPlace(stack)).thenReturn(true);

        assertThat(ScrollTransferTargetService.shouldAllowLastItem(slot, false)).isFalse();
        assertThat(ScrollTransferTargetService.shouldAllowLastItem(slot, true)).isTrue();
    }

    @Test
    void identifiesCraftingInputSlotsAsCraftingMenuSlots() {
        var menu = mock(AbstractCraftingMenu.class);
        var resultSlot = mock(Slot.class);
        var inputSlot = mock(Slot.class);

        when(menu.getResultSlot()).thenReturn(resultSlot);
        when(menu.getInputGridSlots()).thenReturn(List.of(inputSlot));

        assertThat(ScrollTransferTargetService.isCraftingMenuSlot(menu, inputSlot)).isTrue();
    }

    @Test
    void doesNotTreatPlayerInventorySlotAsCraftingMenuSlot() {
        var menu = mock(AbstractCraftingMenu.class);
        var resultSlot = mock(Slot.class);
        var playerSlot = mock(Slot.class);

        when(menu.getResultSlot()).thenReturn(resultSlot);
        when(menu.getInputGridSlots()).thenReturn(List.of());

        assertThat(ScrollTransferTargetService.isCraftingMenuSlot(menu, playerSlot)).isFalse();
    }

    private ItemStack stackWithCount(int count) {
        var itemStack = mock(ItemStack.class);

        when(itemStack.getCount()).thenReturn(count);
        when(itemStack.getMaxStackSize()).thenReturn(64);

        return itemStack;
    }
}
