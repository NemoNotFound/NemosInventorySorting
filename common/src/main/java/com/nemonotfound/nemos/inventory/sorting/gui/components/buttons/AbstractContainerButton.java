package com.nemonotfound.nemos.inventory.sorting.gui.components.buttons;

import com.nemonotfound.nemos.inventory.sorting.model.Position;
import com.nemonotfound.nemos.inventory.sorting.model.Size;
import com.nemonotfound.nemos.inventory.sorting.model.SlotRange;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

public abstract class AbstractContainerButton extends AbstractButton {

    protected final AbstractContainerMenu menu;
    protected final Integer startIndex;
    protected final Integer endIndex;
    private final Component buttonName;
    private final Component shiftButtonName;
    protected final boolean isInventoryButton;

    protected int currentEndIndex;

    public AbstractContainerButton(Position position, int xOffset, Size size, SlotRange slotRange, Component buttonName, AbstractContainerMenu menu) {
        super(position, xOffset, size, buttonName);
        this.setTooltip(Tooltip.create(buttonName));
        this.buttonName = buttonName;
        this.shiftButtonName = buttonName.copy()
                .append(" ")
                .append(Component.translatable("nemos_inventory_sorting.gui.includeHotbar"));
        this.menu = menu;
        this.startIndex = slotRange.startIndex();
        this.endIndex = slotRange.endIndex();
        this.isInventoryButton = startIndex != 0;
        this.currentEndIndex = endIndex;
    }

    @Override
    public boolean keyPressed(@NotNull KeyEvent keyEvent) {
        handleShiftKeyEvent(keyEvent);

        return super.keyPressed(keyEvent);
    }

    @Override
    public boolean keyReleased(@NotNull KeyEvent keyEvent) {
        handleShiftKeyEvent(keyEvent);

        return super.keyReleased(keyEvent);
    }

    private void handleShiftKeyEvent(KeyEvent keyEvent) {
        if (keyEvent.key() == GLFW.GLFW_KEY_LEFT_SHIFT || keyEvent.key() == GLFW.GLFW_KEY_RIGHT_SHIFT) {
            setTooltip();
            setEndIndex();
        }
    }

    @Override
    protected ResourceLocation getTexture() {
        return this.isHovered() ? getButtonHoverTexture() : getButtonTexture();
    }

    protected abstract ResourceLocation getButtonHoverTexture();

    protected abstract ResourceLocation getButtonTexture();

    private void setTooltip() {
        if (shouldIncludeHotbar()) {
            setTooltip(Tooltip.create(shiftButtonName));
        } else {
            setTooltip(Tooltip.create(buttonName));
        }
    }

    private void setEndIndex() {
        if (shouldIncludeHotbar()) {
            currentEndIndex = endIndex + 9;
        } else {
            currentEndIndex = endIndex;
        }
    }

    private boolean shouldIncludeHotbar() {
        return Minecraft.getInstance().hasShiftDown() && isInventoryButton;
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {

    }
}
