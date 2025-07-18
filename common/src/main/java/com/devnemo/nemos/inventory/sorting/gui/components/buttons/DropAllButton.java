package com.devnemo.nemos.inventory.sorting.gui.components.buttons;

import com.devnemo.nemos.inventory.sorting.ModKeyMappings;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.ClickType;

import static com.devnemo.nemos.inventory.sorting.Constants.MOD_ID;

public class DropAllButton extends AbstractSingleClickButton<DropAllButton> {

    private final ResourceLocation buttonTexture = ResourceLocation.fromNamespaceAndPath(MOD_ID, "drop_all_button");
    private final ResourceLocation buttonHoverTexture = ResourceLocation.fromNamespaceAndPath(MOD_ID, "drop_all_button_highlighted");

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
        if (isInventoryButton) {
            return ModKeyMappings.DROP_ALL_INVENTORY.get();
        }

        return ModKeyMappings.DROP_ALL.get();
    }
}
