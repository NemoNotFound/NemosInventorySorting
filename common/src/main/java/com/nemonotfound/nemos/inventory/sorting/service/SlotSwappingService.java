package com.nemonotfound.nemos.inventory.sorting.service;

import com.nemonotfound.nemos.inventory.sorting.NemosInventorySortingClientCommon;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import static com.nemonotfound.nemos.inventory.sorting.Constants.NEMOS_BACKPACKS_MOD_ID;

public class SlotSwappingService {

    private static SlotSwappingService INSTANCE;

    public static SlotSwappingService getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new SlotSwappingService();
        }

        return INSTANCE;
    }

    public void performSlotSwap(AbstractContainerMenu menu, MultiPlayerGameMode gameMode, int containerId, int slot, int targetSlot, LocalPlayer player) {
        var sourceStack = menu.getSlot(slot).getItem();
        var targetStack = menu.getSlot(targetSlot).getItem();

        if (areEquivalentStacks(sourceStack, targetStack)) {
            return;
        }

        if (shouldReverseSwap(sourceStack, targetStack)) {
            var sourceSlot = slot;
            slot = targetSlot;
            targetSlot = sourceSlot;
        }

        swapItems(menu, gameMode, containerId, slot, targetSlot, player);
    }

    public void performStackMerge(AbstractContainerMenu menu, MultiPlayerGameMode gameMode, int containerId, int slot, int targetSlot, LocalPlayer player) {
        swapItems(menu, gameMode, containerId, slot, targetSlot, player);
    }

    private void swapItems(AbstractContainerMenu menu, MultiPlayerGameMode gameMode, int containerId, int sourceSlot, int targetSlot, LocalPlayer player) {
        pickUpItem(menu, gameMode, containerId, sourceSlot, player);
        pickUpItem(menu, gameMode, containerId, targetSlot, player);

        if (!menu.getCarried().isEmpty()) {
            pickUpItem(menu, gameMode, containerId, sourceSlot, player);
        }
    }

    private boolean areEquivalentStacks(ItemStack source, ItemStack target) {
        return ItemStack.isSameItemSameTags(source, target) && source.getCount() == target.getCount();
    }

    private boolean shouldReverseSwap(ItemStack source, ItemStack target) {
        return ItemStack.isSameItemSameTags(source, target)
                && source.getCount() < source.getMaxStackSize()
                && target.getCount() >= target.getMaxStackSize();
    }

    private void pickUpItem(AbstractContainerMenu menu, MultiPlayerGameMode gameMode, int containerId, int slotIndex, LocalPlayer player) {
        var mouseButton = getMouseButton(menu.getCarried(), menu.getSlot(slotIndex));
        gameMode.handleInventoryMouseClick(containerId, slotIndex, mouseButton, ClickType.PICKUP, player);
    }

    private int getMouseButton(ItemStack carriedStack, Slot slot) {
        if ((!carriedStack.isEmpty() && canConsumeStack(slot.getItem()))
                || (canConsumeStack(carriedStack) && !slot.getItem().isEmpty())) {
            return 1;
        }

        return 0;
    }

    private boolean canConsumeStack(ItemStack stack) {
        return isBackpack(stack) || stack.is(Items.BUNDLE);
    }

    private boolean isBackpack(ItemStack stack) {
        if (!NemosInventorySortingClientCommon.MOD_LOADER_HELPER.isModLoaded(NEMOS_BACKPACKS_MOD_ID)) {
            return false;
        }

        return isBackpack(stack, "com.nemonotfound.nemos.backpacks.tags.BackpackItemTags")
                || isBackpack(stack, "com.devnemo.nemos.backpacks.tags.BackpackItemTags");
    }

    private boolean isBackpack(ItemStack stack, String tagClassName) {
        try {
            var tagClass = Class.forName(tagClassName);
            var field = tagClass.getDeclaredField("BACKPACKS");

            @SuppressWarnings("unchecked")
            var tag = (TagKey<Item>) field.get(null);
            return stack.is(tag);
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException ignored) {
            return false;
        }
    }
}
