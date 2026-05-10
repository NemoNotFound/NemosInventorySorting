package com.nemonotfound.nemos.inventory.sorting.mixin;

import com.nemonotfound.nemos.inventory.sorting.gui.components.FilterBox;
import com.nemonotfound.nemos.inventory.sorting.gui.components.RecipeBookUpdatable;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.RecipeBookMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractRecipeBookScreen.class)
public abstract class AbstractRecipeBookScreenMixin<T extends RecipeBookMenu> extends AbstractContainerScreen<T> {

    @Unique
    private int nemosInventorySorting$previousLeftPos;

    public AbstractRecipeBookScreenMixin(T menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    /**
     * Updates the X position of all child widgets that implement {@link RecipeBookUpdatable}
     * after the screen has finished initializing.
     * <p>
     * This ensures that custom widgets are correctly aligned with the final value of {@link #leftPos},
     * when the recipe book is toggled.
     */
    @Inject(method = "init", at = @At("TAIL"))
    private void updateXPosition(CallbackInfo ci) {
        nemosInventorySorting$previousLeftPos = this.leftPos;
        nemosInventorySorting$updateWidgetPositions();
    }

    @Inject(method = "extractRenderState", at = @At("HEAD"))
    private void onExtractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
        if (this.leftPos != nemosInventorySorting$previousLeftPos) {
            nemosInventorySorting$previousLeftPos = this.leftPos;
            nemosInventorySorting$updateWidgetPositions();
        }
    }

    @Unique
    private void nemosInventorySorting$updateWidgetPositions() {
        children().stream()
                .filter(widget -> widget instanceof RecipeBookUpdatable)
                .forEach(widget -> ((RecipeBookUpdatable) widget).updateXPosition(this.leftPos));
    }

    /**
     * Prevents the recipe book from handling key input when a {@link FilterBox} exists and is focused.
     */
    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void keyPressed(KeyEvent keyEvent, CallbackInfoReturnable<Boolean> cir) {
        var optionalFilterBox = children().stream()
                .filter(widget -> widget instanceof FilterBox)
                .findFirst();

        if (optionalFilterBox.isPresent() && optionalFilterBox.get().isFocused()) {
            cir.setReturnValue(super.keyPressed(keyEvent));
        }
    }
}