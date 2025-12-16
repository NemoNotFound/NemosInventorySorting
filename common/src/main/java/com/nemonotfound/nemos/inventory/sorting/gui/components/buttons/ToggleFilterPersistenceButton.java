package com.nemonotfound.nemos.inventory.sorting.gui.components.buttons;

import com.nemonotfound.nemos.inventory.sorting.client.InventorySortingKeyMappings;
import com.nemonotfound.nemos.inventory.sorting.config.model.FilterConfig;
import com.nemonotfound.nemos.inventory.sorting.config.service.ConfigService;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import static com.nemonotfound.nemos.inventory.sorting.Constants.MOD_ID;
import static com.nemonotfound.nemos.inventory.sorting.config.DefaultConfigValues.FILTER_CONFIG_PATH;

public class ToggleFilterPersistenceButton extends AbstractButton {

    private final Identifier toggleOffTexture = Identifier.fromNamespaceAndPath(MOD_ID, "filter_persistence_toggle_off");
    private final Identifier toggleOnTexture = Identifier.fromNamespaceAndPath(MOD_ID, "filter_persistence_toggle_on");
    private final Identifier toggleOffHoverTexture = Identifier.fromNamespaceAndPath(MOD_ID, "filter_persistence_toggle_off_highlighted");
    private final Identifier toggleOnHoverTexture = Identifier.fromNamespaceAndPath(MOD_ID, "filter_persistence_toggle_on_highlighted");
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

    private Identifier getToggleOffHoverTexture() {
        return toggleOffHoverTexture;
    }

    private Identifier getToggleOnHoverTexture() {
        return toggleOnHoverTexture;
    }

    private Identifier getToggleOffTexture() {
        return toggleOffTexture;
    }

    private Identifier getToggleOnTexture() {
        return toggleOnTexture;
    }

    @Override
    protected Identifier getTexture() {
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
