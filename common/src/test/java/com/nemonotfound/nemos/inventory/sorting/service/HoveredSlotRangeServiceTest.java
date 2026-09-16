package com.nemonotfound.nemos.inventory.sorting.service;

import com.nemonotfound.nemos.inventory.sorting.model.SlotRange;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HoveredSlotRangeServiceTest {

    private final HoveredSlotRangeService service = HoveredSlotRangeService.INSTANCE;

    @Test
    void resolvesStorageRange() {
        assertThat(service.getSlotRange(63, 8, false, true, false)).contains(new SlotRange(0, 27));
    }

    @Test
    void resolvesPlayerInventoryWithHotbar() {
        assertThat(service.getSlotRange(63, 30, false, true, true)).contains(new SlotRange(27, 63));
    }

    @Test
    void rejectsCraftingSlots() {
        assertThat(service.getSlotRange(46, 5, true, false, false)).isEmpty();
    }

    @Test
    void resolvesSurvivalInventoryWithoutHotbar() {
        assertThat(service.getSlotRange(46, 10, true, false, false)).contains(new SlotRange(9, 36));
    }
}
