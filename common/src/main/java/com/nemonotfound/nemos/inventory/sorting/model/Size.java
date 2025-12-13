package com.nemonotfound.nemos.inventory.sorting.model;

import com.nemonotfound.nemos.inventory.sorting.Constants;

public record Size(int width, int height, int defaultSize) {

    public Size {
        int minSize = 1;

        if (width < minSize || height < minSize) {
            Constants.LOG.warn(
                    "Invalid button dimensions: width={}, height={}. Minimum is: {}. Falling back to default size: {}. Please update the configuration.",
                    width,
                    height,
                    minSize,
                    defaultSize

            );

            width = defaultSize;
            height = defaultSize;
        }
    }
}
