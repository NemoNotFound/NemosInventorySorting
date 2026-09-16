package com.nemonotfound.nemos.inventory.sorting.gui.components.buttons.filter;

import com.nemonotfound.nemos.inventory.sorting.ModKeyMappings;
import com.nemonotfound.nemos.inventory.sorting.config.service.ConfigService;
import com.nemonotfound.nemos.inventory.sorting.gui.components.buttons.AbstractFilterToggleButton;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import static com.nemonotfound.nemos.inventory.sorting.Constants.MOD_ID;
import static com.nemonotfound.nemos.inventory.sorting.config.DefaultConfigValues.FILTER_CONFIG_PATH;

public class ToggleFilterPersistenceButton extends AbstractFilterToggleButton {

    private final ResourceLocation toggleOffTexture = ResourceLocation.tryBuild(MOD_ID, "textures/gui/sprites/filter_persistence_toggle_off.png");
    private final ResourceLocation toggleOnTexture = ResourceLocation.tryBuild(MOD_ID, "textures/gui/sprites/filter_persistence_toggle_on.png");
    private final ResourceLocation toggleOffHoverTexture = ResourceLocation.tryBuild(MOD_ID, "textures/gui/sprites/filter_persistence_toggle_off_highlighted.png");
    private final ResourceLocation toggleOnHoverTexture = ResourceLocation.tryBuild(MOD_ID, "textures/gui/sprites/filter_persistence_toggle_on_highlighted.png");
    private final Component toggleOnComponent = Component.translatable("nemos_inventory_sorting.gui.toggleFilterPersistence.toggleOn");
    private final Component toggleOffComponent = Component.translatable("nemos_inventory_sorting.gui.toggleFilterPersistence.toggleOff");
    private final ConfigService configService;

    public ToggleFilterPersistenceButton(Builder<? extends AbstractFilterToggleButton> builder) {
        super(builder);
        configService = ConfigService.getInstance();
        setTooltip();
    }

    @Override
    protected ResourceLocation getToggleOffHoverTexture() {
        return toggleOffHoverTexture;
    }

    @Override
    protected ResourceLocation getToggleOnHoverTexture() {
        return toggleOnHoverTexture;
    }

    @Override
    protected ResourceLocation getToggleOffTexture() {
        return toggleOffTexture;
    }

    @Override
    protected ResourceLocation getToggleOnTexture() {
        return toggleOnTexture;
    }

    @Override
    protected void setTooltip() {
        var tooltipComponent = filterConfig.isFilterPersistent() ? toggleOffComponent : toggleOnComponent;

        setTooltip(Tooltip.create(tooltipComponent));
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        filterConfig.toggleFilterPersistence();

        configService.writeConfig(true, FILTER_CONFIG_PATH, filterConfig);
        setTooltip();
    }

    @Override
    protected KeyMapping getKeyMapping() {
        return ModKeyMappings.TOGGLE_FILTER_PERSISTENCE.get();
    }
}
