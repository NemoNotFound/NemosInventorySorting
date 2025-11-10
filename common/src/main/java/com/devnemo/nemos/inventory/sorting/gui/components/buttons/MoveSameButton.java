package com.devnemo.nemos.inventory.sorting.gui.components.buttons;

import com.devnemo.nemos.inventory.sorting.client.InventorySortingKeyMappings;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static com.devnemo.nemos.inventory.sorting.Constants.MOD_ID;

public class MoveSameButton extends AbstractSingleClickButton<MoveSameButton> {

    private final ResourceLocation buttonTexture = ResourceLocation.fromNamespaceAndPath(MOD_ID, "move_same_button");
    private final ResourceLocation buttonHoverTexture = ResourceLocation.fromNamespaceAndPath(MOD_ID, "move_same_button_highlighted");

    public MoveSameButton(Builder<MoveSameButton> builder) {
        super(builder);
    }

    @Override
    protected ResourceLocation getButtonHoverTexture() {
        return buttonHoverTexture;
    }

    @Override
    protected ResourceLocation getButtonTexture() {
        return buttonTexture;
    }

    @Override
    public void onClick(@NotNull MouseButtonEvent mouseButtonEvent, boolean isDoubleClick) {
        interactWithAllItems(ClickType.QUICK_MOVE, 0);
    }

    @Override
    protected KeyMapping getKeyMapping() {
        if (isInventoryButton) {
            return InventorySortingKeyMappings.MOVE_SAME_INVENTORY.get();
        }

        return InventorySortingKeyMappings.MOVE_SAME.get();
    }

    @Override
    protected @NotNull List<Integer> getItemSlotsToInteractWith(AbstractContainerMenu menu) {
        var slots = menu.slots;
        var itemsOutOfIndexRange = getItemsOutOfIndexRange(slots, startIndex, currentEndIndex);

        return IntStream.range(startIndex, currentEndIndex)
                .mapToObj(slotIndex -> Map.entry(slotIndex, slots.get(slotIndex).getItem()))
                .filter(itemStackEntry -> isItemInOtherContainer(itemStackEntry.getValue(), itemsOutOfIndexRange))
                .map(Map.Entry::getKey)
                .toList();
    }

    private List<Item> getItemsOutOfIndexRange(NonNullList<Slot> slots, int startIndex, int endIndex) {
        return slots.stream()
                .filter(slot -> (startIndex != 0 && slot.index < startIndex) || (startIndex == 0 && slot.index >= endIndex))
                .filter(slot -> !slot.getItem().is(Items.AIR))
                .map(slot -> slot.getItem().getItem())
                .distinct()
                .toList();
    }

    private boolean isItemInOtherContainer(ItemStack itemStack, List<Item> itemsOutOfIndexRange) {
        return !itemStack.is(Items.AIR) && itemsOutOfIndexRange.contains(itemStack.getItem());
    }
}
