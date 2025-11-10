package com.nemonotfound.nemos.inventory.sorting.client;

import com.nemonotfound.nemos.inventory.sorting.NemosInventorySortingClientCommon;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

import java.util.function.Supplier;

import static com.nemonotfound.nemos.inventory.sorting.Constants.MOD_ID;
import static com.nemonotfound.nemos.inventory.sorting.client.InventorySortingCategories.NEMOS_INVENTORY_SORTING;

public class InventorySortingKeyMappings {

    public static Supplier<KeyMapping> SORT = registerKeyMapping(new KeyMapping(
            String.format("%s.key.sort", MOD_ID),
            GLFW.GLFW_KEY_UNKNOWN,
            NEMOS_INVENTORY_SORTING
    ));
    public static Supplier<KeyMapping> SORT_INVENTORY = registerKeyMapping(new KeyMapping(
            String.format("%s.key.sortInventory", MOD_ID),
            GLFW.GLFW_KEY_UNKNOWN,
            NEMOS_INVENTORY_SORTING
    ));
    public static Supplier<KeyMapping> MOVE_SAME = registerKeyMapping(new KeyMapping(
            String.format("%s.key.moveSame", MOD_ID),
            GLFW.GLFW_KEY_UNKNOWN,
            NEMOS_INVENTORY_SORTING
    ));
    public static Supplier<KeyMapping> MOVE_SAME_INVENTORY = registerKeyMapping(new KeyMapping(
            String.format("%s.key.moveSameInventory", MOD_ID),
            GLFW.GLFW_KEY_UNKNOWN,
            NEMOS_INVENTORY_SORTING
    ));
    public static Supplier<KeyMapping> MOVE_ALL = registerKeyMapping(new KeyMapping(
            String.format("%s.key.moveAll", MOD_ID),
            GLFW.GLFW_KEY_UNKNOWN,
            NEMOS_INVENTORY_SORTING
    ));
    public static Supplier<KeyMapping> MOVE_ALL_INVENTORY = registerKeyMapping(new KeyMapping(
            String.format("%s.key.moveAllInventory", MOD_ID),
            GLFW.GLFW_KEY_UNKNOWN,
            NEMOS_INVENTORY_SORTING
    ));
    public static Supplier<KeyMapping> DROP_ALL = registerKeyMapping(new KeyMapping(
            String.format("%s.key.dropAll", MOD_ID),
            GLFW.GLFW_KEY_UNKNOWN,
            NEMOS_INVENTORY_SORTING
    ));
    public static Supplier<KeyMapping> DROP_ALL_INVENTORY = registerKeyMapping(new KeyMapping(
            String.format("%s.key.dropAllInventory", MOD_ID),
            GLFW.GLFW_KEY_UNKNOWN,
            NEMOS_INVENTORY_SORTING
    ));
    public static Supplier<KeyMapping> TOGGLE_FILTER_PERSISTENCE = registerKeyMapping(new KeyMapping(
            String.format("%s.key.toggleFilterPersistence", MOD_ID),
            GLFW.GLFW_KEY_UNKNOWN,
            NEMOS_INVENTORY_SORTING
    ));
    public static Supplier<KeyMapping> QUICK_SEARCH = registerKeyMapping(new KeyMapping(
            String.format("%s.key.quickSearch", MOD_ID),
            GLFW.GLFW_KEY_F,
            NEMOS_INVENTORY_SORTING
    ));

    private static Supplier<KeyMapping> registerKeyMapping(KeyMapping keyMapping) {
        return NemosInventorySortingClientCommon.REGISTRY_HELPER.registerKeyMapping(keyMapping);
    }

    public static void init() {}
}
