package com.nemonotfound.nemos.inventory.sorting.service;

import com.nemonotfound.nemos.inventory.sorting.models.LockedSlot;
import com.nemonotfound.nemos.inventory.sorting.models.config.LockedSlotsConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LockedSlotServiceTest {

    @Mock
    LockedSlotsConfig lockedSlotsConfig;
    @InjectMocks
    LockedSlotService lockedSlotService;

    @Test
    void getUnlockedSlots() {
        // given
        var lockedSlot = new LockedSlot(0);

        when(lockedSlotsConfig.getLockedSlots()).thenReturn(List.of(lockedSlot));

        // when
        List<Integer> actual = lockedSlotService.getUnlockedSlots(5, 10);

        // then
        assertThat(actual).hasSize(4);
        assertThat(actual).doesNotContain(5);
    }

    @ParameterizedTest
    @MethodSource
    @DisplayName("isLocked")
    void isLocked(int lockedIndex, int index, int startIndex, boolean expected) {
        // given
        var lockedSlot = new LockedSlot(lockedIndex);

        if (startIndex != 0) {
            when(lockedSlotsConfig.getLockedSlots()).thenReturn(List.of(lockedSlot));
        }

        // when
        var actual = lockedSlotService.isLocked(index, startIndex);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    private static Stream<Arguments> isLocked() {
        return Stream.of(
                Arguments.of(0, 0, 0, false),
                Arguments.of(1, 0, 0, false),
                Arguments.of(1, 1, 1, false),
                Arguments.of(0, 1, 1, true)
        );
    }
}