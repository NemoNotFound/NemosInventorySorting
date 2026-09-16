package com.nemonotfound.nemos.inventory.sorting.gui.components.buttons;

import com.nemonotfound.nemos.inventory.sorting.ModKeyMappings;
import net.minecraft.client.KeyMapping;
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

import static com.nemonotfound.nemos.inventory.sorting.Constants.MOD_ID;
import com.nemonotfound.nemos.inventory.sorting.service.LockedSlotService;

public class MoveSameButton extends AbstractSingleClickButton<MoveSameButton> {

    private final ResourceLocation buttonTexture = ResourceLocation.tryBuild(MOD_ID, "textures/gui/sprites/move_same_button.png");
    private final ResourceLocation buttonHoverTexture = ResourceLocation.tryBuild(MOD_ID, "textures/gui/sprites/move_same_button_highlighted.png");

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
    public void onClick(double mouseX, double mouseY) {
        interactWithAllItems(ClickType.QUICK_MOVE, 0);
    }

    @Override
    protected KeyMapping getKeyMapping() {
        return ModKeyMappings.MOVE_SAME.get();
    }

    @Override
    protected @NotNull List<Integer> getItemSlotsToInteractWith(AbstractContainerMenu menu) {
        var slots = menu.slots;
        var newEndIndex = calculateEndIndex();
        var itemsOutOfIndexRange = getItemsOutOfIndexRange(slots, startIndex, newEndIndex);

        return IntStream.range(startIndex, newEndIndex)
                .filter(index -> !LockedSlotService.INSTANCE.isLocked(index, startIndex))
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
