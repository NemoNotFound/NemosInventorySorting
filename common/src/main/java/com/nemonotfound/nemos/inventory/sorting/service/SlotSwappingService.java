package com.nemonotfound.nemos.inventory.sorting.service;

import com.nemonotfound.nemos.inventory.sorting.NemosInventorySortingClientCommon;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
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
        pickUpItem(menu, gameMode, containerId, slot, player);
        pickUpItem(menu, gameMode, containerId, targetSlot, player);
        pickUpItem(menu, gameMode, containerId, slot, player);
    }

    private void pickUpItem(AbstractContainerMenu menu, MultiPlayerGameMode gameMode, int containerId, int slot, LocalPlayer player) {
        var cursorStack = player.containerMenu.getCarried();
        var itemSlot = menu.getSlot(slot);
        var mouseButton = 0;

        if ((!cursorStack.is(Items.AIR) && canBeFilledWithPrimaryClick(itemSlot.getItem())) ||
                (canBeFilledWithPrimaryClick(cursorStack) && !itemSlot.getItem().is(Items.AIR))) {
            mouseButton = 1;
        }

        gameMode.handleInventoryMouseClick(containerId, slot, mouseButton, ClickType.PICKUP, player);
    }

    private boolean canBeFilledWithPrimaryClick(ItemStack itemStack) {
        return isBackpack(itemStack) || itemStack.is(ItemTags.BUNDLES);
    }

    private boolean isBackpack(ItemStack itemStack) {
        if (!NemosInventorySortingClientCommon.MOD_LOADER_HELPER.isModLoaded(NEMOS_BACKPACKS_MOD_ID)) {
            return false;
        }

        try {
            var itemTagClass = Class.forName("com.nemonotfound.nemos.backpacks.tags.BackpackItemTags");
            var field = itemTagClass.getDeclaredField("BACKPACKS");

            @SuppressWarnings("unchecked")
            TagKey<Item> tagKey = (TagKey<Item>) field.get(null);

            return itemStack.is(tagKey);

        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException ignored) {
            return false;
        }
    }
}
