package com.nemonotfound.nemos.inventory.sorting.mixin;

import com.nemonotfound.nemos.inventory.sorting.config.model.ComponentConfig;
import com.nemonotfound.nemos.inventory.sorting.config.service.ConfigService;
import com.nemonotfound.nemos.inventory.sorting.factory.*;
import com.nemonotfound.nemos.inventory.sorting.model.Offset;
import com.nemonotfound.nemos.inventory.sorting.model.Position;
import com.nemonotfound.nemos.inventory.sorting.model.Size;
import com.nemonotfound.nemos.inventory.sorting.model.SlotRange;
import com.progwml6.ironchest.client.screen.IronChestScreen;
import com.progwml6.ironchest.common.block.IronChestsTypes;
import com.progwml6.ironchest.common.inventory.IronChestMenu;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;

import static com.nemonotfound.nemos.inventory.sorting.config.DefaultConfigValues.*;

//TODO: Refactor
@Mixin(IronChestScreen.class)
public abstract class NeoForgeIronChestScreenMixin extends AbstractContainerScreen<IronChestMenu> {

    @Unique
    private int nemosInventorySortingNeoForge$inventoryEndIndex;
    @Unique
    private int nemosInventorySortingNeoForge$containerSize;
    @Unique
    private int nemosInventorySorting$latestInventoryXOffset = 0;
    @Unique
    private int nemosInventorySorting$latestStorageContainerXOffset = 0;

    @Unique
    private final ConfigService nemosInventorySortingNeoForge$configService = ConfigService.getInstance();
    @Unique
    private final List<AbstractWidget> nemosInventorySorting$widgets = new ArrayList<>();

