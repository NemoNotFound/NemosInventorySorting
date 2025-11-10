package com.nemonotfound.nemos.inventory.sorting.config;

import com.nemonotfound.nemos.inventory.sorting.config.model.ComponentConfig;
import com.nemonotfound.nemos.inventory.sorting.config.model.FilterConfig;

import java.util.ArrayList;
import java.util.List;

import static com.nemonotfound.nemos.inventory.sorting.config.DefaultConfigValues.*;

public class DefaultConfigs {

    public static final List<ComponentConfig> DEFAULT_COMPONENT_CONFIGS = new ArrayList<>();
    public static final FilterConfig DEFAULT_FILTER_CONFIG = new FilterConfig();
    public static final List<ComponentConfig> DEFAULT_IRON_CHEST_COMPONENT_CONFIGS = new ArrayList<>();

    private DefaultConfigs() {}

    public static void setupDefaultConfigs() {
        setupDefaultComponentConfigs();
        setupDefaultIronChestComponentConfigs();
    }

    private static void setupDefaultComponentConfigs() {
        createAndAddComponentConfig(
                SORT_STORAGE_CONTAINER,
                null,
                RIGHT_X_OFFSET_SORT_STORAGE_CONTAINER,
                Y_OFFSET_CONTAINER,
                BUTTON_SIZE,
                BUTTON_SIZE
        );
        createAndAddComponentConfig(
                MOVE_SAME_STORAGE_CONTAINER,
                null,
                RIGHT_X_OFFSET_MOVE_SAME_STORAGE_CONTAINER,
                Y_OFFSET_CONTAINER,
                BUTTON_SIZE,
                BUTTON_SIZE
        );
        createAndAddComponentConfig(
                MOVE_ALL_STORAGE_CONTAINER,
                null,
                RIGHT_X_OFFSET_MOVE_ALL_STORAGE_CONTAINER,
                Y_OFFSET_CONTAINER,
                BUTTON_SIZE,
                BUTTON_SIZE
        );
        createAndAddComponentConfig(
                DROP_ALL_STORAGE_CONTAINER,
                null,
                RIGHT_X_OFFSET_DROP_ALL_STORAGE_CONTAINER,
                Y_OFFSET_CONTAINER,
                BUTTON_SIZE,
                BUTTON_SIZE
        );

        createAndAddComponentConfig(
                SORT_STORAGE_CONTAINER_INVENTORY,
                null,
                RIGHT_X_OFFSET_SORT_STORAGE_CONTAINER,
                null,
                BUTTON_SIZE,
                BUTTON_SIZE
        );
        createAndAddComponentConfig(
                MOVE_SAME_STORAGE_CONTAINER_INVENTORY,
                null,
                RIGHT_X_OFFSET_MOVE_SAME_STORAGE_CONTAINER,
                null,
                BUTTON_SIZE,
                BUTTON_SIZE
        );
        createAndAddComponentConfig(
                MOVE_ALL_STORAGE_CONTAINER_INVENTORY,
                null,
                RIGHT_X_OFFSET_MOVE_ALL_STORAGE_CONTAINER,
                null,
                BUTTON_SIZE,
                BUTTON_SIZE
        );
        createAndAddComponentConfig(
                DROP_ALL_STORAGE_CONTAINER_INVENTORY,
                null,
                RIGHT_X_OFFSET_DROP_ALL_STORAGE_CONTAINER,
                null,
                BUTTON_SIZE,
                BUTTON_SIZE
        );

        createAndAddComponentConfig(
                SORT_INVENTORY,
                X_OFFSET_SORT_INVENTORY,
                null,
                Y_OFFSET_INVENTORY,
                BUTTON_SIZE,
                BUTTON_SIZE
        );
        createAndAddComponentConfig(
                DROP_ALL_INVENTORY,
                X_OFFSET_DROP_ALL_INVENTORY,
                null,
                Y_OFFSET_INVENTORY,
                BUTTON_SIZE,
                BUTTON_SIZE
        );

        createAndAddComponentConfig(
                SORT_CONTAINER_INVENTORY,
                X_OFFSET_SORT_CONTAINER_INVENTORY,
                null,
                null,
                BUTTON_SIZE,
                BUTTON_SIZE
        );
        createAndAddComponentConfig(
                DROP_ALL_CONTAINER_INVENTORY,
                X_OFFSET_DROP_ALL_CONTAINER_INVENTORY,
                null,
                null,
                BUTTON_SIZE,
                BUTTON_SIZE
        );

        createAndAddComponentConfig(
                ITEM_FILTER,
                null,
                null,
                Y_OFFSET_ITEM_FILTER,
                ITEM_FILTER_WIDTH,
                ITEM_FILTER_HEIGHT
        );
        createAndAddComponentConfig(
                FILTER_PERSISTENCE_TOGGLE,
                null,
                null,
                Y_OFFSET_FILTER_PERSISTENCE_TOGGLE,
                FILTER_BUTTON_SIZE,
                FILTER_BUTTON_SIZE
        );
    }

    private static void setupDefaultIronChestComponentConfigs() {
        createAndAddIronChestComponentConfig(
                SORT_STORAGE_CONTAINER,
                Y_OFFSET_CONTAINER,
                BUTTON_SIZE,
                BUTTON_SIZE
        );
        createAndAddIronChestComponentConfig(
                MOVE_SAME_STORAGE_CONTAINER,
                Y_OFFSET_CONTAINER,
                BUTTON_SIZE,
                BUTTON_SIZE
        );
        createAndAddIronChestComponentConfig(
                MOVE_ALL_STORAGE_CONTAINER,
                Y_OFFSET_CONTAINER,
                BUTTON_SIZE,
                BUTTON_SIZE
        );
        createAndAddIronChestComponentConfig(
                DROP_ALL_STORAGE_CONTAINER,
                Y_OFFSET_CONTAINER,
                BUTTON_SIZE,
                BUTTON_SIZE
        );

        createAndAddIronChestComponentConfig(
                SORT_STORAGE_CONTAINER_INVENTORY,
                null,
                BUTTON_SIZE,
                BUTTON_SIZE
        );
        createAndAddIronChestComponentConfig(
                MOVE_SAME_STORAGE_CONTAINER_INVENTORY,
                null,
                BUTTON_SIZE,
                BUTTON_SIZE
        );
        createAndAddIronChestComponentConfig(
                MOVE_ALL_STORAGE_CONTAINER_INVENTORY,
                null,
                BUTTON_SIZE,
                BUTTON_SIZE
        );
        createAndAddIronChestComponentConfig(
                DROP_ALL_STORAGE_CONTAINER_INVENTORY,
                null,
                BUTTON_SIZE,
                BUTTON_SIZE
        );
    }

    private static void createAndAddComponentConfig(String name, Integer xOffset, Integer rightXOffset, Integer yOffset, int width, int height) {
        var config = new ComponentConfig(name, true, xOffset, rightXOffset, yOffset, width, height);

        DEFAULT_COMPONENT_CONFIGS.add(config);
    }

    private static void createAndAddIronChestComponentConfig(String name, Integer yOffset, int width, int height) {
        var config = new ComponentConfig(name, true, null, null, yOffset, width, height);

        DEFAULT_IRON_CHEST_COMPONENT_CONFIGS.add(config);
    }
}
