package com.nemonotfound.nemos.inventory.sorting;

import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

import java.util.function.Supplier;

import static com.nemonotfound.nemos.inventory.sorting.Constants.MOD_ID;
import static com.nemonotfound.nemos.inventory.sorting.config.model.SettingsConfig.INSTANCE;

public class ModKeyMappings {

    private static final String category = String.format("%s.category.nemosInventorySorting", MOD_ID);

    public static Supplier<KeyMapping> HOVER_SORT = registerHoverKeyMapping(new KeyMapping(
            String.format("%s.key.hoverSort", MOD_ID),
            GLFW.GLFW_KEY_UNKNOWN,
            category
    ));
    public static Supplier<KeyMapping> HOVER_MOVE_SAME = registerHoverKeyMapping(new KeyMapping(
            String.format("%s.key.hoverMoveSame", MOD_ID),
            GLFW.GLFW_KEY_UNKNOWN,
            category
    ));
    public static Supplier<KeyMapping> HOVER_MOVE_ALL = registerHoverKeyMapping(new KeyMapping(
            String.format("%s.key.hoverMoveAll", MOD_ID),
            GLFW.GLFW_KEY_UNKNOWN,
            category
    ));
    public static Supplier<KeyMapping> HOVER_DROP_ALL = registerHoverKeyMapping(new KeyMapping(
            String.format("%s.key.hoverDropAll", MOD_ID),
            GLFW.GLFW_KEY_UNKNOWN,
            category
    ));
    public static Supplier<KeyMapping> SORT = registerContainerKeyMapping(new KeyMapping(
            String.format("%s.key.sort", MOD_ID),
            GLFW.GLFW_KEY_UNKNOWN,
            category
    ));
    public static Supplier<KeyMapping> SORT_INVENTORY = registerContainerKeyMapping(new KeyMapping(
            String.format("%s.key.sortInventory", MOD_ID),
            GLFW.GLFW_KEY_UNKNOWN,
            category
    ));
    public static Supplier<KeyMapping> MOVE_SAME = registerContainerKeyMapping(new KeyMapping(
            String.format("%s.key.moveSame", MOD_ID),
            GLFW.GLFW_KEY_UNKNOWN,
            category
    ));
    public static Supplier<KeyMapping> MOVE_SAME_INVENTORY = registerContainerKeyMapping(new KeyMapping(
            String.format("%s.key.moveSameInventory", MOD_ID),
            GLFW.GLFW_KEY_UNKNOWN,
            category
    ));
    public static Supplier<KeyMapping> MOVE_ALL = registerContainerKeyMapping(new KeyMapping(
            String.format("%s.key.moveAll", MOD_ID),
            GLFW.GLFW_KEY_UNKNOWN,
            category
    ));
    public static Supplier<KeyMapping> MOVE_ALL_INVENTORY = registerContainerKeyMapping(new KeyMapping(
            String.format("%s.key.moveAllInventory", MOD_ID),
            GLFW.GLFW_KEY_UNKNOWN,
            category
    ));
    public static Supplier<KeyMapping> DROP_ALL = registerContainerKeyMapping(new KeyMapping(
            String.format("%s.key.dropAll", MOD_ID),
            GLFW.GLFW_KEY_UNKNOWN,
            category
    ));
    public static Supplier<KeyMapping> DROP_ALL_INVENTORY = registerContainerKeyMapping(new KeyMapping(
            String.format("%s.key.dropAllInventory", MOD_ID),
            GLFW.GLFW_KEY_UNKNOWN,
            category
    ));
    public static Supplier<KeyMapping> TOGGLE_FILTER_PERSISTENCE = registerOptionalKeyMapping(new KeyMapping(
            String.format("%s.key.toggleFilterPersistence", MOD_ID),
            GLFW.GLFW_KEY_UNKNOWN,
            category
    ));
    public static Supplier<KeyMapping> QUICK_SEARCH = registerOptionalKeyMapping(new KeyMapping(
            String.format("%s.key.quickSearch", MOD_ID),
            GLFW.GLFW_KEY_F,
            category
    ));

    private static Supplier<KeyMapping> registerKeyMapping(KeyMapping keyMapping) {
        return NemosInventorySortingClientCommon.REGISTRY_HELPER.registerKeyMapping(keyMapping);
    }

    private static Supplier<KeyMapping> registerOptionalKeyMapping(KeyMapping keyMapping) {
        return INSTANCE.areKeyMappingsEnabled() ? registerKeyMapping(keyMapping) : () -> keyMapping;
    }

    private static Supplier<KeyMapping> registerHoverKeyMapping(KeyMapping keyMapping) {
        return INSTANCE.areHoverKeyMappingsEnabled() ? registerKeyMapping(keyMapping) : () -> keyMapping;
    }

    private static Supplier<KeyMapping> registerContainerKeyMapping(KeyMapping keyMapping) {
        return INSTANCE.areContainerKeyMappingsEnabled() ? registerKeyMapping(keyMapping) : () -> keyMapping;
    }

    public static void init() {}
}
