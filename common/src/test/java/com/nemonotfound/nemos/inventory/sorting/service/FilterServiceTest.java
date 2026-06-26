package com.nemonotfound.nemos.inventory.sorting.service;

import com.nemonotfound.nemos.inventory.sorting.enums.FilterResult;
import net.minecraft.SharedConstants;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.Bootstrap;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.ItemContainerContents;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FilterServiceTest {

    static {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Mock
    TooltipService tooltipService;

    FilterService filterService;

    @BeforeEach
    void setUp() throws ReflectiveOperationException {
        // given
        Constructor<FilterService> constructor = FilterService.class.getDeclaredConstructor(TooltipService.class);
        constructor.setAccessible(true);

        // when
        filterService = constructor.newInstance(tooltipService);

        // then
        assertThat(filterService).isNotNull();
    }

    @Test
    @DisplayName("filterSlots: empty item is excluded")
    void filterSlots_emptyItem_excluded() {
        // given
        var itemStack = mock(ItemStack.class);
        var slot = createSlot(itemStack);

        when(itemStack.is(Items.AIR)).thenReturn(true);

        // when
        var actual = filterService.filterSlots(slots(slot), "oak");

        // then
        assertThat(actual.getOrDefault(FilterResult.EXCLUDED, List.of())).containsExactly(slot);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource
    @DisplayName("filterSlots")
    void filterSlots(String ignore, Component itemName, Component displayName, List<Component> tooltipComponents, String filter, FilterResult expected) {
        // given
        var itemStack = createItemStack(itemName, displayName, tooltipComponents);
        var slot = createSlot(itemStack);

        // when
        var actual = filterService.filterSlots(slots(slot), filter);

        // then
        assertThat(actual.getOrDefault(expected, List.of())).containsExactly(slot);
    }

    private static Stream<Arguments> filterSlots() {
        return Stream.of(
                Arguments.of("item name matches case insensitively", Component.literal("Oak Log"), Component.literal("Anything"), List.of(), "oak", FilterResult.INCLUDED),
                Arguments.of("display name matches case insensitively", Component.literal("Anything"), Component.literal("Custom Sword"), List.of(), "sword", FilterResult.INCLUDED),
                Arguments.of("enchantment tooltip matches", Component.literal("Anything"), Component.literal("Anything"), List.of(Component.translatable("enchantment.minecraft.sharpness")), "sharpness", FilterResult.INCLUDED),
                Arguments.of("jukebox song tooltip matches", Component.literal("Anything"), Component.literal("Anything"), List.of(Component.translatable("jukebox_song.minecraft.blocks")), "blocks", FilterResult.INCLUDED),
                Arguments.of("potion tooltip matches", Component.literal("Anything"), Component.literal("Anything"), List.of(Component.translatable("potion.withDuration", "Swiftness", "3:00")), "swiftness", FilterResult.INCLUDED),
                Arguments.of("nothing matches", Component.literal("Stone"), Component.literal("Stone"), List.of(), "oak", FilterResult.EXCLUDED)
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource
    @DisplayName("filterSlots tag")
    void filterSlots_tag(String ignore, String filter, boolean tagMatches, FilterResult expected, Identifier expectedTagId) {
        // given
        var itemStack = createNonAirItemStack();
        var slot = createSlot(itemStack);

        if (expectedTagId != null) {
            when(itemStack.is(anyItemTagKey())).thenReturn(tagMatches);
        }

        // when
        var actual = filterService.filterSlots(slots(slot), filter);

        // then
        assertThat(actual.getOrDefault(expected, List.of())).containsExactly(slot);
        assertTagChecked(itemStack, expectedTagId);
    }

    private static Stream<Arguments> filterSlots_tag() {
        return Stream.of(
                Arguments.of("matching tag includes item", "#minecraft:logs", true, FilterResult.INCLUDED, Identifier.parse("minecraft:logs")),
                Arguments.of("nonmatching tag excludes item", "#minecraft:logs", false, FilterResult.EXCLUDED, Identifier.parse("minecraft:logs")),
                Arguments.of("invalid tag excludes item", "#Bad Value!", false, FilterResult.EXCLUDED, null)
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource
    @DisplayName("filterSlots contents")
    void filterSlots_contents(String ignore, Supplier<ItemStack> itemStackSupplier, String filter, FilterResult expected) {
        // given
        var slot = createSlot(itemStackSupplier.get());

        // when
        var actual = filterService.filterSlots(slots(slot), filter);

        // then
        assertThat(actual.getOrDefault(expected, List.of())).containsExactly(slot);
    }

    private Stream<Arguments> filterSlots_contents() {
        return Stream.of(
                Arguments.of("bundle content match has included item", (Supplier<ItemStack>) () -> itemStackWithBundle(contentItemStack("Oak Log")), "oak", FilterResult.HAS_INCLUDED_ITEM),
                Arguments.of("bundle with null contents excludes item", (Supplier<ItemStack>) this::itemStackWithNullBundleContents, "oak", FilterResult.EXCLUDED),
                Arguments.of("container content match has included item", (Supplier<ItemStack>) () -> itemStackWithContainer(contentItemStack("Oak Log")), "oak", FilterResult.HAS_INCLUDED_ITEM),
                Arguments.of("container with null contents excludes item", (Supplier<ItemStack>) this::itemStackWithNullContainerContents, "oak", FilterResult.EXCLUDED),
                Arguments.of("nested bundle content match has included item", (Supplier<ItemStack>) () -> itemStackWithBundle(itemStackWithBundle(contentItemStack("Oak Log"))), "oak", FilterResult.HAS_INCLUDED_ITEM)
        );
    }

    @Test
    @DisplayName("filterSlots: multiple slots are grouped by result")
    void filterSlots_multipleSlots_groupedByResult() {
        // given
        var includedSlot = createSlot(createItemStack(Component.literal("Oak Log"), Component.literal("Oak Log"), List.of()));
        var excludedSlot = createSlot(createItemStack(Component.literal("Stone"), Component.literal("Stone"), List.of()));
        var hasIncludedItemSlot = createSlot(itemStackWithContainer(contentItemStack("Oak Sapling")));

        // when
        var actual = filterService.filterSlots(slots(includedSlot, excludedSlot, hasIncludedItemSlot), "oak");

        // then
        assertThat(actual.getOrDefault(FilterResult.INCLUDED, List.of())).containsExactly(includedSlot);
        assertThat(actual.getOrDefault(FilterResult.EXCLUDED, List.of())).containsExactly(excludedSlot);
        assertThat(actual.getOrDefault(FilterResult.HAS_INCLUDED_ITEM, List.of())).containsExactly(hasIncludedItemSlot);
    }

    private ItemStack createItemStack(Component itemName, Component displayName, List<Component> tooltipComponents) {
        var itemStack = createNonAirItemStack();

        lenient().when(itemStack.getItemName()).thenReturn(itemName);
        lenient().when(itemStack.getDisplayName()).thenReturn(displayName);
        lenient().when(tooltipService.retrieveTooltipLines(itemStack)).thenReturn(tooltipComponents);
        lenient().when(tooltipService.retrieveEnchantmentNames(tooltipComponents)).thenReturn(tooltipText(tooltipComponents, "enchantment"));
        lenient().when(tooltipService.retrieveJukeboxSongName(tooltipComponents)).thenReturn(tooltipText(tooltipComponents, "jukebox_song"));
        lenient().when(tooltipService.retrievePotionName(tooltipComponents)).thenReturn(tooltipText(tooltipComponents, "potion.withDuration", "potion.withAmplifier"));

        return itemStack;
    }

    private ItemStack createNonAirItemStack() {
        var itemStack = mock(ItemStack.class);

        lenient().when(itemStack.is(Items.AIR)).thenReturn(false);
        lenient().when(itemStack.has(DataComponents.BUNDLE_CONTENTS)).thenReturn(false);
        lenient().when(itemStack.has(DataComponents.CONTAINER)).thenReturn(false);

        return itemStack;
    }

    private String tooltipText(List<Component> tooltipComponents, String... keys) {
        return tooltipComponents.stream()
                .filter(component -> {
                    var componentString = component.toString();

                    return Stream.of(keys).anyMatch(componentString::contains);
                })
                .map(Component::getString)
                .findFirst()
                .orElse("");
    }

    private ItemStack contentItemStack(String itemName) {
        return createItemStack(Component.literal(itemName), Component.literal(itemName), List.of());
    }

    private ItemStack itemStackWithBundle(ItemStack... contents) {
        var itemStack = createItemStack(Component.literal("Bundle"), Component.literal("Bundle"), List.of());
        var bundleContents = mock(BundleContents.class);

        when(itemStack.has(DataComponents.BUNDLE_CONTENTS)).thenReturn(true);
        when(itemStack.get(DataComponents.BUNDLE_CONTENTS)).thenReturn(bundleContents);
        when(bundleContents.itemCopyStream()).thenReturn(Stream.of(contents));

        return itemStack;
    }

    private ItemStack itemStackWithNullBundleContents() {
        var itemStack = createItemStack(Component.literal("Bundle"), Component.literal("Bundle"), List.of());

        when(itemStack.has(DataComponents.BUNDLE_CONTENTS)).thenReturn(true);
        when(itemStack.get(DataComponents.BUNDLE_CONTENTS)).thenReturn(null);

        return itemStack;
    }

    private ItemStack itemStackWithContainer(ItemStack... contents) {
        var itemStack = createItemStack(Component.literal("Container"), Component.literal("Container"), List.of());
        var containerContents = mock(ItemContainerContents.class);

        when(itemStack.has(DataComponents.CONTAINER)).thenReturn(true);
        when(itemStack.get(DataComponents.CONTAINER)).thenReturn(containerContents);
        when(containerContents.nonEmptyItemCopyStream()).thenReturn(Stream.of(contents));

        return itemStack;
    }

    private ItemStack itemStackWithNullContainerContents() {
        var itemStack = createItemStack(Component.literal("Container"), Component.literal("Container"), List.of());

        when(itemStack.has(DataComponents.CONTAINER)).thenReturn(true);
        when(itemStack.get(DataComponents.CONTAINER)).thenReturn(null);

        return itemStack;
    }

    private Slot createSlot(ItemStack itemStack) {
        var slot = mock(Slot.class);

        when(slot.getItem()).thenReturn(itemStack);

        return slot;
    }

    private NonNullList<Slot> slots(Slot... slots) {
        var result = NonNullList.<Slot>create();

        result.addAll(List.of(slots));

        return result;
    }

    private void assertTagChecked(ItemStack itemStack, Identifier expectedTagId) {
        if (expectedTagId == null) {
            verify(itemStack, never()).is(anyItemTagKey());
            return;
        }

        ArgumentCaptor<TagKey<Item>> tagKeyCaptor = ArgumentCaptor.captor();

        verify(itemStack).is(tagKeyCaptor.capture());

        assertThat(tagKeyCaptor.getValue().location()).isEqualTo(expectedTagId);
    }

    @SuppressWarnings("unchecked")
    private TagKey<Item> anyItemTagKey() {
        return any(TagKey.class);
    }
}
