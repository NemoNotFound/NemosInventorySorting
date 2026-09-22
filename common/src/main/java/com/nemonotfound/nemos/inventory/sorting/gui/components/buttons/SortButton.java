package com.nemonotfound.nemos.inventory.sorting.gui.components.buttons;

import com.nemonotfound.nemos.inventory.sorting.ModKeyMappings;
import com.nemonotfound.nemos.inventory.sorting.service.InventoryService;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;

import static com.nemonotfound.nemos.inventory.sorting.Constants.MOD_ID;

public class SortButton extends AbstractInventoryButton {

    private final ResourceLocation buttonTexture = ResourceLocation.tryBuild(MOD_ID, "textures/gui/sprites/sort_button.png");
    private final ResourceLocation buttonHoverTexture = ResourceLocation.tryBuild(MOD_ID, "textures/gui/sprites/sort_button_highlighted.png");

    public SortButton(Builder<SortButton> builder) {
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
        var inventoryService = InventoryService.getInstance();
        var endIndex = inventoryService.calculateEndIndex(isButtonShiftable(), this.endIndex);

        inventoryService.handleSorting(menu, startIndex, endIndex);
    }

    @Override
    protected KeyMapping getKeyMapping() {
        return ModKeyMappings.SORT.get();
    }

    @Override
    protected KeyMapping getInventoryKeyMapping() {
        return ModKeyMappings.SORT_INVENTORY.get();
    }

    @Override
    protected KeyMapping getHoverKeyMapping() {
        return ModKeyMappings.HOVER_SORT.get();
    }
}
