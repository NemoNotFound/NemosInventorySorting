package com.devnemo.nemos.inventory.sorting.config;

public class DefaultConfigValues {

    private DefaultConfigValues() {}

    public static final String CONFIG_DIRECTORY_PATH = "config/nemos-inventory-sorting/";
    public static final String COMPONENT_CONFIG_PATH = CONFIG_DIRECTORY_PATH + "config.json";
    public static final String FILTER_CONFIG_PATH = CONFIG_DIRECTORY_PATH + "filter-config.json";
    public static final String IRON_CHEST_COMPONENT_CONFIG_PATH = CONFIG_DIRECTORY_PATH + "iron-chest-config.json";

    public static String SORT_STORAGE_CONTAINER = "sort_storage_container";
    public static String MOVE_SAME_STORAGE_CONTAINER = "move_same_storage_container";
    public static String MOVE_ALL_STORAGE_CONTAINER = "move_all_storage_container";
    public static String DROP_ALL_STORAGE_CONTAINER = "drop_all_storage_container";

    public static String SORT_STORAGE_CONTAINER_INVENTORY = "sort_storage_container_inventory";
    public static String MOVE_SAME_STORAGE_CONTAINER_INVENTORY = "move_same_storage_container_inventory";
    public static String MOVE_ALL_STORAGE_CONTAINER_INVENTORY = "move_all_storage_container_inventory";
    public static String DROP_ALL_STORAGE_CONTAINER_INVENTORY = "drop_all_storage_container_inventory";

    public static String SORT_INVENTORY = "sort_inventory";
    public static String DROP_ALL_INVENTORY = "drop_all_inventory";

    public static String SORT_CONTAINER_INVENTORY = "sort_container_inventory";
    public static String DROP_ALL_CONTAINER_INVENTORY = "drop_all_container_inventory";

    public static String ITEM_FILTER = "item_filter";
    public static String FILTER_PERSISTENCE_TOGGLE = "filter_persistence_toggle";

    public static int RIGHT_X_OFFSET_SORT_STORAGE_CONTAINER = -61;
    public static int RIGHT_X_OFFSET_MOVE_SAME_STORAGE_CONTAINER = -47;
    public static int RIGHT_X_OFFSET_MOVE_ALL_STORAGE_CONTAINER = -33;
    public static int RIGHT_X_OFFSET_DROP_ALL_STORAGE_CONTAINER = -19;
    public static int Y_OFFSET_CONTAINER = 5;

    public static int X_OFFSET_SORT_INVENTORY = 136;
    public static int X_OFFSET_DROP_ALL_INVENTORY = 154;
    public static int Y_OFFSET_INVENTORY = 71;

    public static int X_OFFSET_SORT_CONTAINER_INVENTORY = 136;
    public static int X_OFFSET_DROP_ALL_CONTAINER_INVENTORY = 154;

    public static int Y_OFFSET_ITEM_FILTER = -16;
    public static int Y_OFFSET_FILTER_PERSISTENCE_TOGGLE = -15;
    public static int ITEM_FILTER_WIDTH = 77;
    public static int ITEM_FILTER_HEIGHT = 15;

    public static int BUTTON_SIZE = 11;
    public static int FILTER_BUTTON_SIZE = 13;
}
