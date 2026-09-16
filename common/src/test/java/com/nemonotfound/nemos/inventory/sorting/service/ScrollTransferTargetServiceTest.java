package com.nemonotfound.nemos.inventory.sorting.service;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ScrollTransferTargetServiceTest {

    static {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void preservesLastOrdinaryItemWithoutShift() {
        assertThat(ScrollTransferTargetService.hasTransferableCount(new ItemStack(Items.STONE, 1), false)).isFalse();
    }

    @Test
    void allowsLastOrdinaryItemWithShift() {
        assertThat(ScrollTransferTargetService.hasTransferableCount(new ItemStack(Items.STONE, 1), true)).isTrue();
    }
}
