package com.nemonotfound.nemos.inventory.sorting.service;

import com.nemonotfound.nemos.inventory.sorting.models.SlotRange;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HoveredSlotRangeServiceTest {

    private final HoveredSlotRangeService service = HoveredSlotRangeService.getInstance();

    @Test
    void resolvesStorageWhenContainerSlotIsHovered() {
        assertThat(service.getSlotRange(63, 8, false, true, false))
                .contains(new SlotRange(0, 27));
    }

    @Test
    void resolvesPlayerInventoryWhenPlayerSlotIsHovered() {
        assertThat(service.getSlotRange(63, 30, false, true, false))
                .contains(new SlotRange(27, 54));
    }

    @Test
    void includesHotbarWhenRequested() {
        assertThat(service.getSlotRange(63, 30, false, true, true))
                .contains(new SlotRange(27, 63));
    }

    @Test
    void rejectsCraftingAndResultSlots() {
        assertThat(service.getSlotRange(46, 0, false, false, false)).isEmpty();
        assertThat(service.getSlotRange(46, 5, false, false, false)).isEmpty();
    }

    @Test
    void resolvesSurvivalInventoryWithoutArmorOrCraftingSlots() {
        assertThat(service.getSlotRange(46, 10, true, false, false))
                .contains(new SlotRange(9, 36));
        assertThat(service.getSlotRange(46, 0, true, false, false)).isEmpty();
    }
}
