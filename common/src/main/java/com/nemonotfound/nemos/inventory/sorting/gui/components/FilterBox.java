package com.nemonotfound.nemos.inventory.sorting.gui.components;

import com.nemonotfound.nemos.inventory.sorting.config.model.FilterConfig;
import com.nemonotfound.nemos.inventory.sorting.config.service.ConfigService;
import com.nemonotfound.nemos.inventory.sorting.model.FilterResult;
import com.nemonotfound.nemos.inventory.sorting.service.FilterService;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;

import java.util.List;
import java.util.Map;

import static com.nemonotfound.nemos.inventory.sorting.config.DefaultConfigValues.FILTER_CONFIG_PATH;

public class FilterBox extends EditBox implements RecipeBookUpdatable {

    private static final Component FILTER_HINT = Component.translatable("nemos_inventory_sorting.gui.inventory.itemFilter")
            .withStyle(ChatFormatting.ITALIC)
            .withStyle(ChatFormatting.GRAY);

    private final FilterService filterService;
    private final ConfigService configService;
    private final int xOffset;

    public FilterBox(Font font, int x, int y, int xOffset, int yOffset, int width, int height, Component message) {
        super(font, x + xOffset, y + yOffset, width, height, message);
        this.filterService = FilterService.getInstance();
        this.configService = ConfigService.getInstance();
        this.xOffset = xOffset;
        this.setTextColor(-1);
        this.setVisible(true);
        this.setMaxLength(50);
        this.setBordered(true);
        this.setCanLoseFocus(true);
        this.setFocused(false);
        this.setHint(FILTER_HINT);
    }

    public void updateXPosition(int leftPos) {
        this.setX(leftPos + this.xOffset);
    }

    public Map<FilterResult, List<Slot>> filterSlots(NonNullList<Slot> slots, String filter) {
        return filterService.filterSlots(slots, filter);
    }

    public void updateAndSaveFilter(FilterConfig filterConfig) {
        var filter = filterConfig.isFilterPersistent() ? getValue() : "";

        filterConfig.setFilter(filter);
        configService.writeConfig(true, FILTER_CONFIG_PATH, filterConfig);
    }
}
