package com.nemonotfound.nemos.inventory.sorting.mixin;

import com.nemonotfound.nemos.inventory.sorting.factory.ButtonCreator;
import com.nemonotfound.nemos.inventory.sorting.factory.DropAllButtonFactory;
import com.nemonotfound.nemos.inventory.sorting.factory.MoveAllButtonFactory;
import com.nemonotfound.nemos.inventory.sorting.factory.MoveSameButtonFactory;
import com.nemonotfound.nemos.inventory.sorting.factory.SortButtonFactory;
import com.nemonotfound.nemos.inventory.sorting.gui.components.buttons.AbstractContainerButton;
import com.nemonotfound.nemos.inventory.sorting.helper.ButtonTypeMapping;
import com.nemonotfound.nemos.inventory.sorting.helper.FilterBoxGetter;
import com.nemonotfound.nemos.inventory.sorting.helper.SortingWidgetGetter;
import com.nemonotfound.nemos.inventory.sorting.models.LockedSlot;
import com.nemonotfound.nemos.inventory.sorting.models.Offset;
import com.nemonotfound.nemos.inventory.sorting.models.Position;
import com.nemonotfound.nemos.inventory.sorting.models.Size;
import com.nemonotfound.nemos.inventory.sorting.models.SlotRange;
import com.nemonotfound.nemos.inventory.sorting.models.config.ComponentConfig;
import com.nemonotfound.nemos.inventory.sorting.models.config.LockedSlotsConfig;
import com.nemonotfound.nemos.inventory.sorting.models.config.SettingsConfig;
import com.nemonotfound.nemos.inventory.sorting.service.HoveredSlotRangeService;
import com.nemonotfound.nemos.inventory.sorting.service.InventoryService;
import com.nemonotfound.nemos.inventory.sorting.service.ScrollTransferService;
import com.nemonotfound.nemos.inventory.sorting.service.config.ConfigService;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.BlastFurnaceMenu;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.CrafterMenu;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.SmokerMenu;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
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
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.nemonotfound.nemos.inventory.sorting.Constants.*;
import static com.nemonotfound.nemos.inventory.sorting.SortingCommonClient.MOD_LOADER_HELPER;
import static com.nemonotfound.nemos.inventory.sorting.config.DefaultConfigValues.*;
import static com.nemonotfound.nemos.inventory.sorting.enums.config.ConfigId.*;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin extends Screen implements SortingWidgetGetter {

    @Unique
    private static final Identifier LOCKED_SLOT = Identifier.fromNamespaceAndPath(MOD_ID, "container/locked_slot");

    @Shadow
    protected int leftPos;
    @Shadow
    protected int topPos;
    @Shadow
    protected int inventoryLabelY;
    @Final
    @Shadow
    protected int imageWidth;

    @Shadow
    @Nullable
    protected Slot hoveredSlot;

    @Unique
    private final Set<Slot> nemosInventorySorting$previousHoveredSlots = new HashSet<>();
    @Unique
    private Slot nemosInventorySorting$previousHoveredSlot = null;

    @Unique
    private int nemosInventorySorting$inventoryEndIndex;
    @Unique
    private int nemosInventorySorting$containerSize;

    @Unique
    private final ConfigService nemosInventorySorting$configService = ConfigService.INSTANCE;
    @Unique
    private final List<AbstractWidget> nemosInventorySorting$widgets = new ArrayList<>();

    @Unique
    private boolean nemosInventorySorting$displayLockedSlots = false;
    @Unique
    private boolean nemosInventorySorting$displayTooltip = true;
    @Unique
    private boolean nemosInventorySorting$splitQuickMoveHandled = false;

    protected AbstractContainerScreenMixin(Component component) {
        super(component);
    }

    @Inject(method = "init", at = @At(value = "TAIL"))
    public void init(CallbackInfo ci) {
        nemosInventorySorting$setSlotIndexes();
        nemosInventorySorting$initButtons();
    }

    @Unique
    private void nemosInventorySorting$setSlotIndexes() {
        nemosInventorySorting$inventoryEndIndex = nemosInventorySorting$getMenu().slots.size() - 9;
        nemosInventorySorting$containerSize = nemosInventorySorting$inventoryEndIndex - 27;
    }

    @Unique
    private void nemosInventorySorting$initButtons() {
        var configs = nemosInventorySorting$configService.readOrGetDefaultComponentConfigs();

        if (nemosInventorySorting$shouldHaveStorageContainerButtons()) {
            nemosInventorySorting$initStorageContainerButtons(configs);
        }

        if (nemosInventorySorting$shouldHaveContainerInventorySortingButtons()) {
            nemosInventorySorting$initContainerInventoryButtons(configs);
        }
    }

    @Override
    protected void clearWidgets() {
        nemosInventorySorting$widgets.clear();
        super.clearWidgets();
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    public void keyPressed(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
        if (nemosInventorySorting$handleKeyEventForHoveredContainer(event)) {
            cir.setReturnValue(true);
            return;
        }

        if (nemosInventorySorting$isSearchInactive() && nemosInventorySorting$handleWidgetInput(widget -> widget.keyPressed(event))) {
            cir.setReturnValue(true);
        }

        if (event.hasAltDown() && !((Screen) this instanceof CreativeModeInventoryScreen)) {
            nemosInventorySorting$displayLockedSlots = true;
        }
    }

    @Unique
    private boolean nemosInventorySorting$handleKeyEventForHoveredContainer(KeyEvent event) {
        return nemosInventorySorting$handleKeyEventForHoveredContainer(
                button -> button.matchesKeyMapping(event),
                button -> button.keyPressed(event),
                event.hasShiftDown()
        );
    }

    @Override
    public boolean keyReleased(@NotNull KeyEvent keyEvent) {
        if (nemosInventorySorting$isSearchInactive() && nemosInventorySorting$handleWidgetInput(widget -> widget.keyReleased(keyEvent))) {
            return true;
        }

        if (!keyEvent.hasAltDown()) {
            nemosInventorySorting$displayLockedSlots = false;
        }

        return super.keyReleased(keyEvent);
    }

    @Unique
    private boolean nemosInventorySorting$isSearchInactive() {
        return !Optional.ofNullable(((FilterBoxGetter) this).nemosInventorySorting$getFilterBox())
                .map(AbstractWidget::isFocused)
                .orElse(false);
    }

    @Unique
    private boolean nemosInventorySorting$handleKeyEventForHoveredContainer(
            Predicate<AbstractContainerButton> matches,
            Consumer<AbstractContainerButton> activate,
            boolean shiftDown
    ) {
        if (!nemosInventorySorting$isSearchInactive()) {
            return false;
        }

        var matchingButtons = nemosInventorySorting$getContainerButtons(matches);

        if (matchingButtons.isEmpty()) {
            return false;
        }

        nemosInventorySorting$getHoveredButton(matchingButtons, shiftDown).ifPresent(activate);

        return true;
    }

    @Unique
    private List<AbstractContainerButton> nemosInventorySorting$getContainerButtons(Predicate<AbstractContainerButton> matches) {
        return nemosInventorySorting$widgets.stream()
                .filter(AbstractContainerButton.class::isInstance)
                .map(AbstractContainerButton.class::cast)
                .filter(matches)
                .toList();
    }

    @Unique
    private Optional<AbstractContainerButton> nemosInventorySorting$getHoveredButton(
            List<AbstractContainerButton> matchingButtons,
            boolean shiftDown
    ) {
        if (!nemosInventorySorting$hasHoveredShortcutTarget()) {
            return Optional.empty();
        }

        var menu = nemosInventorySorting$getMenu();
        var storageContainer = nemosInventorySorting$shouldHaveStorageContainerButtons();

        return HoveredSlotRangeService.getInstance().getSlotRange(
                menu,
                hoveredSlot,
                storageContainer,
                SettingsConfig.INSTANCE.shouldIncludeHotbar(shiftDown)
        ).flatMap(slotRange -> matchingButtons.stream()
                        .filter(button -> button.isWithinSlotRange(slotRange))
                        .findFirst());
    }

    @Unique
    private boolean nemosInventorySorting$hasHoveredShortcutTarget() {
        if (hoveredSlot == null || (Screen) this instanceof CreativeModeInventoryScreen) {
            return false;
        }

        return nemosInventorySorting$getMenu() instanceof InventoryMenu
                || nemosInventorySorting$shouldHaveStorageContainerButtons()
                || nemosInventorySorting$shouldHaveContainerInventorySortingButtons();
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void mouseClicked(MouseButtonEvent event, boolean isDoubleClick, CallbackInfoReturnable<Boolean> cir) {
        if (nemosInventorySorting$handleMouseClick(event, isDoubleClick)) {
            cir.setReturnValue(true);
        }
    }

    @Unique
    private boolean nemosInventorySorting$handleMouseClick(MouseButtonEvent event, boolean isDoubleClick) {
        if (nemosInventorySorting$handleKeyEventForHoveredContainer(event, isDoubleClick)) {
            return true;
        }

        return nemosInventorySorting$handleWidgetInput(widget -> widget.mouseClicked(event, isDoubleClick))
                || nemosInventorySorting$handleSplitQuickMove(event)
                || nemosInventorySorting$handleSlotLocking(event);
    }

    @Unique
    private boolean nemosInventorySorting$handleKeyEventForHoveredContainer(MouseButtonEvent event, boolean isDoubleClick) {
        return nemosInventorySorting$handleKeyEventForHoveredContainer(
                button -> button.matchesKeyMapping(event),
                button -> button.mouseClicked(event, isDoubleClick),
                event.hasShiftDown()
        );
    }

    @Inject(method = "mouseDragged", at = @At("HEAD"), cancellable = true)
    private void mouseDragged(MouseButtonEvent event, double dx, double dy, CallbackInfoReturnable<Boolean> cir) {
        if (nemosInventorySorting$handleMouseDrag(event)) {
            cir.setReturnValue(true);
        }
    }

    @Unique
    private boolean nemosInventorySorting$handleMouseDrag(MouseButtonEvent event) {
        if (nemosInventorySorting$handleDraggingSlotLock(event)) {
            return true;
        }

        if (nemosInventorySorting$handleDragQuickMove(event)) {
            return false;
        }

        return nemosInventorySorting$handleDraggingSplitQuickMove(event);
    }

    @Inject(method = "mouseScrolled", at = @At("HEAD"), cancellable = true)
    private void mouseScrolled(double x, double y, double scrollX, double scrollY, CallbackInfoReturnable<Boolean> cir) {
        if (!SettingsConfig.INSTANCE.isScrollTransferEnabled() || hoveredSlot == null) {
            return;
        }

        var menu = nemosInventorySorting$getMenu();
        var shiftDown = minecraft.hasShiftDown();
        var scrollDelta = ScrollTransferService.resolveScrollDelta(scrollX, scrollY, shiftDown);

        if (InventoryService.getInstance().handleSingleItemScrollMove(menu, hoveredSlot.index, scrollDelta, shiftDown)) {
            cir.setReturnValue(true);
        }
    }

    @Unique
    private boolean nemosInventorySorting$shouldHandleSplitQuickMove(MouseButtonEvent event) {
        return SettingsConfig.INSTANCE.isSplitQuickMoveEnabled()
                && event.hasShiftDown()
                && event.button() == 1
                && hoveredSlot != null;
    }

    @Unique
    private boolean nemosInventorySorting$handleSplitQuickMove(MouseButtonEvent event) {
        if (!nemosInventorySorting$shouldHandleSplitQuickMove(event)) {
            return false;
        }

        InventoryService.getInstance().handleSplitQuickMove(nemosInventorySorting$getMenu(), hoveredSlot.index);
        nemosInventorySorting$previousHoveredSlots.add(hoveredSlot);
        nemosInventorySorting$previousHoveredSlot = hoveredSlot;
        nemosInventorySorting$splitQuickMoveHandled = true;
        return true;
    }

    @Unique
    private boolean nemosInventorySorting$handleSlotLocking(MouseButtonEvent event) {
        if (!event.hasAltDown()) {
            return false;
        }

        nemosInventorySorting$handleLockedSlot();
        return true;
    }

    @Unique
    private boolean nemosInventorySorting$handleDraggingSlotLock(MouseButtonEvent event) {
        if (!event.hasAltDown() || nemosInventorySorting$previousHoveredSlots.contains(hoveredSlot)) {
            return false;
        }

        nemosInventorySorting$handleLockedSlot();
        return true;
    }

    @Unique
    private boolean nemosInventorySorting$shouldHandleDragQuickMove(MouseButtonEvent event) {
        return SettingsConfig.INSTANCE.isDragQuickMoveEnabled()
                && event.hasShiftDown()
                && event.button() == 0
                && hoveredSlot != null
                && nemosInventorySorting$previousHoveredSlot != hoveredSlot;
    }

    @Unique
    private boolean nemosInventorySorting$handleDragQuickMove(MouseButtonEvent event) {
        if (!nemosInventorySorting$shouldHandleDragQuickMove(event)) {
            return false;
        }

        nemosInventorySorting$handleDraggingQuickMove(event.input(), hoveredSlot);
        return true;
    }

    @Unique
    private boolean nemosInventorySorting$shouldHandleDraggingSplitQuickMove(MouseButtonEvent event) {
        return nemosInventorySorting$shouldHandleSplitQuickMove(event)
                && nemosInventorySorting$previousHoveredSlot != hoveredSlot;
    }

    @Unique
    private boolean nemosInventorySorting$handleDraggingSplitQuickMove(MouseButtonEvent event) {
        if (!nemosInventorySorting$shouldHandleDraggingSplitQuickMove(event)) {
            return false;
        }

        nemosInventorySorting$handleDraggingSplitQuickMove(hoveredSlot);
        return true;
    }

    @Unique
    private void nemosInventorySorting$handleDraggingQuickMove(int mouseInput, Slot hoveredSlot) {
        var menu = nemosInventorySorting$getMenu();
        var player = minecraft.player;

        if (player == null || minecraft.gameMode == null) {
            return;
        }

        if (menu instanceof CreativeModeInventoryScreen.ItemPickerMenu) {
            menu.clicked(hoveredSlot.index, mouseInput, ContainerInput.QUICK_MOVE, player);
        } else {
            minecraft.gameMode.handleContainerInput(menu.containerId, hoveredSlot.index, mouseInput, ContainerInput.QUICK_MOVE, player);
        }

        nemosInventorySorting$previousHoveredSlot = hoveredSlot;
        nemosInventorySorting$displayTooltip = false;
    }

    @Unique
    private void nemosInventorySorting$handleDraggingSplitQuickMove(Slot hoveredSlot) {
        var menu = nemosInventorySorting$getMenu();

        InventoryService.getInstance().handleSplitQuickMove(menu, hoveredSlot.index);

        nemosInventorySorting$previousHoveredSlot = hoveredSlot;
        nemosInventorySorting$splitQuickMoveHandled = true;
        nemosInventorySorting$displayTooltip = false;
    }

    @Inject(method = "mouseReleased", at = @At("HEAD"), cancellable = true)
    private void mouseReleased(MouseButtonEvent event, CallbackInfoReturnable<Boolean> cir) {
        nemosInventorySorting$previousHoveredSlots.clear();
        nemosInventorySorting$previousHoveredSlot = null;
        nemosInventorySorting$displayTooltip = true;

        if (nemosInventorySorting$splitQuickMoveHandled && event.button() == 1) {
            nemosInventorySorting$splitQuickMoveHandled = false;
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "extractTooltip", at = @At("HEAD"), cancellable = true)
    private void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY, CallbackInfo ci) {
        if (!nemosInventorySorting$displayTooltip) {
            ci.cancel();
        }
    }

    @Unique
    private void nemosInventorySorting$handleLockedSlot() { //TODO: Put into LockedService
        if (hoveredSlot == null || !nemosInventorySorting$isLockableSlot(hoveredSlot.index)) {
            return;
        }

        var index = hoveredSlot.index - nemosInventorySorting$getInventoryStartIndex();
        var lockedSlot = new LockedSlot(index);

        if (!LockedSlotsConfig.INSTANCE.remove(lockedSlot)) {
            LockedSlotsConfig.INSTANCE.add(lockedSlot);
        }

        ConfigService.INSTANCE.writeConfig(true, LOCKED_SLOTS_CONFIG_PATH, LockedSlotsConfig.INSTANCE);
        nemosInventorySorting$previousHoveredSlots.add(hoveredSlot);
    }

    @Unique
    private boolean nemosInventorySorting$isLockableSlot(int index) { //TODO: Put into LockedService
        var menu = nemosInventorySorting$getMenu();
        var isInventoryMenu = menu instanceof InventoryMenu;
        var isLockableInventoryIndex = index >= InventoryMenu.INV_SLOT_START && index < InventoryMenu.USE_ROW_SLOT_END;
        var isLockableContainerInventoryIndex = index >= nemosInventorySorting$containerSize;

        return (isInventoryMenu && isLockableInventoryIndex) || (!isInventoryMenu && isLockableContainerInventoryIndex);
    }

    @Inject(method = "extractContents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;extractSlots(Lnet/minecraft/client/gui/GuiGraphicsExtractor;II)V"))
    void renderHighlightedSlot(GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (!nemosInventorySorting$displayLockedSlots) {
            return;
        }

        for (LockedSlot lockedSlot : LockedSlotsConfig.INSTANCE.getLockedSlots()) {
            var menu = nemosInventorySorting$getMenu();
            var slot = menu.getSlot(lockedSlot.index() + nemosInventorySorting$getInventoryStartIndex());

            guiGraphicsExtractor.blitSprite(RenderPipelines.GUI_TEXTURED, LOCKED_SLOT, slot.x, slot.y, 16, 16);
        }
    }

    @Unique
    private int nemosInventorySorting$getInventoryStartIndex() {
        return nemosInventorySorting$getMenu() instanceof InventoryMenu ?
                InventoryMenu.INV_SLOT_START : nemosInventorySorting$containerSize;
    }

    @Unique
    private boolean nemosInventorySorting$handleWidgetInput(Function<AbstractWidget, Boolean> action) {
        for (var widget : nemosInventorySorting$widgets) {
            if (action.apply(widget)) {
                return true;
            }
        }

        return false;
    }

    @Unique
    private boolean nemosInventorySorting$shouldHaveStorageContainerButtons() {
        var menu = nemosInventorySorting$getMenu();

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
    private boolean nemosInventorySorting$shouldHaveContainerInventorySortingButtons() {
        var menu = nemosInventorySorting$getMenu();

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
        nemosInventorySorting$createButtons(
                componentConfigs,
                new ButtonTypeMapping(SORT_STORAGE_CONTAINER, SortButtonFactory.getInstance(), Y_OFFSET_CONTAINER, false),
                new ButtonTypeMapping(MOVE_SAME_STORAGE_CONTAINER, MoveSameButtonFactory.getInstance(), Y_OFFSET_CONTAINER, false),
                new ButtonTypeMapping(MOVE_ALL_STORAGE_CONTAINER, MoveAllButtonFactory.getInstance(), Y_OFFSET_CONTAINER, false),
                new ButtonTypeMapping(DROP_ALL_STORAGE_CONTAINER, DropAllButtonFactory.getInstance(), Y_OFFSET_CONTAINER, false)
        );

        nemosInventorySorting$initStorageContainerInventoryButtons(componentConfigs);
    }

    @Unique
    private void nemosInventorySorting$initStorageContainerInventoryButtons(List<ComponentConfig> componentConfigs) {
        var yOffset = inventoryLabelY - 2;

        nemosInventorySorting$createButtons(
                componentConfigs,
                new ButtonTypeMapping(SORT_STORAGE_CONTAINER_INVENTORY, SortButtonFactory.getInstance(), yOffset, true),
                new ButtonTypeMapping(MOVE_SAME_STORAGE_CONTAINER_INVENTORY, MoveSameButtonFactory.getInstance(), yOffset, true),
                new ButtonTypeMapping(MOVE_ALL_STORAGE_CONTAINER_INVENTORY, MoveAllButtonFactory.getInstance(), yOffset, true),
                new ButtonTypeMapping(DROP_ALL_STORAGE_CONTAINER_INVENTORY, DropAllButtonFactory.getInstance(), yOffset, true)
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
            nemosInventorySorting$configService.getOrDefault(configs, mapping.configId())
                    .filter(ComponentConfig::isEnabled)
                    .ifPresent(config -> nemosInventorySorting$createButton(mapping, config));
        }
    }

    @Unique
    private void nemosInventorySorting$createButton(ButtonTypeMapping mapping, ComponentConfig config) {
        var yOffset = config.yOffset() != null ? config.yOffset() : mapping.defaultYOffset();
        var xOffset = config.xOffset() != null ? config.xOffset() : imageWidth + config.rightXOffset();
        var offset = new Offset(xOffset, yOffset);
        var size = new Size(config.width(), config.height(), BUTTON_SIZE);

        nemosInventorySorting$createButton(mapping.factory(), mapping.isInventoryButton(), offset, size);
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
        var button = buttonCreator.createButton(slotRange, position, offset, size, nemosInventorySorting$getMenu());
        nemosInventorySorting$addSortingWidget(button);
    }

    @Unique
    private AbstractContainerMenu nemosInventorySorting$getMenu() {
        return ((AbstractContainerScreen<?>) (Object) this).getMenu();
    }

    @Override
    public void nemosInventorySorting$addSortingWidget(AbstractWidget sortingWidget) {
        nemosInventorySorting$widgets.add(sortingWidget);
        this.addRenderableWidget(sortingWidget);
    }
}
