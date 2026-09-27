package com.nemonotfound.nemos.inventory.sorting.service;

import com.nemonotfound.nemos.inventory.sorting.SortingCommonClient;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import static com.nemonotfound.nemos.inventory.sorting.service.ContainerInputService.PRIMARY_MOUSE_BUTTON;
import static com.nemonotfound.nemos.inventory.sorting.service.ContainerInputService.SECONDARY_MOUSE_BUTTON;

public class SlotSwapService {

    private static SlotSwapService INSTANCE;

    private final ContainerInputService containerInputService;

    SlotSwapService(ContainerInputService containerInputService) {
        this.containerInputService = containerInputService;
    }

    public static SlotSwapService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SlotSwapService(ContainerInputService.getInstance());
        }

        return INSTANCE;
    }

    public void performSlotSwap(AbstractContainerMenu menu, int slot, int targetSlot) {
        var sourceStack = menu.getSlot(slot).getItem();
        var targetStack = menu.getSlot(targetSlot).getItem();

        if (areEquivalentStacks(sourceStack, targetStack)) {
            return;
        }

        if (shouldReverseSwap(sourceStack, targetStack)) {
            int sourceSlot = slot;
            slot = targetSlot;
            targetSlot = sourceSlot;
        }

        swapItems(menu, slot, targetSlot);
    }

    void mergeStack(AbstractContainerMenu menu, int sourceSlot, int targetSlot) {
        swapItems(menu, sourceSlot, targetSlot);
    }

    private void swapItems(AbstractContainerMenu menu, int sourceSlot, int targetSlot) {
        pickUpItem(menu, sourceSlot);
        pickUpItem(menu, targetSlot);

        if (!menu.getCarried().isEmpty()) {
            pickUpItem(menu, sourceSlot);
        }
    }

    static boolean areEquivalentStacks(ItemStack sourceStack, ItemStack targetStack) {
        return ItemStack.isSameItemSameComponents(sourceStack, targetStack) &&
                sourceStack.getCount() == targetStack.getCount();
    }

    static boolean shouldReverseSwap(ItemStack sourceStack, ItemStack targetStack) {
        return ItemStack.isSameItemSameComponents(sourceStack, targetStack) &&
                sourceStack.getCount() < sourceStack.getMaxStackSize() &&
                targetStack.getCount() >= targetStack.getMaxStackSize();
    }

    void pickUpItem(AbstractContainerMenu menu, int slot) {
        var cursorStack = menu.getCarried();
        var itemSlot = menu.getSlot(slot);

        containerInputService.getContext()
                .ifPresent(
                        context -> containerInputService.pickup(menu, context, slot, getMouseButton(cursorStack, itemSlot))
                );
    }

    private int getMouseButton(ItemStack cursorStack, Slot slot) {
        var targetStack = slot.getItem();

        if (cursorStack.isEmpty() || targetStack.isEmpty()) {
            return PRIMARY_MOUSE_BUTTON;
        }

        if (cursorStack.getMaxStackSize() == 1 || targetStack.getMaxStackSize() == 1) {
            return SECONDARY_MOUSE_BUTTON;
        }

        return PRIMARY_MOUSE_BUTTON;
    }

}
