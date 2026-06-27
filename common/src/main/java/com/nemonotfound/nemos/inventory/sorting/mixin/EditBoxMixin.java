package com.nemonotfound.nemos.inventory.sorting.mixin;

import com.nemonotfound.nemos.inventory.sorting.helper.WidgetSpritesGetter;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EditBox.class)
public abstract class EditBoxMixin extends AbstractWidget implements WidgetSpritesGetter {

    @Shadow
    @Final
    private static WidgetSprites SPRITES;

    public EditBoxMixin(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    @ModifyVariable(method = "extractWidgetRenderState", at = @At(value = "STORE"), name = "sprite")
    private Identifier getWidgetSpriteIdentifier(Identifier sprite) {
        return nemosInventorySorting$getWidgetSprites().get(this.isActive(), this.isFocused());
    }

    @Override
    public WidgetSprites nemosInventorySorting$getWidgetSprites() {
        return SPRITES;
    }
}
