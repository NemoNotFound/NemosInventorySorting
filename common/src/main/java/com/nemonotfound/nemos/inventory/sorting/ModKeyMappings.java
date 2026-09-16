package com.nemonotfound.nemos.inventory.sorting;

import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

import java.util.function.Supplier;

import static com.nemonotfound.nemos.inventory.sorting.Constants.MOD_ID;

public class ModKeyMappings {

    private static final String category = String.format("%s.category.nemosInventorySorting", MOD_ID);

    public static Supplier<KeyMapping> SORT = registerKeyMapping(new KeyMapping(
            String.format("%s.key.sort", MOD_ID),
            GLFW.GLFW_KEY_UNKNOWN,
            category
    ));
    public static Supplier<KeyMapping> MOVE_SAME = registerKeyMapping(new KeyMapping(
            String.format("%s.key.moveSame", MOD_ID),
            GLFW.GLFW_KEY_UNKNOWN,
            category
    ));
    public static Supplier<KeyMapping> MOVE_ALL = registerKeyMapping(new KeyMapping(
            String.format("%s.key.moveAll", MOD_ID),
            GLFW.GLFW_KEY_UNKNOWN,
            category
    ));
    public static Supplier<KeyMapping> DROP_ALL = registerKeyMapping(new KeyMapping(
            String.format("%s.key.dropAll", MOD_ID),
            GLFW.GLFW_KEY_UNKNOWN,
            category
    ));
    public static Supplier<KeyMapping> TOGGLE_FILTER_PERSISTENCE = registerKeyMapping(new KeyMapping(
            String.format("%s.key.toggleFilterPersistence", MOD_ID),
            GLFW.GLFW_KEY_UNKNOWN,
            category
    ));
    public static Supplier<KeyMapping> QUICK_SEARCH = registerKeyMapping(new KeyMapping(
            String.format("%s.key.quickSearch", MOD_ID),
            GLFW.GLFW_KEY_F,
            category
    ));

    private static Supplier<KeyMapping> registerKeyMapping(KeyMapping keyMapping) {
        return NemosInventorySortingClientCommon.REGISTRY_HELPER.registerKeyMapping(keyMapping);
    }

    public static void init() {}
}
