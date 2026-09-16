package com.nemonotfound.nemos.inventory.sorting.factory;

import com.nemonotfound.nemos.inventory.sorting.gui.components.buttons.AbstractInventoryButton;
import com.nemonotfound.nemos.inventory.sorting.gui.components.buttons.MoveAllButton;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class MoveAllButtonFactory extends ButtonFactory {

    private static MoveAllButtonFactory INSTANCE;

    private MoveAllButtonFactory() {}

    public static MoveAllButtonFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MoveAllButtonFactory();
        }

        return INSTANCE;
    }

    @Override
    public AbstractInventoryButton createButton(
            int startIndex, int endIndex, int leftPos, int topPos, int xOffset, int yOffset, int width,
            int height, AbstractContainerMenu menu, boolean isInventoryButton
    ) {
        var buttonName = Component.translatable("nemos_inventory_sorting.gui.moveAll");
        var shiftButtonName = Component.translatable("nemos_inventory_sorting.gui.moveAllShift");
        var builder = new AbstractInventoryButton.Builder<>(MoveAllButton.class)
                .startIndex(startIndex)
                .endIndex(endIndex)
                .x(getLeftPosWithOffset(leftPos, xOffset))
                .y(topPos + yOffset)
                .xOffset(xOffset)
                .width(width)
                .height(height)
                .buttonName(buttonName)
                .shiftButtonName(shiftButtonName)
                .menu(menu)
                .isInventoryButton(isInventoryButton);

        return builder.build();
    }
}
