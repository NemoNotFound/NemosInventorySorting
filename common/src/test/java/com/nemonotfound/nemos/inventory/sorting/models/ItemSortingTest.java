package com.nemonotfound.nemos.inventory.sorting.models;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ItemSortingTest {

    @Test
    void sortsStackCountDescendingWithinSameItemOrder() {
        var smallerStack = key(8, "");
        var largerStack = key(64, "");

        assertThat(ItemSorting.COMPARATOR.compare(largerStack, smallerStack)).isNegative();
    }

    @Test
    void usesComponentDataAsFinalTieBreaker() {
        var flopper = key(1, "tropical_fish/pattern=>flopper");
        var sunstreak = key(1, "tropical_fish/pattern=>sunstreak");

        assertThat(ItemSorting.COMPARATOR.compare(flopper, sunstreak)).isNegative();
    }

    private ItemSorting key(int count, String componentData) {
        return new ItemSorting(1, count, "Item", "", "", "", componentData);
    }
}
