package com.nemonotfound.nemos.inventory.sorting.factory;

import com.nemonotfound.nemos.inventory.sorting.gui.components.buttons.AbstractContainerButton;

public abstract class ButtonFactory<T extends AbstractContainerButton> implements ButtonCreator<T> {

    protected int getLeftPosWithOffset(int leftPos, int offset) {
        return leftPos + offset;
    }
}
