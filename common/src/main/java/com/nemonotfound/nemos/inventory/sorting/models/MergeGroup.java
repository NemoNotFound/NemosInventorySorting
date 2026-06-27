package com.nemonotfound.nemos.inventory.sorting.models;

import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.item.Item;

public record MergeGroup(Item item, DataComponentMap components) {
}
