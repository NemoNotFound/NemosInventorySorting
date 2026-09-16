package com.nemonotfound.nemos.inventory.sorting.service;

import com.nemonotfound.nemos.inventory.sorting.config.model.LockedSlotsConfig;

import java.util.List;
import java.util.stream.IntStream;

public class LockedSlotService {

    public static final LockedSlotService INSTANCE = new LockedSlotService();

    public boolean isLocked(int index, int startIndex) {
        return startIndex != 0 && LockedSlotsConfig.INSTANCE.getLockedSlots().stream()
                .anyMatch(slot -> slot.index() + startIndex == index);
    }

    public List<Integer> getUnlockedSlots(int startIndex, int endIndex) {
        return IntStream.range(startIndex, endIndex)
                .filter(index -> !isLocked(index, startIndex))
                .boxed()
                .toList();
    }
}
