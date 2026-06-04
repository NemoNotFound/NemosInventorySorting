package com.nemonotfound.nemos.inventory.sorting.models.config;

import com.nemonotfound.nemos.inventory.sorting.models.LockedSlot;

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

    public boolean remove(LockedSlot lockedSlot) {
        return lockedSlots.remove(lockedSlot);
    }

    public boolean add(LockedSlot lockedSlot) {
        return lockedSlots.add(lockedSlot);
    }
}
