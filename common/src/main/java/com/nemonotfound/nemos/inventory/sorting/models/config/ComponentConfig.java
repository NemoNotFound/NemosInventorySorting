package com.nemonotfound.nemos.inventory.sorting.models.config;

public record ComponentConfig( //TODO: Rethink
        String componentName, //TODO: name to id
        boolean isEnabled,
        Integer xOffset,
        Integer rightXOffset,
        Integer yOffset,
        int width,
        int height
) {
}
