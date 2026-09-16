package com.nemonotfound.nemos.inventory.sorting.gui.components.buttons;

import com.nemonotfound.nemos.inventory.sorting.config.model.FilterConfig;
import com.nemonotfound.nemos.inventory.sorting.gui.components.RecipeBookUpdatable;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public abstract class AbstractFilterToggleButton extends AbstractWidget implements RecipeBookUpdatable {

    private final int xOffset;
    protected final FilterConfig filterConfig;

    public AbstractFilterToggleButton(Builder<? extends AbstractFilterToggleButton> builder) {
        super(builder.x, builder.y, builder.width, builder.height, builder.buttonName);
        this.xOffset = builder.xOffset;
        this.filterConfig = builder.filterConfig;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        var texture = filterConfig.isFilterPersistent()
                ? this.isHovered() ? getToggleOnHoverTexture() : getToggleOnTexture()
                : this.isHovered() ? getToggleOffHoverTexture() : getToggleOffTexture();

        this.renderTexture(guiGraphics, texture, this.getX(), this.getY(), 0, 0,0, this.getWidth(), this.getHeight(), this.getWidth(), this.getHeight());
    }

    @Override
    public void updateXPosition(int leftPos) {
        this.setX(leftPos + this.xOffset);
    }

    protected abstract ResourceLocation getToggleOffTexture();
    protected abstract ResourceLocation getToggleOnTexture();
    protected abstract ResourceLocation getToggleOffHoverTexture();
    protected abstract ResourceLocation getToggleOnHoverTexture();

    protected abstract void setTooltip();

    @Override
    public abstract void onClick(double mouseX, double mouseY);

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
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
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {

    }

    public static class Builder<T extends AbstractFilterToggleButton> {
        private Integer x;
        private Integer y;
        private Integer xOffset;
        private Integer width;
        private Integer height;
        private Component buttonName;
        private FilterConfig filterConfig;
        private final Class<T> clazz;

        public Builder(Class<T> clazz) {
            this.clazz = clazz;
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

        public Builder<T> filterConfig(FilterConfig filterConfig) {
            this.filterConfig = filterConfig;
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
            if (x == null || y == null || xOffset == null || width == null
                    || height == null || buttonName == null || filterConfig == null) {
                throw new IllegalArgumentException("Not all fields were set!");
            }
        }
    }
}
