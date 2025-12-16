package com.nemonotfound.nemos.inventory.sorting.mixin;

import com.nemonotfound.nemos.inventory.sorting.config.model.ComponentConfig;
import com.nemonotfound.nemos.inventory.sorting.config.model.FilterConfig;
import com.nemonotfound.nemos.inventory.sorting.config.service.ConfigService;
import com.nemonotfound.nemos.inventory.sorting.gui.components.FilterBox;
import com.nemonotfound.nemos.inventory.sorting.gui.components.buttons.ToggleFilterPersistenceButton;
import com.nemonotfound.nemos.inventory.sorting.model.FilterResult;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static com.nemonotfound.nemos.inventory.sorting.Constants.MOD_ID;
import static com.nemonotfound.nemos.inventory.sorting.client.InventorySortingKeyMappings.QUICK_SEARCH;
import static com.nemonotfound.nemos.inventory.sorting.config.DefaultConfigValues.*;

@Mixin(AbstractContainerScreen.class)
public abstract class ContainerFilterMixin extends Screen {

    @Unique
    private static final Identifier HIGHLIGHTED_SLOT = Identifier.fromNamespaceAndPath(MOD_ID, "container/highlighted_slot");
    @Unique
    private static final Identifier HIGHLIGHTED_SLOT_INCLUDED_ITEM = Identifier.fromNamespaceAndPath(MOD_ID, "container/highlighted_slot_included_item");
    @Unique
    private static final Identifier DIMMED_SLOT = Identifier.fromNamespaceAndPath(MOD_ID, "container/dimmed_slot");

    @Shadow protected int leftPos;
    @Shadow protected int topPos;
    @Unique
    private FilterBox nemosInventorySorting$filterBox;
    @Unique
    private FilterConfig nemosInventorySorting$filterConfig;
    @Unique
    private int nemosInventorySorting$filterBoxWidth = 0;

    @Unique
    private final ConfigService nemosInventorySorting$configService = ConfigService.getInstance();

