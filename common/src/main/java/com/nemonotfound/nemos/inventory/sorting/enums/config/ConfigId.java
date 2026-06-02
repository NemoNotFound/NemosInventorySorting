package com.nemonotfound.nemos.inventory.sorting.enums.config;

public enum ConfigId {

    SORT_STORAGE_CONTAINER("sort_storage_container"),

    MOVE_SAME_STORAGE_CONTAINER("move_same_storage_container"),
    MOVE_ALL_STORAGE_CONTAINER("move_all_storage_container"),
    DROP_ALL_STORAGE_CONTAINER("drop_all_storage_container"),

    SORT_STORAGE_CONTAINER_INVENTORY("sort_storage_container_inventory"),
    MOVE_SAME_STORAGE_CONTAINER_INVENTORY("move_same_storage_container_inventory"),
    MOVE_ALL_STORAGE_CONTAINER_INVENTORY("move_all_storage_container_inventory"),
    DROP_ALL_STORAGE_CONTAINER_INVENTORY("drop_all_storage_container_inventory"),

    SORT_INVENTORY("sort_inventory"),
    DROP_ALL_INVENTORY("drop_all_inventory"),

    SORT_CONTAINER_INVENTORY("sort_container_inventory"),
    DROP_ALL_CONTAINER_INVENTORY("drop_all_container_inventory"),

    ITEM_FILTER("item_filter"),
    FILTER_PERSISTENCE_TOGGLE("filter_persistence_toggle");

    private final String id;

    ConfigId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
