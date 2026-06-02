package com.nemonotfound.nemos.inventory.sorting.mixin;

import com.nemonotfound.nemos.inventory.sorting.models.config.ComponentConfig;
import com.nemonotfound.nemos.inventory.sorting.service.config.ConfigService;
import com.nemonotfound.nemos.inventory.sorting.factory.DropAllButtonFactory;
import com.nemonotfound.nemos.inventory.sorting.factory.SortButtonFactory;
import com.nemonotfound.nemos.inventory.sorting.helper.ButtonTypeMapping;
import com.nemonotfound.nemos.inventory.sorting.helper.SortingWidgetGetter;
import com.nemonotfound.nemos.inventory.sorting.models.Offset;
import com.nemonotfound.nemos.inventory.sorting.models.Position;
import com.nemonotfound.nemos.inventory.sorting.models.Size;
import com.nemonotfound.nemos.inventory.sorting.models.SlotRange;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static com.nemonotfound.nemos.inventory.sorting.config.DefaultConfigValues.*;
import static com.nemonotfound.nemos.inventory.sorting.enums.config.ConfigId.DROP_ALL_INVENTORY;
import static com.nemonotfound.nemos.inventory.sorting.enums.config.ConfigId.SORT_INVENTORY;
import static net.minecraft.world.inventory.InventoryMenu.INV_SLOT_END;
import static net.minecraft.world.inventory.InventoryMenu.INV_SLOT_START;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractRecipeBookScreen<@NotNull InventoryMenu> {

    @Unique
    private final ConfigService nemosInventorySorting$configService = ConfigService.INSTANCE;

    private InventoryScreenMixin(InventoryMenu menu, RecipeBookComponent<?> recipeBookComponent, Inventory inventory, Component title) {
        super(menu, recipeBookComponent, inventory, title);
    }

    @Inject(method = "init", at = @At(value = "TAIL"))
    public void init(CallbackInfo ci) {
        var componentConfigs = nemosInventorySorting$configService.readOrGetDefaultComponentConfigs();

        nemosInventorySorting$createConfiguredButtons(
                componentConfigs,
                new ButtonTypeMapping(SORT_INVENTORY, SortButtonFactory.getInstance(), Y_OFFSET_INVENTORY, true),
                new ButtonTypeMapping(DROP_ALL_INVENTORY, DropAllButtonFactory.getInstance(), Y_OFFSET_INVENTORY, true)
        );
    }

    @Unique
    private void nemosInventorySorting$createConfiguredButtons(List<ComponentConfig> configs, ButtonTypeMapping... mappings) {
        var position = new Position(leftPos, topPos);
        var slotRange = new SlotRange(INV_SLOT_START, INV_SLOT_END);

        for (ButtonTypeMapping mapping : mappings) {
            nemosInventorySorting$configService.getOrDefault(configs, mapping.configId())
                    .filter(ComponentConfig::isEnabled)
                    .ifPresent(config -> nemosInventorySorting$createButtonFromConfig(mapping, config, position, slotRange));
        }
    }

    @Unique
    private void nemosInventorySorting$createButtonFromConfig(
            ButtonTypeMapping mapping,
            ComponentConfig config,
            Position position,
            SlotRange slotRange
    ) {
        ((SortingWidgetGetter) this).nemosInventorySorting$addSortingWidget(
                mapping.factory().createButton(
                        slotRange,
                        position,
                        nemosInventorySorting$resolveOffset(config, mapping),
                        new Size(config.width(), config.height(), BUTTON_SIZE),
                        getMenu()
                )
        );
    }

    @Unique
    private Offset nemosInventorySorting$resolveOffset(ComponentConfig config, ButtonTypeMapping mapping) {
        var yOffset = config.yOffset() != null ? config.yOffset() : mapping.defaultYOffset();
        var xOffset = config.xOffset() != null ? config.xOffset() : imageWidth + config.rightXOffset();

        return new Offset(xOffset, yOffset);
    }
}
