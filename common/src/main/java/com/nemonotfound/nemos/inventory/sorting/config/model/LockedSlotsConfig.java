package com.nemonotfound.nemos.inventory.sorting.config.model;

import com.nemonotfound.nemos.inventory.sorting.model.LockedSlot;

import java.util.ArrayList;
import java.util.List;

public class LockedSlotsConfig {

    public static LockedSlotsConfig INSTANCE = new LockedSlotsConfig();

    private final List<LockedSlot> lockedSlots = new ArrayList<>();

    private LockedSlotsConfig() {
    }

    public List<LockedSlot> getLockedSlots() {
        return lockedSlots;
    }

    public boolean remove(LockedSlot slot) {
        return lockedSlots.remove(slot);
    }

    public boolean add(LockedSlot slot) {
        return lockedSlots.add(slot);
    }
}
