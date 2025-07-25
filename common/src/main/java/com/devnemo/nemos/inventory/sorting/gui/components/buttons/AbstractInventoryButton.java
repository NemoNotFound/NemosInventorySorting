package com.devnemo.nemos.inventory.sorting.gui.components.buttons;

import com.devnemo.nemos.inventory.sorting.gui.components.RecipeBookUpdatable;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public abstract class AbstractInventoryButton extends AbstractWidget implements RecipeBookUpdatable {

    protected final AbstractContainerMenu menu;
    protected final Integer startIndex;
    protected final Integer endIndex;
    private final int xOffset;
    private final Component buttonName;
    private final Component shiftButtonName;
    protected final boolean isInventoryButton;

    protected boolean isShiftKeyDown = false;

    public AbstractInventoryButton(Builder<? extends AbstractInventoryButton> builder) {
        super(builder.x, builder.y, builder.width, builder.height, builder.buttonName);
        this.setTooltip(Tooltip.create(builder.buttonName));
        this.buttonName = builder.buttonName;
        this.shiftButtonName = builder.shiftButtonName;
        this.menu = builder.menu;
        this.startIndex = builder.startIndex;
        this.endIndex = builder.endIndex;
        this.xOffset = builder.xOffset;
        this.isInventoryButton = builder.isInventoryButton;
    }

    @Override
    public abstract void onClick(double mouseX, double mouseY);

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 340) {
            setIsShiftKeyDown(true);
            setTooltip();
        }

        var minecraft = Minecraft.getInstance();
        var isKeyPressed = Arrays.stream(minecraft.options.keyMappings)
                .filter(keyMapping -> keyMapping.same(getKeyMapping()))
                .anyMatch(keyMapping -> keyMapping.matches(keyCode, scanCode));

        if (!isKeyPressed) {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        playDownSound(minecraft.getSoundManager());
        onClick(0, 0);

        return true;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 340) {
            setIsShiftKeyDown(false);
            setTooltip();
        }

        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        var minecraft = Minecraft.getInstance();
        var isKeyPressed = Arrays.stream(minecraft.options.keyMappings)
                .filter(keyMapping -> keyMapping.same(getKeyMapping()))
                .anyMatch(keyMapping -> keyMapping.matchesMouse(button));

        if (!isKeyPressed) {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        playDownSound(minecraft.getSoundManager());
        onClick(0, 0);

        return true;
    }

    protected abstract KeyMapping getKeyMapping();

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.isHovered()) {
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, getButtonHoverTexture(), this.getX(), this.getY(), this.getWidth(), this.getHeight());
        } else {
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, getButtonTexture(), this.getX(), this.getY(), this.getWidth(), this.getHeight());
        }
    }

    @Override
    public void updateXPosition(int leftPos) {
        this.setX(leftPos + this.xOffset);
    }

    protected abstract ResourceLocation getButtonHoverTexture();
    protected abstract ResourceLocation getButtonTexture();

    public void setIsShiftKeyDown(boolean shiftKeyDown) {
        isShiftKeyDown = shiftKeyDown;
    }

    public void setTooltip() {
        if (isButtonShiftable()) {
            setTooltip(Tooltip.create(shiftButtonName));
        } else {
            setTooltip(Tooltip.create(buttonName));
        }
    }

    //TODO: Use service instead
    protected int calculateEndIndex() {
        if (isButtonShiftable()) {
            return endIndex + 9;
        }

        return endIndex;
    }

    protected boolean isButtonShiftable() {
        return isShiftKeyDown && isInventoryButton;
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
