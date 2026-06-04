package com.nemonotfound.nemos.inventory.sorting.gui.components.buttons;

import com.nemonotfound.nemos.inventory.sorting.client.SortingKeyMappings;
import com.nemonotfound.nemos.inventory.sorting.models.config.FilterConfig;
import com.nemonotfound.nemos.inventory.sorting.service.config.ConfigService;
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

    public ToggleFilterPersistenceButton(int x, int y, int xOffset, int width, int height, Component buttonName) {
        super(x, y, xOffset, width, height, buttonName);
        configService = ConfigService.INSTANCE;
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
        return FilterConfig.INSTANCE.isFilterPersistent()
                ? this.isHovered() ? getToggleOnHoverTexture() : getToggleOnTexture()
                : this.isHovered() ? getToggleOffHoverTexture() : getToggleOffTexture();
    }

    private void setTooltip() {
        var tooltipComponent = FilterConfig.INSTANCE.isFilterPersistent() ? toggleOffComponent : toggleOnComponent;

        setTooltip(Tooltip.create(tooltipComponent));
    }

    @Override
    public void onClick(@NotNull MouseButtonEvent mouseButtonEvent, boolean isDoubleClick) {
        FilterConfig.INSTANCE.toggleFilterPersistence();

        configService.writeConfig(true, FILTER_CONFIG_PATH, FilterConfig.INSTANCE);
        setTooltip();
    }

    @Override
    protected KeyMapping getKeyMapping() {
        return SortingKeyMappings.TOGGLE_FILTER_PERSISTENCE.get();
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {
        this.defaultButtonNarrationText(narrationElementOutput);
    }
}
