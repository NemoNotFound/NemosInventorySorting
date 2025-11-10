package com.nemonotfound.nemos.inventory.sorting.gui.components.buttons;

import com.nemonotfound.nemos.inventory.sorting.client.InventorySortingKeyMappings;
import com.nemonotfound.nemos.inventory.sorting.service.InventoryService;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import static com.nemonotfound.nemos.inventory.sorting.Constants.MOD_ID;

public class SortButton extends AbstractInventoryButton {

    private final ResourceLocation buttonTexture = ResourceLocation.fromNamespaceAndPath(MOD_ID, "sort_button");
    private final ResourceLocation buttonHoverTexture = ResourceLocation.fromNamespaceAndPath(MOD_ID, "sort_button_highlighted");

    private final InventoryService inventoryService;

    public SortButton(Builder<SortButton> builder) {
        super(builder);
        this.inventoryService = InventoryService.getInstance();
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
