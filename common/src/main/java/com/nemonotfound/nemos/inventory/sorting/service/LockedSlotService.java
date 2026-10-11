package com.nemonotfound.nemos.inventory.sorting.service;

import com.nemonotfound.nemos.inventory.sorting.models.config.ContainerLockedSlotsConfig;
import com.nemonotfound.nemos.inventory.sorting.models.config.LockedSlotsConfig;

import java.util.List;
import java.util.stream.IntStream;

public class LockedSlotService {

    public static LockedSlotService INSTANCE = new LockedSlotService(LockedSlotsConfig.INSTANCE);

    private final LockedSlotsConfig lockedSlotsConfig;

    private LockedSlotService(LockedSlotsConfig lockedSlotsConfig) {
        this.lockedSlotsConfig = lockedSlotsConfig;
    }

    public List<Integer> getUnlockedSlots(int startIndex, int endIndex) {
        return IntStream.range(startIndex, endIndex)
                .filter(index -> !isLocked(index, startIndex))
                .boxed()
                .toList();
    }

    public boolean isLocked(int index, int startIndex) {
        if (startIndex == 0) {
            // Container range: slot indexes are container-relative and stored per opened container
            return ContainerLockedSlotsConfig.INSTANCE.isLocked(index);
        }

        return lockedSlotsConfig.getLockedSlots().stream()
                .anyMatch(slot -> slot.index() + startIndex == index);
    }
}
