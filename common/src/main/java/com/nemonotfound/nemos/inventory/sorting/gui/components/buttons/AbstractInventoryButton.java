package com.nemonotfound.nemos.inventory.sorting.gui.components.buttons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

public abstract class AbstractInventoryButton extends AbstractButton {

    protected final AbstractContainerMenu menu;
    protected final Integer startIndex;
    protected final Integer endIndex;
    private final Component buttonName;
    private final Component shiftButtonName;
    protected final boolean isInventoryButton;

    protected int currentEndIndex;

    public AbstractInventoryButton(Builder<? extends AbstractInventoryButton> builder) {
        super(builder.x, builder.y, builder.xOffset, builder.width, builder.height, builder.buttonName);
        this.setTooltip(Tooltip.create(builder.buttonName));
        this.buttonName = builder.buttonName;
        this.shiftButtonName = builder.shiftButtonName;
        this.menu = builder.menu;
        this.startIndex = builder.startIndex;
        this.endIndex = builder.endIndex;
        this.isInventoryButton = builder.isInventoryButton;
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

    public static class Builder<T extends AbstractInventoryButton> {
        private Integer startIndex;
        private Integer endIndex;
        private Integer x;
        private Integer y;
        private Integer xOffset;
        private Integer width;
        private Integer height;
        private Component buttonName;
        private Component shiftButtonName;
        private AbstractContainerMenu menu;
        private boolean isInventoryButton = false;
        private final Class<T> clazz;

        public Builder(Class<T> clazz) {
            this.clazz = clazz;
        }

        public Builder<T> startIndex(int startIndex) {
            this.startIndex = startIndex;
            return this;
        }

        public Builder<T> endIndex(int endIndex) {
            this.endIndex = endIndex;
            return this;
        }

        public Builder<T> x(int x) {
            this.x = x;
            return this;
        }

        public Builder<T> y(int y) {
            this.y = y;
            return this;
        }

        public Builder<T> xOffset(int xOffset) {
            this.xOffset = xOffset;
            return this;
        }

        public Builder<T> width(int width) {
            this.width = width;
            return this;
        }

        public Builder<T> height(int height) {
            this.height = height;
            return this;
        }

        public Builder<T> buttonName(Component buttonName) {
            this.buttonName = buttonName;
            return this;
        }

        public Builder<T> shiftButtonName(Component shiftButtonName) {
            this.shiftButtonName = shiftButtonName;
            return this;
        }

        public Builder<T> menu(AbstractContainerMenu menu) {
            this.menu = menu;
            return this;
        }

        public Builder<T> isInventoryButton(boolean isInventoryButton) {
            this.isInventoryButton = isInventoryButton;
            return this;
        }

        public T build() {
            checkRequiredFields();

            try {
                return clazz.getDeclaredConstructor(Builder.class).newInstance(this);
            } catch (Exception e) {
                throw new RuntimeException("Failed to create instance of " + clazz.getName(), e);
            }
        }

        private void checkRequiredFields() {
            if (startIndex == null || endIndex == null || x == null || y == null || xOffset == null || width == null
                    || height == null || buttonName == null || menu == null) {
                throw new IllegalArgumentException("Not all fields were set!");
            }
        }
    }
}