    protected ContainerFilterMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At(value = "TAIL"))
    public void init(CallbackInfo ci) {
        var componentConfigs = nemosInventorySorting$configService.readOrGetDefaultComponentConfigs();

        if (nemosInventorySorting$shouldHaveFilter()) {
            nemosInventorySorting$filterConfig = nemosInventorySorting$configService.readOrGetDefaultFilterConfig();

            nemosInventorySorting$initFilter(componentConfigs);
        }
    }

    @Inject(method = "onClose", at = @At("TAIL"))
    private void onClose(CallbackInfo ci) {
        if (nemosInventorySorting$filterBox == null) {
            return;
        }

        nemosInventorySorting$filterBox.updateAndSaveFilter(nemosInventorySorting$filterConfig);
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    public void keyPressed(KeyEvent keyEvent, CallbackInfoReturnable<Boolean> cir) {
        if (this.nemosInventorySorting$filterBox != null) {
            if (this.nemosInventorySorting$filterBox.isFocused() && keyEvent.key() != GLFW.GLFW_KEY_ESCAPE) {
                cir.setReturnValue(this.nemosInventorySorting$filterBox.keyPressed(keyEvent));
                return;
            }

            if (!this.nemosInventorySorting$filterBox.isFocused() && keyEvent.hasControlDown() && QUICK_SEARCH.get().matches(keyEvent)) {
                nemosInventorySorting$handleQuickSearch(cir);

                return;
            }
        }

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
    private void mouseClicked(MouseButtonEvent mouseButtonEvent, boolean isDoubleClick, CallbackInfoReturnable<Boolean> cir) {
        Optional<GuiEventListener> optional = this.getChildAt(mouseButtonEvent.x(), mouseButtonEvent.y());

        if (optional.isEmpty()) {
            for (GuiEventListener guiEventListener : this.children()) {
                guiEventListener.setFocused(false);
            }
        }

        if (this.nemosInventorySorting$filterBox != null && !this.nemosInventorySorting$filterBox.isFocused() && mouseButtonEvent.hasControlDown() && QUICK_SEARCH.get().matchesMouse(mouseButtonEvent)) {
            nemosInventorySorting$handleQuickSearch(cir);

            return;
        }

        if (nemosInventorySorting$triggerActionOnWidget(widget -> widget.mouseClicked(mouseButtonEvent, isDoubleClick))) {
            cir.setReturnValue(true);
        }

        if (nemosInventorySorting$filterBox != null && !nemosInventorySorting$filterBox.mouseClicked(mouseButtonEvent, isDoubleClick) && this.getFocused() == nemosInventorySorting$filterBox) {
            this.setFocused(null);
        }
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
        this.nemosInventorySorting$filterBox.onClick(new MouseButtonEvent(filterBoxX, filterBoxY, new MouseButtonInfo(0, 0)), false);
        cir.setReturnValue(true);
    }

    @Unique
    private boolean nemosInventorySorting$triggerActionOnWidget(Function<GuiEventListener, Boolean> function) {
        for (var widget : this.children()) {
            if (widget instanceof ToggleFilterPersistenceButton && function.apply(widget)) {
                return true;
            }
        }

        return false;
    }

    @Inject(method = "renderContents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;renderSlots(Lnet/minecraft/client/gui/GuiGraphics;II)V"))
    void renderHighlightedSlot(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (!nemosInventorySorting$shouldHaveFilter() || this.nemosInventorySorting$filterBox == null) {
            return;
        }

        var filter = this.nemosInventorySorting$filterBox.getValue();

        if (!filter.isEmpty()) {
            var filteredSlotMap = this.nemosInventorySorting$filterBox.filterSlots(nemosInventorySorting$getThis().getMenu().slots, filter);

            nemosInventorySorting$markSlots(filteredSlotMap.get(FilterResult.INCLUDED), guiGraphics, HIGHLIGHTED_SLOT);
            nemosInventorySorting$markSlots(filteredSlotMap.get(FilterResult.HAS_INCLUDED_ITEM), guiGraphics, HIGHLIGHTED_SLOT_INCLUDED_ITEM);
        }
    }

    @Inject(method = "renderContents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;renderSlotHighlightFront(Lnet/minecraft/client/gui/GuiGraphics;)V", shift = At.Shift.AFTER))
    void renderDimmedSlot(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (!nemosInventorySorting$shouldHaveFilter() || this.nemosInventorySorting$filterBox == null) {
            return;
        }

        var filter = this.nemosInventorySorting$filterBox.getValue();

        if (!filter.isEmpty()) {
            var filteredSlotMap = this.nemosInventorySorting$filterBox.filterSlots(nemosInventorySorting$getThis().getMenu().slots, filter);

            nemosInventorySorting$markSlots(filteredSlotMap.get(FilterResult.EXCLUDED), guiGraphics, DIMMED_SLOT);
        }
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
        nemosInventorySorting$createButton(configs, FILTER_PERSISTENCE_TOGGLE);
    }

    @Unique
    private void nemosInventorySorting$createSearchBox(int xOffset, int yOffset, int width, int height, String filter) {
        nemosInventorySorting$filterBox = new FilterBox(
                font,
                leftPos,
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

    @Unique
    private void nemosInventorySorting$createButton(List<ComponentConfig> configs, String componentName) {
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
        var buttonName = Component.translatable("nemos_inventory_sorting.gui.toggleFilterPersistence");
        var button = new ToggleFilterPersistenceButton(
                leftPos + xOffset,
                topPos + yOffset,
                xOffset,
                width,
                config.height(),
                buttonName,
                nemosInventorySorting$filterConfig
        );

        this.addRenderableWidget(button);
    }

    @Unique
    private void nemosInventorySorting$markSlots(
            List<Slot> slots,
            GuiGraphics guiGraphics,
            Identifier texture
    ) {
        if (slots == null) {
            return;
        }

        for (Slot slot : slots) {
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, texture, slot.x, slot.y, 16, 16);
        }
    }

    @Unique
    private boolean nemosInventorySorting$shouldHaveFilter() {
        return !(nemosInventorySorting$getThis().getMenu() instanceof CreativeModeInventoryScreen.ItemPickerMenu);
    }

    @Unique
    private AbstractContainerScreen<?> nemosInventorySorting$getThis() {
        return (AbstractContainerScreen<?>) (Object) this;
    }
}
