package com.nemonotfound.nemos.inventory.sorting.gui.components.buttons;

import com.nemonotfound.nemos.inventory.sorting.models.Position;
import com.nemonotfound.nemos.inventory.sorting.models.Size;
import com.nemonotfound.nemos.inventory.sorting.models.SlotRange;
import com.nemonotfound.nemos.inventory.sorting.models.config.SettingsConfig;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractContainerButton extends AbstractButton {

    private static final int HOTBAR_SLOT_COUNT = 9;

    protected final AbstractContainerMenu menu;
    protected final int startIndex;
    private final int endIndex;
    private final Component buttonName;
    private final boolean inventoryButton;

    public AbstractContainerButton(Position position, int xOffset, Size size, SlotRange slotRange, Component buttonName, AbstractContainerMenu menu) {
        super(position, xOffset, size, buttonName);
        this.buttonName = buttonName;
        this.menu = menu;
        this.startIndex = slotRange.startIndex();
        this.endIndex = slotRange.endIndex();
        this.inventoryButton = startIndex != 0;
        updateTooltip();
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
        if (keyEvent.key() == InputConstants.KEY_LSHIFT || keyEvent.key() == InputConstants.KEY_RSHIFT) {
            updateTooltip();
        }
    }

    @Override
    protected Identifier getTexture() {
        return this.isHovered() ? getButtonHoverTexture() : getButtonTexture();
    }

    protected abstract Identifier getButtonHoverTexture();

    protected abstract Identifier getButtonTexture();

    private void updateTooltip() {
        if (!inventoryButton || !Minecraft.getInstance().hasShiftDown()) {
            setTooltip(Tooltip.create(buttonName));
            return;
        }

        var translationKey = SettingsConfig.INSTANCE.includeHotbarByDefault()
                ? "nemos_inventory_sorting.gui.excludeHotbar"
                : "nemos_inventory_sorting.gui.includeHotbar";
        var shiftButtonName = buttonName.copy()
                .append(" ")
                .append(Component.translatable(translationKey));

        setTooltip(Tooltip.create(shiftButtonName));
    }

    protected int getEndIndex() {
        return shouldIncludeHotbar() ? endIndex + HOTBAR_SLOT_COUNT : endIndex;
    }

    public boolean isWithinSlotRange(SlotRange slotRange) {
        return startIndex == slotRange.startIndex() && getEndIndex() == slotRange.endIndex();
    }

    private boolean shouldIncludeHotbar() {
        return inventoryButton && SettingsConfig.INSTANCE.shouldIncludeHotbar(Minecraft.getInstance().hasShiftDown());
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {
        this.defaultButtonNarrationText(narrationElementOutput);
    }
}
