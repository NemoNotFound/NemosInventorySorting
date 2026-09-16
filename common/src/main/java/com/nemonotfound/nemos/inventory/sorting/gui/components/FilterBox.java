package com.nemonotfound.nemos.inventory.sorting.gui.components;

import com.nemonotfound.nemos.inventory.sorting.config.model.FilterConfig;
import com.nemonotfound.nemos.inventory.sorting.config.service.ConfigService;
import com.nemonotfound.nemos.inventory.sorting.model.FilterResult;
import com.nemonotfound.nemos.inventory.sorting.service.FilterService;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

import static com.nemonotfound.nemos.inventory.sorting.config.DefaultConfigValues.FILTER_CONFIG_PATH;
import static com.nemonotfound.nemos.inventory.sorting.Constants.MOD_ID;

public class FilterBox extends EditBox implements RecipeBookUpdatable {

    private static final int TEXT_X_OFFSET = 4;
    private static final int TEXT_Y_OFFSET = 3;
    private static final int HORIZONTAL_TEXT_PADDING = 8;
    private static final int DEFAULT_TEXT_COLOR = -12566464;
    private static final int DARK_MODE_TEXT_COLOR = -1;
    private static final ResourceLocation FOCUSED_TEXTURE = ResourceLocation.tryBuild(MOD_ID, "textures/gui/sprites/widget/filter_box.png");
    private static final ResourceLocation HOVERED_TEXTURE = ResourceLocation.tryBuild(MOD_ID, "textures/gui/sprites/widget/filter_box_hovered.png");
    private static final ResourceLocation UNFOCUSED_TEXTURE = ResourceLocation.tryBuild(MOD_ID, "textures/gui/sprites/widget/filter_box_unfocused.png");

    private static final Component FILTER_HINT = Component.translatable("nemos_inventory_sorting.gui.inventory.itemFilter")
            .withStyle(ChatFormatting.ITALIC)
            .withStyle(ChatFormatting.WHITE);

    private final FilterService filterService;
    private final ConfigService configService;
    private final int xOffset;

    public FilterBox(Font font, int x, int y, int xOffset, int yOffset, int width, int height, Component message) {
        super(font, x + xOffset, y + yOffset, width, height, message);
        this.filterService = FilterService.getInstance();
        this.configService = ConfigService.getInstance();
        this.xOffset = xOffset;
        this.setTextColor(isDarkModeEnabled() ? DARK_MODE_TEXT_COLOR : DEFAULT_TEXT_COLOR);
        this.setTextColorUneditable(DEFAULT_TEXT_COLOR);
        this.setVisible(true);
        this.setMaxLength(50);
        this.setBordered(false);
        this.setCanLoseFocus(true);
        this.setFocused(false);
        this.setHint(FILTER_HINT);
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        var texture = isFocused() ? FOCUSED_TEXTURE : isHovered() ? HOVERED_TEXTURE : UNFOCUSED_TEXTURE;
        var x = getX();
        var y = getY();
        var width = getWidth();

        renderTexture(guiGraphics, texture, x, y, 0, 0, 0, width, getHeight(), width, getHeight());

        setX(x + TEXT_X_OFFSET);
        setY(y + TEXT_Y_OFFSET);
        setWidth(width - HORIZONTAL_TEXT_PADDING);
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        setX(x);
        setY(y);
        setWidth(width);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX - TEXT_X_OFFSET, mouseY);
    }

    public void updateXPosition(int leftPos) {
        this.setX(leftPos - 1 + this.xOffset);
    }

    public Map<FilterResult, List<Slot>> filterSlots(NonNullList<Slot> slots, String filter) {
        return filterService.filterSlots(slots, filter);
    }

    public void updateAndSaveFilter(FilterConfig filterConfig) {
        var filter = filterConfig.isFilterPersistent() ? getValue() : "";

        filterConfig.setFilter(filter);
        configService.writeConfig(true, FILTER_CONFIG_PATH, filterConfig);
    }

    private boolean isDarkModeEnabled() {
        return Minecraft.getInstance().getResourcePackRepository().getSelectedIds().stream()
                .anyMatch(packId -> packId.contains("nemos_inventory_sorting") && packId.contains("dark_mode"));
    }
}
