package com.nemonotfound.nemos.inventory.sorting.factory;

import com.nemonotfound.nemos.inventory.sorting.config.model.FilterConfig;
import com.nemonotfound.nemos.inventory.sorting.gui.components.buttons.AbstractFilterToggleButton;
import com.nemonotfound.nemos.inventory.sorting.gui.components.buttons.filter.ToggleFilterPersistenceButton;
import net.minecraft.network.chat.Component;

public class ToggleFilterPersistenceButtonFactory implements FilterButtonCreator {

    private static ToggleFilterPersistenceButtonFactory INSTANCE;

    private ToggleFilterPersistenceButtonFactory() {}

    public static ToggleFilterPersistenceButtonFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ToggleFilterPersistenceButtonFactory();
        }

        return INSTANCE;
    }

    @Override
    public AbstractFilterToggleButton createButton(int leftPos, int topPos, int xOffset, int yOffset, int width, int height, FilterConfig filterConfig) {
        var buttonName = Component.translatable("nemos_inventory_sorting.gui.toggleFilterPersistence");
        var builder = new AbstractFilterToggleButton.Builder<>(ToggleFilterPersistenceButton.class)
                .x(leftPos + xOffset)
                .y(topPos + yOffset)
                .xOffset(xOffset)
                .width(width)
                .height(height)
                .buttonName(buttonName)
                .filterConfig(filterConfig);

        return builder.build();
    }
}
