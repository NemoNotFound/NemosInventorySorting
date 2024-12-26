package com.nemonotfound.nemos.inventory.sorting.factory;

import com.nemonotfound.nemos.inventory.sorting.client.gui.components.AbstractSortButton;
import com.nemonotfound.nemos.inventory.sorting.client.gui.components.MoveAllButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;

public class MoveAllButtonFactory extends SortButtonFactory {

    private static MoveAllButtonFactory INSTANCE;

    private MoveAllButtonFactory() {}

    public static MoveAllButtonFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MoveAllButtonFactory();
        }

        return INSTANCE;
    }

    @Override
    public AbstractSortButton createButton(
            int startIndex, int endIndex, int leftPos, int topPos, int xOffset, int yOffset, int imageWidth, int width,
            int height, AbstractContainerScreen<?> containerScreen
    ) {
        Component component = Component.translatable("gui.nemosInventorySorting.move_all");
        AbstractSortButton.Builder<MoveAllButton> builder = new AbstractSortButton.Builder<>(MoveAllButton.class)
                .startIndex(startIndex)
                .endIndex(endIndex)
                .x(getLeftPosWithOffset(leftPos, imageWidth, xOffset))
                .y(topPos + yOffset)
                .xOffset(xOffset)
                .width(width)
                .height(height)
                .component(component)
                .containerScreen(containerScreen);

        return builder.build();
    }
}
