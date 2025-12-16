package com.nemonotfound.nemos.inventory.sorting.gui.components.buttons;

import com.nemonotfound.nemos.inventory.sorting.gui.components.RecipeBookUpdatable;
import com.nemonotfound.nemos.inventory.sorting.model.Position;
import com.nemonotfound.nemos.inventory.sorting.model.Size;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public abstract class AbstractButton extends AbstractWidget implements RecipeBookUpdatable {

    private final int xOffset;

    public AbstractButton(int x, int y, int xOffset, int width, int height, Component message) {
        super(x, y, width, height, message);
        this.xOffset = xOffset;
    }

    public AbstractButton(Position position, int xOffset, Size size, Component message) {
        super(position.x(), position.y(), size.width(), size.height(), message);
        this.xOffset = xOffset;
    }

    protected abstract KeyMapping getKeyMapping();

    protected abstract Identifier getTexture();

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, getTexture(), this.getX(), this.getY(), this.getWidth(), this.getHeight());
    }

    @Override
    public abstract void onClick(@NotNull MouseButtonEvent mouseButtonEvent, boolean isDoubleClick);

    @Override
    public void updateXPosition(int leftPos) {
        this.setX(leftPos + this.xOffset);
    }

    @Override
    public boolean keyPressed(@NotNull KeyEvent keyEvent) {
        var minecraft = Minecraft.getInstance();
        var isKeyPressed = Arrays.stream(minecraft.options.keyMappings)
                .filter(keyMapping -> keyMapping.same(getKeyMapping()))
                .anyMatch(keyMapping -> keyMapping.matches(keyEvent));

        if (!isKeyPressed) {
            return super.keyPressed(keyEvent);
        }

        playDownSound(minecraft.getSoundManager());
        onClick(new MouseButtonEvent(0, 0, new MouseButtonInfo(0, 0)), false);

        return true;
    }

    @Override
    public boolean mouseClicked(@NotNull MouseButtonEvent mouseButtonEvent, boolean bl) {
        var minecraft = Minecraft.getInstance();
        var isKeyPressed = Arrays.stream(minecraft.options.keyMappings)
                .filter(keyMapping -> keyMapping.same(getKeyMapping()))
                .anyMatch(keyMapping -> keyMapping.matchesMouse(mouseButtonEvent));

        if (!isKeyPressed) {
            return super.mouseClicked(mouseButtonEvent, bl);
        }

        playDownSound(minecraft.getSoundManager());
        onClick(mouseButtonEvent, bl);

        return true;
    }
}
