package com.nemonotfound.nemos.inventory.sorting.model;

import net.minecraft.world.item.ItemStack;

public record SlotItem(int slotIndex, ItemStack itemStack) {
}
