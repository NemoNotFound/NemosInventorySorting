package com.nemonotfound.nemos.inventory.sorting.gui.components.buttons;

import com.nemonotfound.nemos.inventory.sorting.client.InventorySortingKeyMappings;
import com.nemonotfound.nemos.inventory.sorting.model.Position;
import com.nemonotfound.nemos.inventory.sorting.model.Size;
import com.nemonotfound.nemos.inventory.sorting.model.SlotRange;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

import static com.nemonotfound.nemos.inventory.sorting.Constants.MOD_ID;

public class MoveAllButton extends AbstractSingleClickButton {

    private final Identifier buttonTexture = Identifier.fromNamespaceAndPath(MOD_ID, "move_all_button");
    private final Identifier buttonHoverTexture = Identifier.fromNamespaceAndPath(MOD_ID, "move_all_button_highlighted");

    public MoveAllButton(Position position, int xOffset, Size size, SlotRange slotRange, Component buttonName, AbstractContainerMenu menu) {
        super(position, xOffset, size, slotRange, buttonName, menu);
    }

    @Override
    protected Identifier getButtonHoverTexture() {
        return buttonHoverTexture;
    }

    @Override
    protected Identifier getButtonTexture() {
        return buttonTexture;
    }

    @Override
    public void onClick(@NotNull MouseButtonEvent mouseButtonEvent, boolean isDoubleClick) {
        interactWithAllItems(ClickType.QUICK_MOVE, 0);
    }

    @Override
    protected KeyMapping getKeyMapping() {
        if (isInventoryButton) {
            return InventorySortingKeyMappings.MOVE_ALL_INVENTORY.get();
        }

        return InventorySortingKeyMappings.MOVE_ALL.get();
    }
}
