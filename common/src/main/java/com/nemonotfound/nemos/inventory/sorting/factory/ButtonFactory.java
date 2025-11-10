package com.nemonotfound.nemos.inventory.sorting.factory;

public abstract class ButtonFactory implements ButtonCreator {

    protected int getLeftPosWithOffset(int leftPos, int offset) {
        return leftPos + offset;
    }
}
