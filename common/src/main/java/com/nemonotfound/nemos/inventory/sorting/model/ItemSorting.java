package com.nemonotfound.nemos.inventory.sorting.model;

import java.util.Comparator;

public record ItemSorting(int itemOrder, int stackCount, String displayName, String enchantments, String jukeboxSong, String potion, String nbtData) {

    public static final Comparator<ItemSorting> COMPARATOR = Comparator
            .comparingInt(ItemSorting::itemOrder)
            .thenComparing(ItemSorting::stackCount, Comparator.reverseOrder())
            .thenComparing(ItemSorting::displayName)
            .thenComparing(ItemSorting::enchantments)
            .thenComparing(ItemSorting::jukeboxSong)
            .thenComparing(ItemSorting::potion)
            .thenComparing(ItemSorting::nbtData);
}
