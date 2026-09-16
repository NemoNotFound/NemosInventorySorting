package com.nemonotfound.nemos.inventory.sorting.helper;

import com.nemonotfound.nemos.inventory.sorting.factory.ButtonCreator;

public record ButtonTypeMapping(String componentName, ButtonCreator factory, int defaultYOffset, boolean isInventoryButton) {
}
