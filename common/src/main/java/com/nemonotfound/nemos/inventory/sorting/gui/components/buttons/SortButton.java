package com.nemonotfound.nemos.inventory.sorting.gui.components.buttons;

import com.nemonotfound.nemos.inventory.sorting.client.InventorySortingKeyMappings;
import com.nemonotfound.nemos.inventory.sorting.model.Position;
import com.nemonotfound.nemos.inventory.sorting.model.Size;
import com.nemonotfound.nemos.inventory.sorting.model.SlotRange;
import com.nemonotfound.nemos.inventory.sorting.service.InventoryService;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.NotNull;

import static com.nemonotfound.nemos.inventory.sorting.Constants.MOD_ID;

public class SortButton extends AbstractContainerButton {

    private final Identifier buttonTexture = Identifier.fromNamespaceAndPath(MOD_ID, "sort_button");
    private final Identifier buttonHoverTexture = Identifier.fromNamespaceAndPath(MOD_ID, "sort_button_highlighted");

    private final InventoryService inventoryService;

    public SortButton(Position position, int xOffset, Size size, SlotRange slotRange, Component buttonName, AbstractContainerMenu menu) {
        super(position, xOffset, size, slotRange, buttonName, menu);
        this.inventoryService = InventoryService.getInstance();
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
        inventoryService.handleSorting(menu, startIndex, currentEndIndex);
    }

    @Override
    protected KeyMapping getKeyMapping() {
        if (isInventoryButton) {
            return InventorySortingKeyMappings.SORT_INVENTORY.get();
        }

        return InventorySortingKeyMappings.SORT.get();
    }
}
