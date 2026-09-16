package com.nemonotfound.nemos.inventory.sorting.config.model;

public class SettingsConfig {

    public static volatile SettingsConfig INSTANCE = new SettingsConfig();

    private boolean includeHotbarByDefault = false;
    private boolean enableDragQuickMove = true;
    private boolean enableSplitQuickMove = true;
    private boolean enableScrollTransfer = true;
    private boolean enableSlotLocking = true;

    private SettingsConfig() {
    }

    public boolean includeHotbarByDefault() {
        return includeHotbarByDefault;
    }

    public boolean shouldIncludeHotbar(boolean shiftDown) {
        return includeHotbarByDefault != shiftDown;
    }

    public boolean isDragQuickMoveEnabled() {
        return enableDragQuickMove;
    }

    public boolean isSplitQuickMoveEnabled() {
        return enableSplitQuickMove;
    }

    public boolean isScrollTransferEnabled() {
        return enableScrollTransfer;
    }

    public boolean isSlotLockingEnabled() {
        return enableSlotLocking;
    }
}
