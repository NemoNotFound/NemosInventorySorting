package com.nemonotfound.nemos.inventory.sorting.client;

import com.nemonotfound.nemos.inventory.sorting.SortingCommonClient;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;

import java.util.function.Supplier;

import static com.nemonotfound.nemos.inventory.sorting.Constants.MOD_ID;
import static com.nemonotfound.nemos.inventory.sorting.client.SortingKeymappingCategories.NEMOS_INVENTORY_SORTING;

public class SortingKeyMappings {

    public static Supplier<KeyMapping> SORT = registerKeyMapping(new KeyMapping(
            String.format("%s.key.sort", MOD_ID),
            InputConstants.UNKNOWN.getValue(),
            NEMOS_INVENTORY_SORTING
    ));
    public static Supplier<KeyMapping> MOVE_SAME = registerKeyMapping(new KeyMapping(
            String.format("%s.key.moveSame", MOD_ID),
            InputConstants.UNKNOWN.getValue(),
            NEMOS_INVENTORY_SORTING
    ));
    public static Supplier<KeyMapping> MOVE_ALL = registerKeyMapping(new KeyMapping(
            String.format("%s.key.moveAll", MOD_ID),
            InputConstants.UNKNOWN.getValue(),
            NEMOS_INVENTORY_SORTING
    ));
    public static Supplier<KeyMapping> DROP_ALL = registerKeyMapping(new KeyMapping(
            String.format("%s.key.dropAll", MOD_ID),
            InputConstants.UNKNOWN.getValue(),
            NEMOS_INVENTORY_SORTING
    ));
    public static Supplier<KeyMapping> TOGGLE_FILTER_PERSISTENCE = registerKeyMapping(new KeyMapping(
            String.format("%s.key.toggleFilterPersistence", MOD_ID),
            InputConstants.UNKNOWN.getValue(),
            NEMOS_INVENTORY_SORTING
    ));
    public static Supplier<KeyMapping> QUICK_SEARCH = registerKeyMapping(new KeyMapping(
            String.format("%s.key.quickSearch", MOD_ID),
            InputConstants.KEY_F,
            NEMOS_INVENTORY_SORTING
    ));

    private static Supplier<KeyMapping> registerKeyMapping(KeyMapping keyMapping) {
        return SortingCommonClient.REGISTRY_HELPER.registerKeyMapping(keyMapping);
    }

    public static void init() {}
}
