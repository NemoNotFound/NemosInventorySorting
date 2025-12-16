package com.nemonotfound.nemos.inventory.sorting.client;

import com.nemonotfound.nemos.inventory.sorting.Constants;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

import static com.nemonotfound.nemos.inventory.sorting.Constants.MOD_ID;

public class InventorySortingCategories {

    public static final KeyMapping.Category NEMOS_INVENTORY_SORTING = registerCategory();

    public static void init() {}

    private static KeyMapping.Category registerCategory() {
        return KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MOD_ID, Constants.MOD_ID));
    }
}
