package com.nemonotfound.nemos.inventory.sorting.mixin;

import com.nemonotfound.nemos.inventory.sorting.client.gui.components.AbstractSortButton;
import com.nemonotfound.nemos.inventory.sorting.factory.*;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ContainerScreen.class)
public abstract class ContainerScreenMixin extends AbstractContainerScreen<ChestMenu> {

    @Shadow @Final private int containerRows;

    public ContainerScreenMixin(ChestMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    public void init() {
        super.init();

        int xOffsetFirstButton = 22;
        int xOffsetSecondButton = 40;
        int xOffsetThirdButton = 58;
        int xOffsetFourthButton = 76;
        int xOffsetFifthButton = 94;
        int yOffsetInventory = 72;
        int yOffsetContainer = 5;
        int size = 11;
        int containerSize = this.getMenu().getContainer().getContainerSize();
        int inventoryEndIndex = containerSize + 27;
        int inventoryWithHotbarEndIndex = inventoryEndIndex + 9;

        if (containerRows == 6) {
            yOffsetInventory = 126;
        }

        SortAlphabeticallyButtonFactory sortAlphabeticallyButtonFactory = SortAlphabeticallyButtonFactory.getInstance();
        SortAlphabeticallyDescendingButtonFactory sortAlphabeticallyDescendingButtonFactory = SortAlphabeticallyDescendingButtonFactory.getInstance();
        DropAllButtonFactory dropAllButtonFactory = DropAllButtonFactory.getInstance();
        MoveSameButtonFactory moveSameButtonFactory = MoveSameButtonFactory.getInstance();
        MoveAllButtonFactory moveAllButtonFactory = MoveAllButtonFactory.getInstance();

        AbstractSortButton sortAlphabeticallyButton = sortAlphabeticallyButtonFactory.createButton(0, containerSize, leftPos, topPos, xOffsetSecondButton, yOffsetContainer, imageWidth, size, size, this);
        AbstractSortButton sortAlphabeticallyDescendingButton = sortAlphabeticallyDescendingButtonFactory.createButton(0, containerSize, leftPos, topPos, xOffsetFirstButton, yOffsetContainer, imageWidth, size, size, this);
        AbstractSortButton moveSameButton = moveSameButtonFactory.createButton(0, containerSize, leftPos, topPos, xOffsetThirdButton, yOffsetContainer, imageWidth, size, size, this);
        AbstractSortButton moveAllButton = moveAllButtonFactory.createButton(0, containerSize, leftPos, topPos, xOffsetFourthButton, yOffsetContainer, imageWidth, size, size, this);
        AbstractSortButton dropAllButton = dropAllButtonFactory.createButton(0, containerSize, leftPos, topPos, xOffsetFifthButton, yOffsetContainer, imageWidth, size, size, this);
        AbstractSortButton inventorySortAlphabeticallyInButton = sortAlphabeticallyButtonFactory.createButton(containerSize, inventoryEndIndex, leftPos, topPos, xOffsetSecondButton, yOffsetInventory, imageWidth, size, size, this);
        AbstractSortButton inventorySortAlphabeticallyDescendingInButton = sortAlphabeticallyDescendingButtonFactory.createButton(containerSize, inventoryEndIndex, leftPos, topPos, xOffsetFirstButton, yOffsetInventory, imageWidth, size, size, this);
        AbstractSortButton inventoryMoveSameButton = moveSameButtonFactory.createButton(containerSize, inventoryWithHotbarEndIndex, leftPos, topPos, xOffsetThirdButton, yOffsetInventory, imageWidth, size, size, this);
        AbstractSortButton inventoryMoveAllButton = moveAllButtonFactory.createButton(containerSize, inventoryEndIndex, leftPos, topPos, xOffsetFourthButton, yOffsetInventory, imageWidth, size, size, this);
        AbstractSortButton inventoryDropAllButton = dropAllButtonFactory.createButton(containerSize, inventoryEndIndex, leftPos, topPos, xOffsetFifthButton, yOffsetInventory, imageWidth, size, size, this);

        this.addRenderableWidget(sortAlphabeticallyButton);
        this.addRenderableWidget(sortAlphabeticallyDescendingButton);
        this.addRenderableWidget(moveSameButton);
        this.addRenderableWidget(moveAllButton);
        this.addRenderableWidget(dropAllButton);
        this.addRenderableWidget(inventorySortAlphabeticallyInButton);
        this.addRenderableWidget(inventorySortAlphabeticallyDescendingInButton);
        this.addRenderableWidget(inventoryMoveSameButton);
        this.addRenderableWidget(inventoryMoveAllButton);
        this.addRenderableWidget(inventoryDropAllButton);
    }
}