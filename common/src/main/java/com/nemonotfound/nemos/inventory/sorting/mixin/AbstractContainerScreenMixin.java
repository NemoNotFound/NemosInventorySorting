package com.nemonotfound.nemos.inventory.sorting.mixin;

import com.nemonotfound.nemos.inventory.sorting.config.model.ComponentConfig;
import com.nemonotfound.nemos.inventory.sorting.config.model.FilterConfig;
import com.nemonotfound.nemos.inventory.sorting.config.model.LockedSlotsConfig;
import com.nemonotfound.nemos.inventory.sorting.config.model.SettingsConfig;
import com.nemonotfound.nemos.inventory.sorting.config.service.ConfigService;
import com.nemonotfound.nemos.inventory.sorting.factory.*;
import com.nemonotfound.nemos.inventory.sorting.gui.components.FilterBox;
import com.nemonotfound.nemos.inventory.sorting.gui.components.buttons.AbstractInventoryButton;
import com.nemonotfound.nemos.inventory.sorting.helper.ButtonTypeMapping;
import com.nemonotfound.nemos.inventory.sorting.model.FilterResult;
import com.nemonotfound.nemos.inventory.sorting.model.LockedSlot;
import com.nemonotfound.nemos.inventory.sorting.service.InventoryInteractionService;
import com.nemonotfound.nemos.inventory.sorting.service.HoveredSlotRangeService;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static com.nemonotfound.nemos.inventory.sorting.Constants.MOD_ID;
import static com.nemonotfound.nemos.inventory.sorting.Constants.NEMOS_BACKPACKS_MOD_ID;
import static com.nemonotfound.nemos.inventory.sorting.Constants.REINFORCED_BARRELS_MOD_ID;
import static com.nemonotfound.nemos.inventory.sorting.Constants.REINFORCED_CHESTS_MOD_ID;
import static com.nemonotfound.nemos.inventory.sorting.Constants.REINFORCED_SHULKER_BOXES_MOD_ID;
import static com.nemonotfound.nemos.inventory.sorting.ModKeyMappings.QUICK_SEARCH;
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
    public abstract AbstractContainerMenu getMenu();

    @Shadow
    protected int inventoryLabelY;
    @Shadow
    protected int imageWidth;
    @Unique
    private FilterBox nemosInventorySorting$filterBox;
    @Unique
    private FilterConfig nemosInventorySorting$filterConfig;
    @Unique
    private static final ResourceLocation HIGHLIGHTED_SLOT = ResourceLocation.tryBuild(MOD_ID, "textures/gui/sprites/container/highlighted_slot.png");
    @Unique
    private static final ResourceLocation HIGHLIGHTED_SLOT_INCLUDED_ITEM = ResourceLocation.tryBuild(MOD_ID, "textures/gui/sprites/container/highlighted_slot_included_item.png");
    @Unique
    private static final ResourceLocation DIMMED_SLOT = ResourceLocation.tryBuild(MOD_ID, "textures/gui/sprites/container/dimmed_slot.png");
    @Unique
    private static final ResourceLocation LOCKED_SLOT = ResourceLocation.tryBuild(MOD_ID, "textures/gui/sprites/container/locked_slot.png");
    @Shadow
    protected Slot hoveredSlot;
    @Unique
    private final Set<Slot> nemosInventorySorting$lockedDuringDrag = new HashSet<>();
    @Unique
    private boolean nemosInventorySorting$displayLockedSlots;
    @Unique
    private Slot nemosInventorySorting$previousQuickMoveSlot;
    @Unique
    private int nemosInventorySorting$inventoryEndIndex;
    @Unique
    private int nemosInventorySorting$containerSize;
    @Unique
    private int nemosInventorySorting$filterBoxWidth = 0;

    @Unique
    private final ConfigService nemosInventorySorting$configService = ConfigService.getInstance();
    @Unique
    private final List<AbstractWidget> nemosInventorySorting$widgets = new ArrayList<>();

    protected AbstractContainerScreenMixin(Component component) {
        super(component);
    }

    @Inject(method = "init", at = @At(value = "TAIL"))
    public void init(CallbackInfo ci) {
        nemosInventorySorting$configService.loadSettingsConfig();
        nemosInventorySorting$inventoryEndIndex = getMenu().slots.size() - 9;

        if (getMenu() instanceof InventoryMenu) {
            nemosInventorySorting$inventoryEndIndex--;
        }

        nemosInventorySorting$containerSize = nemosInventorySorting$inventoryEndIndex - 27;
        var componentConfigs = nemosInventorySorting$configService.readOrGetDefaultComponentConfigs();

        if (nemosInventorySorting$shouldHaveFilter()) {
            nemosInventorySorting$filterConfig = nemosInventorySorting$configService.readOrGetDefaultFilterConfig();

            nemosInventorySorting$initFilter(componentConfigs);
        }

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

    @Unique
    private void nemosInventorySorting$initFilter(List<ComponentConfig> configs) {
        var optionalComponentConfig = nemosInventorySorting$configService.getOrDefaultComponentConfig(configs, ITEM_FILTER);

        if (optionalComponentConfig.isEmpty()) {
            return;
        }

        var config = optionalComponentConfig.get();

        if (!config.isEnabled()) {
            return;
        }

        nemosInventorySorting$filterBoxWidth = config.width();
        var xOffset = config.xOffset() != null ? config.xOffset() : 1;
        var yOffset = config.yOffset() != null ? config.yOffset() : Y_OFFSET_ITEM_FILTER;

        nemosInventorySorting$createSearchBox(xOffset, yOffset, nemosInventorySorting$filterBoxWidth, config.height(), nemosInventorySorting$filterConfig.getFilter());
        nemosInventorySorting$createButton(configs, FILTER_PERSISTENCE_TOGGLE, ToggleFilterPersistenceButtonFactory.getInstance());
    }

    @Inject(method = "onClose", at = @At("TAIL"))
    private void onClose(CallbackInfo ci) {
        if (nemosInventorySorting$filterBox == null) {
            return;
        }

        nemosInventorySorting$filterBox.updateAndSaveFilter(nemosInventorySorting$filterConfig);
    }

    @Unique
    private void nemosInventorySorting$createSearchBox(int xOffset, int yOffset, int width, int height, String filter) {
        nemosInventorySorting$filterBox = new FilterBox(
                font,
                leftPos - 1,
                topPos,
                xOffset,
                yOffset,
                width,
                height,
                Component.translatable("nemos_inventory_sorting.itemFilter")
        );

        this.addRenderableWidget(nemosInventorySorting$filterBox);
        nemosInventorySorting$filterBox.setValue(filter);
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    public void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        nemosInventorySorting$updateShiftState();

        if (this.nemosInventorySorting$filterBox != null) {
            if (this.nemosInventorySorting$filterBox.isFocused() && keyCode != 256) {
                cir.setReturnValue(this.nemosInventorySorting$filterBox.keyPressed(keyCode, scanCode, modifiers));
                return;
            }

            if (SettingsConfig.INSTANCE.areKeyMappingsEnabled() && !this.nemosInventorySorting$filterBox.isFocused() && hasControlDown() && keyCode == 70) {
                nemosInventorySorting$handleQuickSearch(cir);

                return;
            }
        }

        if (nemosInventorySorting$handleHoveredKey(keyCode, scanCode, modifiers)) {
            cir.setReturnValue(true);
            return;
        }

        if (nemosInventorySorting$triggerActionOnWidget(widget -> widget.keyPressed(keyCode, scanCode, modifiers))) {
            cir.setReturnValue(true);
            return;
        }

        if (SettingsConfig.INSTANCE.isSlotLockingEnabled() && hasAltDown()) {
            nemosInventorySorting$displayLockedSlots = true;
        }
    }

    @Unique
    private boolean nemosInventorySorting$handleHoveredKey(int keyCode, int scanCode, int modifiers) {
        if (hoveredSlot == null) return false;
        var slotRange = HoveredSlotRangeService.INSTANCE.getSlotRange(
                getMenu(), hoveredSlot, nemosInventorySorting$shouldHaveStorageContainerButtons(),
                SettingsConfig.INSTANCE.shouldIncludeHotbar(hasShiftDown())
        );
        if (slotRange.isEmpty()) return false;
        var range = slotRange.get();
        return nemosInventorySorting$widgets.stream()
                .filter(AbstractInventoryButton.class::isInstance)
                .map(AbstractInventoryButton.class::cast)
                .filter(button -> button.matchesKey(keyCode, scanCode) && button.matchesSlotRange(range.startIndex(), range.endIndex()))
                .findFirst().map(button -> {
                    button.activateHoverKeyMapping();
                    return true;
                }).orElse(false);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        nemosInventorySorting$updateShiftState();

        if (nemosInventorySorting$triggerActionOnWidget(widget -> widget.keyReleased(keyCode, scanCode, modifiers))) {
            return true;
        }

        nemosInventorySorting$displayLockedSlots = hasAltDown();
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        nemosInventorySorting$updateShiftState();

        Optional<GuiEventListener> optional = this.getChildAt(mouseX, mouseY);

        if (optional.isEmpty()) {
            for (GuiEventListener guiEventListener : this.children()) {
                guiEventListener.setFocused(false);
            }
        }

        if (SettingsConfig.INSTANCE.areKeyMappingsEnabled() && this.nemosInventorySorting$filterBox != null && !this.nemosInventorySorting$filterBox.isFocused() && hasControlDown() && QUICK_SEARCH.get().matchesMouse(button)) {
            nemosInventorySorting$handleQuickSearch(cir);

            return;
        }

        if (nemosInventorySorting$handleHoveredMouse(button)
                || nemosInventorySorting$triggerActionOnWidget(widget -> widget.mouseClicked(mouseX, mouseY, button))) {
            cir.setReturnValue(true);
            return;
        }

        if (SettingsConfig.INSTANCE.isSlotLockingEnabled() && hasAltDown()) {
            nemosInventorySorting$toggleLockedSlot();
            cir.setReturnValue(true);
        }

        if (SettingsConfig.INSTANCE.isSplitQuickMoveEnabled() && hasShiftDown() && button == 1 && hoveredSlot != null) {
            cir.setReturnValue(InventoryInteractionService.getInstance().splitQuickMove(getMenu(), hoveredSlot));
        }
    }

    @Unique
    private boolean nemosInventorySorting$handleHoveredMouse(int mouseButton) {
        if (hoveredSlot == null) return false;
        var slotRange = HoveredSlotRangeService.INSTANCE.getSlotRange(
                getMenu(), hoveredSlot, nemosInventorySorting$shouldHaveStorageContainerButtons(),
                SettingsConfig.INSTANCE.shouldIncludeHotbar(hasShiftDown())
        );
        if (slotRange.isEmpty()) return false;
        var range = slotRange.get();
        return nemosInventorySorting$widgets.stream().filter(AbstractInventoryButton.class::isInstance)
                .map(AbstractInventoryButton.class::cast)
                .filter(button -> button.matchesMouse(mouseButton) && button.matchesSlotRange(range.startIndex(), range.endIndex()))
                .findFirst().map(button -> {
                    button.activateHoverKeyMapping();
                    return true;
                }).orElse(false);
    }

    @Inject(method = "mouseDragged", at = @At("HEAD"), cancellable = true)
    private void mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY, CallbackInfoReturnable<Boolean> cir) {
        if (SettingsConfig.INSTANCE.isSlotLockingEnabled()
                && hasAltDown()
                && hoveredSlot != null
                && !nemosInventorySorting$lockedDuringDrag.contains(hoveredSlot)) {
            nemosInventorySorting$toggleLockedSlot();
            cir.setReturnValue(true);
        }

        if (SettingsConfig.INSTANCE.isDragQuickMoveEnabled() && hasShiftDown() && button == 0 && hoveredSlot != null && hoveredSlot != nemosInventorySorting$previousQuickMoveSlot) {
            minecraft.gameMode.handleInventoryMouseClick(getMenu().containerId, hoveredSlot.index, 0, ClickType.QUICK_MOVE, minecraft.player);
            nemosInventorySorting$previousQuickMoveSlot = hoveredSlot;
        }
        if (SettingsConfig.INSTANCE.isSplitQuickMoveEnabled()
                && hasShiftDown()
                && button == 1
                && hoveredSlot != null
                && hoveredSlot != nemosInventorySorting$previousQuickMoveSlot) {
            if (InventoryInteractionService.getInstance().splitQuickMove(getMenu(), hoveredSlot)) {
                nemosInventorySorting$previousQuickMoveSlot = hoveredSlot;
                cir.setReturnValue(true);
            }
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        var handled = SettingsConfig.INSTANCE.isScrollTransferEnabled() && hoveredSlot != null
                && InventoryInteractionService.getInstance().scrollTransfer(getMenu(), hoveredSlot, delta, hasShiftDown());
        return handled || super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Inject(method = "mouseReleased", at = @At("HEAD"))
    private void mouseReleased(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        nemosInventorySorting$lockedDuringDrag.clear();
        nemosInventorySorting$previousQuickMoveSlot = null;
    }

    @Unique
    private void nemosInventorySorting$toggleLockedSlot() {
        if (hoveredSlot == null || !nemosInventorySorting$isLockableSlot(hoveredSlot.index)) {
            return;
        }

        var lockedSlot = new LockedSlot(hoveredSlot.index - nemosInventorySorting$containerSize);
        if (!LockedSlotsConfig.INSTANCE.remove(lockedSlot)) {
            LockedSlotsConfig.INSTANCE.add(lockedSlot);
        }

        nemosInventorySorting$configService.writeConfig(true, LOCKED_SLOTS_CONFIG_PATH, LockedSlotsConfig.INSTANCE);
        nemosInventorySorting$lockedDuringDrag.add(hoveredSlot);
    }

    @Unique
    private boolean nemosInventorySorting$isLockableSlot(int slotIndex) {
        if (getMenu() instanceof InventoryMenu) {
            return slotIndex >= InventoryMenu.INV_SLOT_START && slotIndex < InventoryMenu.USE_ROW_SLOT_END;
        }

        return slotIndex >= nemosInventorySorting$containerSize && slotIndex < nemosInventorySorting$inventoryEndIndex + 9;
    }

    @Unique
    private void nemosInventorySorting$handleQuickSearch(CallbackInfoReturnable<Boolean> cir) {
        var filterBoxX = nemosInventorySorting$filterBox.getX();
        var filterBoxY = nemosInventorySorting$filterBox.getY();
        var optionalGuiEventListener = this.getChildAt(filterBoxX, filterBoxY);

        if (optionalGuiEventListener.isEmpty()) {
            return;
        }

        this.setFocused(optionalGuiEventListener.get());
        this.nemosInventorySorting$filterBox.setFocused(true);
        this.nemosInventorySorting$filterBox.onClick(filterBoxX + nemosInventorySorting$filterBoxWidth, nemosInventorySorting$filterBox.getY());
        cir.setReturnValue(true);
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
    private void nemosInventorySorting$updateShiftState() {
        nemosInventorySorting$widgets.stream()
                .filter(AbstractInventoryButton.class::isInstance)
                .map(AbstractInventoryButton.class::cast)
                .forEach(button -> button.setIsShiftKeyDown(hasShiftDown()));
    }

    @Inject(method = "render", at = @At("TAIL"))
    void renderHighlightedSlot(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        nemosInventorySorting$renderLockedSlots(guiGraphics);
        if (!nemosInventorySorting$shouldHaveFilter() || this.nemosInventorySorting$filterBox == null) {
            return;
        }

        var filter = this.nemosInventorySorting$filterBox.getValue();

        if (!filter.isEmpty()) {
            var filteredSlotMap = this.nemosInventorySorting$filterBox.filterSlots(getMenu().slots, filter);

            nemosInventorySorting$markSlots(filteredSlotMap.get(FilterResult.INCLUDED), guiGraphics, HIGHLIGHTED_SLOT);
            nemosInventorySorting$markSlots(filteredSlotMap.get(FilterResult.HAS_INCLUDED_ITEM), guiGraphics, HIGHLIGHTED_SLOT_INCLUDED_ITEM);
        }
    }

    @Unique
    private void nemosInventorySorting$renderLockedSlots(GuiGraphics guiGraphics) {
        if (!SettingsConfig.INSTANCE.isSlotLockingEnabled() || !nemosInventorySorting$displayLockedSlots) {
            return;
        }

        for (var lockedSlot : LockedSlotsConfig.INSTANCE.getLockedSlots()) {
            var slot = getMenu().getSlot(lockedSlot.index() + nemosInventorySorting$containerSize);
            guiGraphics.blit(LOCKED_SLOT, leftPos + slot.x, topPos + slot.y, 0, 0, 16, 16);
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;renderLabels(Lnet/minecraft/client/gui/GuiGraphics;II)V", shift = At.Shift.AFTER))
    void renderDimmedSlot(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (!nemosInventorySorting$shouldHaveFilter() || this.nemosInventorySorting$filterBox == null) {
            return;
        }

        var filter = this.nemosInventorySorting$filterBox.getValue();

        if (!filter.isEmpty()) {
            var filteredSlotMap = this.nemosInventorySorting$filterBox.filterSlots(getMenu().slots, filter);

            nemosInventorySorting$markSlots(filteredSlotMap.get(FilterResult.EXCLUDED), guiGraphics, DIMMED_SLOT);
        }
    }

    @Unique
    private boolean nemosInventorySorting$shouldHaveFilter() {
        return !(getMenu() instanceof CreativeModeInventoryScreen.ItemPickerMenu);
    }

    @Unique
    private boolean nemosInventorySorting$shouldHaveStorageContainerButtons() {
        var menu = getMenu();

        return menu instanceof ChestMenu
                || menu instanceof ShulkerBoxMenu
                || nemosInventorySorting$isModdedContainerMenu(
                        menu,
                        NEMOS_BACKPACKS_MOD_ID,
                        "com.nemonotfound.nemos.backpacks.world.inventory.BackpackMenu",
                        "com.devnemo.nemos.backpacks.world.inventory.BackpackMenu"
                )
                || nemosInventorySorting$isModdedContainerMenu(
                        menu,
                        REINFORCED_CHESTS_MOD_ID,
                        "atonkish.reinfcore.screen.ReinforcedStorageScreenHandler"
                )
                || nemosInventorySorting$isModdedContainerMenu(
                        menu,
                        REINFORCED_BARRELS_MOD_ID,
                        "atonkish.reinfcore.screen.ReinforcedStorageScreenHandler"
                )
                || nemosInventorySorting$isModdedContainerMenu(
                        menu,
                        REINFORCED_SHULKER_BOXES_MOD_ID,
                        "atonkish.reinfcore.screen.ReinforcedStorageScreenHandler"
                );
    }

    @Unique
    private boolean nemosInventorySorting$isModdedContainerMenu(AbstractContainerMenu menu, String modId, String... classNames) {
        if (!MOD_LOADER_HELPER.isModLoaded(modId)) {
            return false;
        }

        for (var className : classNames) {
            try {
                if (Class.forName(className).isInstance(menu)) {
                    return true;
                }
            } catch (ClassNotFoundException ignored) {
            }
        }

        return false;
    }

    @Unique
    private boolean nemosInventorySorting$shouldHaveInventoryButtons() {
        return getMenu() instanceof InventoryMenu;
    }

    @Unique
    private boolean nemosInventorySorting$shouldHaveContainerInventorySortingButtons() {
        var menu = getMenu();

        return menu instanceof EnchantmentMenu ||
                menu instanceof FurnaceMenu ||
                menu instanceof SmokerMenu ||
                menu instanceof BlastFurnaceMenu ||
                menu instanceof CraftingMenu ||
                menu instanceof GrindstoneMenu ||
                menu instanceof BrewingStandMenu;
    }

    @Unique
    private void nemosInventorySorting$markSlots(
            List<Slot> slots,
            GuiGraphics guiGraphics,
            ResourceLocation texture
    ) {
        if (slots == null) {
            return;
        }

        for (Slot slot : slots) {
            var xPos = leftPos + slot.x;
            var yPos = topPos + slot.y;

            if (texture == DIMMED_SLOT) {
                guiGraphics.blit(texture, slot.x, slot.y, 0, 0, 16, 16);
                guiGraphics.fillGradient(RenderType.guiOverlay(), slot.x, slot.y, slot.x + 16, slot.y + 16, -2139062142, -2139062142, 0);

                continue;
            }

            guiGraphics.blit(texture, xPos, yPos, 0, 0, 16, 16);
        }
    }

    @Unique
    private void nemosInventorySorting$initStorageContainerButtons(List<ComponentConfig> componentConfigs) {
        var defaultInventoryYOffset = getMenu() instanceof ShulkerBoxMenu ? inventoryLabelY - 1 : inventoryLabelY - 2;

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
    private void nemosInventorySorting$createButton(List<ComponentConfig> configs, String componentName, FilterButtonCreator filterButtonCreator) {
        var optionalComponentConfig = nemosInventorySorting$configService.getOrDefaultComponentConfig(configs, componentName);

        if (optionalComponentConfig.isEmpty()) {
            return;
        }

        var config = optionalComponentConfig.get();

        if (!config.isEnabled()) {
            return;
        }

        var width = config.width();
        var xOffset = config.xOffset() != null ? config.xOffset() : nemosInventorySorting$filterBoxWidth + 3;
        var yOffset = config.yOffset() != null ? config.yOffset() : Y_OFFSET_ITEM_FILTER;
        var button = filterButtonCreator.createButton(leftPos, topPos, xOffset, yOffset, width, config.height(), nemosInventorySorting$filterConfig);

        nemosInventorySorting$widgets.add(button);
    }

    @Unique
    private void nemosInventorySorting$createButtons(List<ComponentConfig> configs,
                                                     ButtonTypeMapping... mappings) {
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

            nemosInventorySorting$createButton(mapping.factory(), mapping.isInventoryButton(), xOffset, yOffset, config.width(), config.height());
        }
    }

    @Unique
    private void nemosInventorySorting$createButton(ButtonCreator buttonCreator, boolean isInventoryButton, int xOffset, int yOffset, int width, int height) {
        var startIndex = isInventoryButton ? nemosInventorySorting$containerSize : 0;
        var endIndex = isInventoryButton ? nemosInventorySorting$inventoryEndIndex : nemosInventorySorting$containerSize;

        nemosInventorySorting$createButton(buttonCreator, startIndex, endIndex, xOffset, yOffset, width, height, isInventoryButton);
    }

    @Unique
    private void nemosInventorySorting$createButton(ButtonCreator buttonCreator, int startIndex, int endIndex, int xOffset, int yOffset, int width, int height, boolean isInventoryButton) {
        var sortButton = buttonCreator.createButton(startIndex, endIndex, leftPos, topPos, xOffset, yOffset, width, height, getMenu(), isInventoryButton);
        nemosInventorySorting$widgets.add(sortButton);
    }
}
