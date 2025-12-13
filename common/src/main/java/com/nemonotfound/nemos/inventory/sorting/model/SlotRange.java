package com.nemonotfound.nemos.inventory.sorting.model;

public record SlotRange(int startIndex, int endIndex) {

    public SlotRange {
        if (startIndex < 0 || endIndex < startIndex) {
            throw new IllegalArgumentException(String.format("Invalid slot range: startIndex=%s, endIndex=%s.", startIndex, endIndex));
        }
    }
}
