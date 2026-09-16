package com.nemonotfound.nemos.inventory.sorting.mixin;

import com.nemonotfound.nemos.inventory.sorting.gui.components.FilterBox;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EditBox.class)
public abstract class EditBoxMixin {

    @Redirect(
            method = "renderWidget",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/util/FormattedCharSequence;III)I")
    )
    private int drawText(GuiGraphics guiGraphics, Font font, FormattedCharSequence text, int x, int y, int color) {
        return guiGraphics.drawString(font, text, x, y, color, nemosInventorySorting$drawShadow());
    }

    @Redirect(
            method = "renderWidget",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)I")
    )
    private int drawHint(GuiGraphics guiGraphics, Font font, Component text, int x, int y, int color) {
        return guiGraphics.drawString(font, text, x, y, color, nemosInventorySorting$drawShadow());
    }

    @Redirect(
            method = "renderWidget",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;III)I")
    )
    private int drawCursor(GuiGraphics guiGraphics, Font font, String text, int x, int y, int color) {
        return guiGraphics.drawString(font, text, x, y, color, nemosInventorySorting$drawShadow());
    }

    @Unique
    private boolean nemosInventorySorting$drawShadow() {
        return !((Object) this instanceof FilterBox);
    }
}
