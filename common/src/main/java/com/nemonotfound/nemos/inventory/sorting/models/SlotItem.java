package com.nemonotfound.nemos.inventory.sorting.models;

import net.minecraft.world.item.ItemStack;

public record SlotItem(int slotIndex, ItemStack itemStack) {
}
