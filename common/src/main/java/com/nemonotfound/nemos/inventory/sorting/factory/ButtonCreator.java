package com.nemonotfound.nemos.inventory.sorting.factory;

import com.nemonotfound.nemos.inventory.sorting.gui.components.buttons.AbstractInventoryButton;
import net.minecraft.world.inventory.AbstractContainerMenu;

public interface ButtonCreator {

    AbstractInventoryButton createButton(
            int startIndex,
            int endIndex,
            int leftPos,
            int topPos,
            int xOffset,
            int yOffset,
            int width,
            int height,
            AbstractContainerMenu menu,
            boolean isInventoryButton
    );
}
