package com.nemonotfound.nemos.inventory.sorting.service;

import com.nemonotfound.nemos.inventory.sorting.models.config.LockedSlotsConfig;

public class LockedSlotService {

    public static LockedSlotService INSTANCE = new LockedSlotService();

    private LockedSlotService() {}

    public boolean isLockedSLot(int index, int startIndex) {
        return LockedSlotsConfig.INSTANCE.getLockedSlots().stream()
                .anyMatch(slot -> slot.index() + startIndex == index);
    }
}
