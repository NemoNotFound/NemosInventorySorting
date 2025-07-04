package com.devnemo.nemos.inventory.sorting.factory;

import com.devnemo.nemos.inventory.sorting.config.model.FilterConfig;
import com.devnemo.nemos.inventory.sorting.gui.components.buttons.AbstractFilterToggleButton;

public interface FilterButtonCreator {

    AbstractFilterToggleButton createButton(
            int leftPos,
            int topPos,
            int xOffset,
            int yOffset,
            int width,
            int height,
            FilterConfig filterConfig
    );
}
