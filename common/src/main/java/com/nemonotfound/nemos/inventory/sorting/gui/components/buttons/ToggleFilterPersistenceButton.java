package com.nemonotfound.nemos.inventory.sorting.gui.components.buttons;

import com.nemonotfound.nemos.inventory.sorting.client.InventorySortingKeyMappings;
import com.nemonotfound.nemos.inventory.sorting.config.model.FilterConfig;
import com.nemonotfound.nemos.inventory.sorting.config.service.ConfigService;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import static com.nemonotfound.nemos.inventory.sorting.Constants.MOD_ID;
import static com.nemonotfound.nemos.inventory.sorting.config.DefaultConfigValues.FILTER_CONFIG_PATH;

public class ToggleFilterPersistenceButton extends AbstractButton {

    private final ResourceLocation toggleOffTexture = ResourceLocation.fromNamespaceAndPath(MOD_ID, "filter_persistence_toggle_off");
    private final ResourceLocation toggleOnTexture = ResourceLocation.fromNamespaceAndPath(MOD_ID, "filter_persistence_toggle_on");
    private final ResourceLocation toggleOffHoverTexture = ResourceLocation.fromNamespaceAndPath(MOD_ID, "filter_persistence_toggle_off_highlighted");
    private final ResourceLocation toggleOnHoverTexture = ResourceLocation.fromNamespaceAndPath(MOD_ID, "filter_persistence_toggle_on_highlighted");
    private final Component toggleOnComponent = Component.translatable("nemos_inventory_sorting.gui.toggleFilterPersistence.toggleOn");
    private final Component toggleOffComponent = Component.translatable("nemos_inventory_sorting.gui.toggleFilterPersistence.toggleOff");

    private final ConfigService configService;
    private final FilterConfig filterConfig;

    public ToggleFilterPersistenceButton(int x, int y, int xOffset, int width, int height, Component buttonName, FilterConfig filterConfig) {
        super(x, y, xOffset, width, height, buttonName);
        configService = ConfigService.getInstance();
        this.filterConfig = filterConfig;
        setTooltip();
    }

    private ResourceLocation getToggleOffHoverTexture() {
        return toggleOffHoverTexture;
    }

    private ResourceLocation getToggleOnHoverTexture() {
        return toggleOnHoverTexture;
    }

    private ResourceLocation getToggleOffTexture() {
        return toggleOffTexture;
    }

    private ResourceLocation getToggleOnTexture() {
        return toggleOnTexture;
    }

    @Override
    protected ResourceLocation getTexture() {
        return filterConfig.isFilterPersistent()
                ? this.isHovered() ? getToggleOnHoverTexture() : getToggleOnTexture()
                : this.isHovered() ? getToggleOffHoverTexture() : getToggleOffTexture();
    }

    private void setTooltip() {
        var tooltipComponent = filterConfig.isFilterPersistent() ? toggleOffComponent : toggleOnComponent;

        setTooltip(Tooltip.create(tooltipComponent));
    }

    @Override
    public void onClick(@NotNull MouseButtonEvent mouseButtonEvent, boolean isDoubleClick) {
        filterConfig.toggleFilterPersistence();

        configService.writeConfig(true, FILTER_CONFIG_PATH, filterConfig);
        setTooltip();
    }

    @Override
    protected KeyMapping getKeyMapping() {
        return InventorySortingKeyMappings.TOGGLE_FILTER_PERSISTENCE.get();
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {

    }
}
