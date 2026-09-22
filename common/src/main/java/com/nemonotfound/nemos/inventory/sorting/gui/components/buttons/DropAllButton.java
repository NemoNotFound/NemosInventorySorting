package com.nemonotfound.nemos.inventory.sorting.gui.components.buttons;

import com.nemonotfound.nemos.inventory.sorting.ModKeyMappings;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.ClickType;

import static com.nemonotfound.nemos.inventory.sorting.Constants.MOD_ID;

public class DropAllButton extends AbstractSingleClickButton<DropAllButton> {

    private final ResourceLocation buttonTexture = ResourceLocation.tryBuild(MOD_ID, "textures/gui/sprites/drop_all_button.png");
    private final ResourceLocation buttonHoverTexture = ResourceLocation.tryBuild(MOD_ID, "textures/gui/sprites/drop_all_button_highlighted.png");

    public DropAllButton(Builder<DropAllButton> builder) {
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
        interactWithAllItems(ClickType.THROW, 1);
    }

    @Override
    protected KeyMapping getKeyMapping() {
        return ModKeyMappings.DROP_ALL.get();
    }

    @Override
    protected KeyMapping getInventoryKeyMapping() {
        return ModKeyMappings.DROP_ALL_INVENTORY.get();
    }

    @Override
    protected KeyMapping getHoverKeyMapping() {
        return ModKeyMappings.HOVER_DROP_ALL.get();
    }
}
