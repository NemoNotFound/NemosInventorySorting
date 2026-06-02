package com.nemonotfound.nemos.inventory.sorting.helper;

import com.nemonotfound.nemos.inventory.sorting.enums.config.ConfigId;
import com.nemonotfound.nemos.inventory.sorting.factory.ButtonCreator;

public record ButtonTypeMapping(ConfigId configId, ButtonCreator factory, int defaultYOffset, boolean isInventoryButton) {
}
