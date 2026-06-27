package com.nemonotfound.nemos.inventory.sorting.gui.components;

import com.nemonotfound.nemos.inventory.sorting.helper.WidgetSpritesGetter;
import com.nemonotfound.nemos.inventory.sorting.models.config.FilterConfig;
import com.nemonotfound.nemos.inventory.sorting.service.config.ConfigService;
import com.nemonotfound.nemos.inventory.sorting.enums.FilterResult;
import com.nemonotfound.nemos.inventory.sorting.service.FilterService;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.Slot;

import java.util.List;
import java.util.Map;

import static com.nemonotfound.nemos.inventory.sorting.Constants.MOD_ID;
import static com.nemonotfound.nemos.inventory.sorting.config.DefaultConfigValues.FILTER_CONFIG_PATH;

public class FilterBox extends EditBox implements RecipeBookUpdatable, WidgetSpritesGetter {

    private static final WidgetSprites SPRITES = new WidgetSprites(
            Identifier.fromNamespaceAndPath(MOD_ID, "widget/filter_box_unfocused"),
            Identifier.fromNamespaceAndPath(MOD_ID, "widget/filter_box")
    );
    private static final Identifier HOVERED_TEXTURE = Identifier.fromNamespaceAndPath(MOD_ID, "widget/filter_box_hovered");
    private static final Component FILTER_HINT = Component.translatable("nemos_inventory_sorting.gui.inventory.itemFilter")
            .withStyle(ChatFormatting.ITALIC)
            .withStyle(ChatFormatting.WHITE);

    private final FilterService filterService;
    private final ConfigService configService;
    private final int xOffset;

    public FilterBox(Font font, int x, int y, int xOffset, int yOffset, int width, int height, Component message) {
        super(font, x + xOffset, y + yOffset, width, height, message);

        this.filterService = FilterService.getInstance();
        this.configService = ConfigService.INSTANCE;
        this.xOffset = xOffset;

        this.setTextColor(-12566464);
        this.setTextShadow(false);
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

    public void updateAndSaveFilter() {
        FilterConfig filterConfig = FilterConfig.INSTANCE;
        var filter = filterConfig.isFilterPersistent() ? getValue() : "";

        filterConfig.setFilter(filter);
        configService.writeConfig(true, FILTER_CONFIG_PATH, filterConfig);
    }

    @Override
    public WidgetSprites nemosInventorySorting$getWidgetSprites() {
        return SPRITES;
    }

    @Override
    public Identifier nemosInventorySorting$getHoveredTexture() {
        return HOVERED_TEXTURE;
    }
}
