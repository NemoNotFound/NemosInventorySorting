package com.nemonotfound.nemos.inventory.sorting.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ItemSortingTest {

    @Test
    void sortsLargerStacksFirst() {
        var smallerStack = sorting(8, "");
        var largerStack = sorting(64, "");

        assertThat(ItemSorting.COMPARATOR.compare(largerStack, smallerStack)).isNegative();
    }

    @Test
    void usesNbtDataAsFinalTieBreaker() {
        var flopper = sorting(1, "BucketVariant:flopper");
        var sunstreak = sorting(1, "BucketVariant:sunstreak");

        assertThat(ItemSorting.COMPARATOR.compare(flopper, sunstreak)).isNegative();
    }

    private ItemSorting sorting(int count, String nbtData) {
        return new ItemSorting(1, count, "Item", "", "", "", nbtData);
    }
}