    public NeoForgeIronChestScreenMixin(IronChestMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    public void init() {
        super.init();

        nemosInventorySortingNeoForge$inventoryEndIndex = getMenu().slots.size() - 9;
        nemosInventorySortingNeoForge$containerSize = nemosInventorySortingNeoForge$inventoryEndIndex - 27;
        nemosInventorySorting$latestStorageContainerXOffset = imageWidth - 8;
        nemosInventorySorting$latestInventoryXOffset = imageWidth - 8;

        if (nemosInventorySorting$shouldHaveStorageContainerButtons()) {
            nemosInventorySorting$initStorageContainerButtons();
        }

        for (AbstractWidget widget : nemosInventorySorting$widgets) {
            this.addRenderableWidget(widget);
        }
    }

    @Override
    public boolean keyPressed(@NotNull KeyEvent keyEvent) {
        for (AbstractWidget widget : nemosInventorySorting$widgets) {
            if (widget.keyPressed(keyEvent)) {
                return true;
            }
        }

        return super.keyPressed(keyEvent);
    }

    @Override
    public boolean keyReleased(@NotNull KeyEvent keyEvent) {
        for (AbstractWidget widget : nemosInventorySorting$widgets) {
            if (widget.keyReleased(keyEvent)) {
                return true;
            }
        }

        return super.keyReleased(keyEvent);
    }

    @Override
    public boolean mouseClicked(@NotNull MouseButtonEvent mouseButtonEvent, boolean bl) {
        for (AbstractWidget widget : nemosInventorySorting$widgets) {
            if (widget.mouseClicked(mouseButtonEvent, bl)) {
                return true;
            }
        }

        return super.mouseClicked(mouseButtonEvent, bl);
    }

    @Override
    protected void clearWidgets() {
        nemosInventorySorting$widgets.clear();
        super.clearWidgets();
    }

    @Unique
    private boolean nemosInventorySorting$shouldHaveStorageContainerButtons() {
        var menu = getMenu();

        return menu instanceof IronChestMenu ironChestMenu && ironChestMenu.getChestType() != IronChestsTypes.DIRT;
    }

    @Unique
    private void nemosInventorySorting$initStorageContainerButtons() {
        var sortButtonFactory = SortButtonFactory.getInstance();
        var dropAllButtonFactory = DropAllButtonFactory.getInstance();
        var moveSameButtonFactory = MoveSameButtonFactory.getInstance();
        var moveAllButtonFactory = MoveAllButtonFactory.getInstance();
        var configs = nemosInventorySortingNeoForge$configService.readOrGetDefaultIronChestComponentConfigs();
        var yOffset = imageHeight - 96;

        nemosInventorySorting$createButtonForContainer(configs, DROP_ALL_STORAGE_CONTAINER, dropAllButtonFactory, Y_OFFSET_CONTAINER);
        nemosInventorySorting$createButtonForContainer(configs, MOVE_ALL_STORAGE_CONTAINER, moveAllButtonFactory, Y_OFFSET_CONTAINER);
        nemosInventorySorting$createButtonForContainer(configs, MOVE_SAME_STORAGE_CONTAINER, moveSameButtonFactory, Y_OFFSET_CONTAINER);
        nemosInventorySorting$createButtonForContainer(configs, SORT_STORAGE_CONTAINER, sortButtonFactory, Y_OFFSET_CONTAINER);

        nemosInventorySorting$createButtonForInventory(configs, DROP_ALL_STORAGE_CONTAINER_INVENTORY, dropAllButtonFactory, yOffset);
        nemosInventorySorting$createButtonForInventory(configs, MOVE_ALL_STORAGE_CONTAINER_INVENTORY, moveAllButtonFactory, yOffset);
        nemosInventorySorting$createButtonForInventory(configs, MOVE_SAME_STORAGE_CONTAINER_INVENTORY, moveSameButtonFactory, yOffset);
        nemosInventorySorting$createButtonForInventory(configs, SORT_STORAGE_CONTAINER_INVENTORY, sortButtonFactory, yOffset);
    }

    @Unique
    private void nemosInventorySorting$createButtonForContainer(List<ComponentConfig> configs, String componentName, ButtonCreator<?> buttonCreator, int defaultYOffset) {
        var optionalComponentConfig = nemosInventorySortingNeoForge$configService.getOrDefaultComponentConfig(configs, componentName);

        if (optionalComponentConfig.isEmpty()) {
            return;
        }

        var config = optionalComponentConfig.get();

        if (!config.isEnabled()) {
            return;
        }

        var width = config.width();
        var xOffset = config.xOffset() != null ? config.xOffset() : nemosInventorySorting$latestStorageContainerXOffset - width - 7;
        var yOffset = config.yOffset() != null ? config.yOffset() : defaultYOffset;

        nemosInventorySorting$latestStorageContainerXOffset = xOffset;
        nemosInventorySorting$createContainerButton(buttonCreator, new Offset(xOffset, yOffset), new Size(width, config.height(), BUTTON_SIZE));
    }

    @Unique
    private void nemosInventorySorting$createButtonForInventory(List<ComponentConfig> configs, String componentName, ButtonCreator<?> buttonCreator, int defaultYOffset) {
        var optionalComponentConfig = nemosInventorySortingNeoForge$configService.getOrDefaultComponentConfig(configs, componentName);

        if (optionalComponentConfig.isEmpty()) {
            return;
        }

        var config = optionalComponentConfig.get();

        if (!config.isEnabled()) {
            return;
        }

        var width = config.width();
        var xOffset = config.xOffset() != null ? config.xOffset() : nemosInventorySorting$latestInventoryXOffset - width - 7;
        var yOffset = config.yOffset() != null ? config.yOffset() : defaultYOffset;

        nemosInventorySorting$latestInventoryXOffset = xOffset;
        nemosInventorySorting$createInventoryButton(buttonCreator, new Offset(xOffset, yOffset), new Size(width, config.height(), BUTTON_SIZE));
    }

    @Unique
    private void nemosInventorySorting$createContainerButton(ButtonCreator<?> buttonCreator, Offset offset, Size size) {
        nemosInventorySorting$createButton(buttonCreator, new SlotRange(0, nemosInventorySortingNeoForge$containerSize), offset, size);
    }

    @Unique
    private void nemosInventorySorting$createInventoryButton(ButtonCreator<?> buttonCreator, Offset offset, Size size) {
        nemosInventorySorting$createButton(buttonCreator, new SlotRange(nemosInventorySortingNeoForge$containerSize, nemosInventorySortingNeoForge$inventoryEndIndex), offset, size);
    }

    @Unique
    private void nemosInventorySorting$createButton(ButtonCreator<?> buttonCreator, SlotRange slotRange, Offset offset, Size size) {
        var sortButton = buttonCreator.createButton(slotRange, new Position(leftPos, topPos), offset, size, getMenu());
        nemosInventorySorting$widgets.add(sortButton);
    }
}
