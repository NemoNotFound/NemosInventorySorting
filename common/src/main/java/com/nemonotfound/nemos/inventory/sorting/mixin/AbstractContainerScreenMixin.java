package com.nemonotfound.nemos.inventory.sorting.mixin;

import com.nemonotfound.nemos.inventory.sorting.config.model.ComponentConfig;
import com.nemonotfound.nemos.inventory.sorting.config.service.ConfigService;
import com.nemonotfound.nemos.inventory.sorting.factory.*;
import com.nemonotfound.nemos.inventory.sorting.helper.ButtonTypeMapping;
import com.nemonotfound.nemos.inventory.sorting.model.Offset;
import com.nemonotfound.nemos.inventory.sorting.model.Position;
import com.nemonotfound.nemos.inventory.sorting.model.Size;
import com.nemonotfound.nemos.inventory.sorting.model.SlotRange;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.*;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static com.nemonotfound.nemos.inventory.sorting.Constants.*;
import static com.nemonotfound.nemos.inventory.sorting.NemosInventorySortingClientCommon.MOD_LOADER_HELPER;
import static com.nemonotfound.nemos.inventory.sorting.config.DefaultConfigValues.*;

//TODO: Refactor
@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin extends Screen {

    @Shadow
    protected int leftPos;
    @Shadow
    protected int topPos;
    @Shadow
    protected int inventoryLabelY;
    @Shadow
    protected int imageWidth;
    @Unique
    private int nemosInventorySorting$inventoryEndIndex;
    @Unique
    private int nemosInventorySorting$containerSize;

    @Unique
    private final ConfigService nemosInventorySorting$configService = ConfigService.getInstance();
    @Unique
    private final List<AbstractWidget> nemosInventorySorting$widgets = new ArrayList<>();

    protected AbstractContainerScreenMixin(Component component) {
        super(component);
    }

    @Inject(method = "init", at = @At(value = "TAIL"))
    public void init(CallbackInfo ci) {
        var menu = ((AbstractContainerScreen<?>) (Object) this).getMenu();
        nemosInventorySorting$inventoryEndIndex = menu.slots.size() - 9;

        if (menu instanceof InventoryMenu) {
            nemosInventorySorting$inventoryEndIndex--;
        }

        nemosInventorySorting$containerSize = nemosInventorySorting$inventoryEndIndex - 27;

        if (MOD_LOADER_HELPER.isModLoaded(NEMOS_BACKPACKS_MOD_ID) && menu instanceof InventoryMenu) {
            nemosInventorySorting$inventoryEndIndex--;
            nemosInventorySorting$containerSize--;
        }

        var componentConfigs = nemosInventorySorting$configService.readOrGetDefaultComponentConfigs();

        if (nemosInventorySorting$shouldHaveStorageContainerButtons()) {
            nemosInventorySorting$initStorageContainerButtons(componentConfigs);
        }

        if (nemosInventorySorting$shouldHaveInventoryButtons()) {
            nemosInventorySorting$initInventoryButtons(componentConfigs);
        }

        if (nemosInventorySorting$shouldHaveContainerInventorySortingButtons()) {
            nemosInventorySorting$initContainerInventoryButtons(componentConfigs);
        }

        for (AbstractWidget widget : nemosInventorySorting$widgets) {
            this.addRenderableWidget(widget);
        }
    }

    @Override
    protected void clearWidgets() {
        nemosInventorySorting$widgets.clear();
        super.clearWidgets();
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    public void keyPressed(KeyEvent keyEvent, CallbackInfoReturnable<Boolean> cir) {
        if (nemosInventorySorting$triggerActionOnWidget(widget -> widget.keyPressed(keyEvent))) {
            cir.setReturnValue(true);
        }
    }

    @Override
    public boolean keyReleased(@NotNull KeyEvent keyEvent) {
        if (nemosInventorySorting$triggerActionOnWidget(widget -> widget.keyReleased(keyEvent))) {
            return true;
        }

        return super.keyReleased(keyEvent);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void mouseClicked(MouseButtonEvent mouseButtonEvent, boolean bl, CallbackInfoReturnable<Boolean> cir) {
        if (nemosInventorySorting$triggerActionOnWidget(widget -> widget.mouseClicked(mouseButtonEvent, bl))) {
            cir.setReturnValue(true);
        }
    }

    @Unique
    private boolean nemosInventorySorting$triggerActionOnWidget(Function<AbstractWidget, Boolean> function) {
        for (var widget : nemosInventorySorting$widgets) {
            if (function.apply(widget)) {
                return true;
            }
        }

        return false;
    }

    @Unique
    private boolean nemosInventorySorting$shouldHaveStorageContainerButtons() {
        var menu = ((AbstractContainerScreen<?>) (Object) this).getMenu();

        return menu instanceof ChestMenu ||
                menu instanceof ShulkerBoxMenu ||
                nemosInventorySorting$isModdedContainerMenu(menu, NEMOS_BACKPACKS_MOD_ID, "com.nemonotfound.nemos.backpacks.world.inventory.BackpackMenu") ||
                nemosInventorySorting$isModdedContainerMenu(menu, REINFORCED_CHESTS_MOD_ID, "atonkish.reinfcore.screen.ReinforcedStorageScreenHandler") ||
                nemosInventorySorting$isModdedContainerMenu(menu, REINFORCED_BARRELS_MOD_ID, "atonkish.reinfcore.screen.ReinforcedStorageScreenHandler") ||
                nemosInventorySorting$isModdedContainerMenu(menu, REINFORCED_SHULKER_BOXES_MOD_ID, "atonkish.reinfcore.screen.ReinforcedStorageScreenHandler");
    }

    @Unique
    private boolean nemosInventorySorting$isModdedContainerMenu(AbstractContainerMenu menu, String modId, String className) {
        if (MOD_LOADER_HELPER.isModLoaded(modId)) {
            try {
                var clazz = Class.forName(className);

                if (clazz.isInstance(menu)) {
                    return true;
                }
            } catch (ClassNotFoundException ignored) {
            }
        }

        return false;
    }

    @Unique
    private boolean nemosInventorySorting$shouldHaveInventoryButtons() {
        return ((AbstractContainerScreen<?>) (Object) this).getMenu() instanceof InventoryMenu;
    }

    @Unique
    private boolean nemosInventorySorting$shouldHaveContainerInventorySortingButtons() {
        var menu = ((AbstractContainerScreen<?>) (Object) this).getMenu();

        return menu instanceof EnchantmentMenu ||
                menu instanceof FurnaceMenu ||
                menu instanceof SmokerMenu ||
                menu instanceof BlastFurnaceMenu ||
                menu instanceof CraftingMenu ||
                menu instanceof CrafterMenu ||
                menu instanceof GrindstoneMenu ||
                menu instanceof BrewingStandMenu;
    }

    @Unique
    private void nemosInventorySorting$initStorageContainerButtons(List<ComponentConfig> componentConfigs) {
        var defaultInventoryYOffset = ((AbstractContainerScreen<?>) (Object) this).getMenu() instanceof ShulkerBoxMenu ? inventoryLabelY - 1 : inventoryLabelY - 2;

        nemosInventorySorting$createButtons(
                componentConfigs,
                new ButtonTypeMapping(SORT_STORAGE_CONTAINER, SortButtonFactory.getInstance(), Y_OFFSET_CONTAINER, false),
                new ButtonTypeMapping(MOVE_SAME_STORAGE_CONTAINER, MoveSameButtonFactory.getInstance(), Y_OFFSET_CONTAINER, false),
                new ButtonTypeMapping(MOVE_ALL_STORAGE_CONTAINER, MoveAllButtonFactory.getInstance(), Y_OFFSET_CONTAINER, false),
                new ButtonTypeMapping(DROP_ALL_STORAGE_CONTAINER, DropAllButtonFactory.getInstance(), Y_OFFSET_CONTAINER, false),
                new ButtonTypeMapping(SORT_STORAGE_CONTAINER_INVENTORY, SortButtonFactory.getInstance(), defaultInventoryYOffset, true),
                new ButtonTypeMapping(MOVE_SAME_STORAGE_CONTAINER_INVENTORY, MoveSameButtonFactory.getInstance(), defaultInventoryYOffset, true),
                new ButtonTypeMapping(MOVE_ALL_STORAGE_CONTAINER_INVENTORY, MoveAllButtonFactory.getInstance(), defaultInventoryYOffset, true),
                new ButtonTypeMapping(DROP_ALL_STORAGE_CONTAINER_INVENTORY, DropAllButtonFactory.getInstance(), defaultInventoryYOffset, true)
        );
    }


    @Unique
    private void nemosInventorySorting$initInventoryButtons(List<ComponentConfig> componentConfigs) {
        nemosInventorySorting$createButtons(
                componentConfigs,
                new ButtonTypeMapping(SORT_INVENTORY, SortButtonFactory.getInstance(), Y_OFFSET_INVENTORY, true),
                new ButtonTypeMapping(DROP_ALL_INVENTORY, DropAllButtonFactory.getInstance(), Y_OFFSET_INVENTORY, true)
        );
    }

    @Unique
    private void nemosInventorySorting$initContainerInventoryButtons(List<ComponentConfig> componentConfigs) {
        var defaultInventoryYOffset = inventoryLabelY - 1;

        nemosInventorySorting$createButtons(
                componentConfigs,
                new ButtonTypeMapping(SORT_CONTAINER_INVENTORY, SortButtonFactory.getInstance(), defaultInventoryYOffset, true),
                new ButtonTypeMapping(DROP_ALL_CONTAINER_INVENTORY, DropAllButtonFactory.getInstance(), defaultInventoryYOffset, true)
        );
    }

    @Unique
    private void nemosInventorySorting$createButtons(List<ComponentConfig> configs, ButtonTypeMapping... mappings) {
        for (ButtonTypeMapping mapping : mappings) {
            var optionalConfig = nemosInventorySorting$configService.getOrDefaultComponentConfig(configs, mapping.componentName());

            if (optionalConfig.isEmpty()) {
                continue;
            }

            var config = optionalConfig.get();

            if (!config.isEnabled()) {
                continue;
            }

            var yOffset = config.yOffset() != null ? config.yOffset() : mapping.defaultYOffset();
            var xOffset = config.xOffset() != null ? config.xOffset() : imageWidth + config.rightXOffset();

            nemosInventorySorting$createButton(mapping.factory(), mapping.isInventoryButton(), new Offset(xOffset, yOffset), new Size(config.width(), config.height(), BUTTON_SIZE));
        }
    }

    @Unique
    private void nemosInventorySorting$createButton(ButtonCreator<?> buttonCreator, boolean isInventoryButton, Offset offset, Size size) {
        var startIndex = isInventoryButton ? nemosInventorySorting$containerSize : 0;
        var endIndex = isInventoryButton ? nemosInventorySorting$inventoryEndIndex : nemosInventorySorting$containerSize;
        var slotRange = new SlotRange(startIndex, endIndex);

        nemosInventorySorting$createButton(buttonCreator, slotRange, offset, size);
    }

    @Unique
    private void nemosInventorySorting$createButton(ButtonCreator<?> buttonCreator, SlotRange slotRange, Offset offset, Size size) {
        var position = new Position(leftPos, topPos);
        var sortButton = buttonCreator.createButton(slotRange, position, offset, size, ((AbstractContainerScreen<?>) (Object) this).getMenu());
        nemosInventorySorting$widgets.add(sortButton);
    }
}