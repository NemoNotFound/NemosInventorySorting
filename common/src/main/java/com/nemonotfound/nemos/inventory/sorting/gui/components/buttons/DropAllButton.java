package com.nemonotfound.nemos.inventory.sorting.gui.components.buttons;

import com.nemonotfound.nemos.inventory.sorting.client.InventorySortingKeyMappings;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

import static com.nemonotfound.nemos.inventory.sorting.Constants.MOD_ID;

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
    public void onClick(@NotNull MouseButtonEvent mouseButtonEvent, boolean isDoubleClick) {
        interactWithAllItems(ClickType.THROW, 1);
    }

    @Override
    protected KeyMapping getKeyMapping() {
        if (isInventoryButton) {
            return InventorySortingKeyMappings.DROP_ALL_INVENTORY.get();
        }

        return InventorySortingKeyMappings.DROP_ALL.get();
    }
}
