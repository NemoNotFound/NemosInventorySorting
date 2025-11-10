package com.nemonotfound.nemos.inventory.sorting.mixin;

import com.nemonotfound.nemos.inventory.sorting.gui.components.RecipeBookUpdatable;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.RecipeBookMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractRecipeBookScreen.class)
public abstract class AbstractRecipeBookScreenMixinForge<T extends RecipeBookMenu> extends AbstractContainerScreen<T> {


    public AbstractRecipeBookScreenMixinForge(T menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Inject(method = "lambda$initButton$0", at = @At(value = "TAIL"))
    private void updateXPosition(Button button, CallbackInfo ci) {
        children().stream()
                .filter(widget -> widget instanceof RecipeBookUpdatable)
                .forEach(widget -> ((RecipeBookUpdatable) widget).updateXPosition(this.leftPos));
    }
}
