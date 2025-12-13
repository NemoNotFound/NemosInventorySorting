package com.nemonotfound.nemos.inventory.sorting.factory;

import com.nemonotfound.nemos.inventory.sorting.gui.components.buttons.MoveSameButton;
import com.nemonotfound.nemos.inventory.sorting.model.Offset;
import com.nemonotfound.nemos.inventory.sorting.model.Position;
import com.nemonotfound.nemos.inventory.sorting.model.Size;
import com.nemonotfound.nemos.inventory.sorting.model.SlotRange;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class MoveSameButtonFactory extends ButtonFactory<MoveSameButton> {

    private static MoveSameButtonFactory INSTANCE;

    private MoveSameButtonFactory() {}

    public static MoveSameButtonFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MoveSameButtonFactory();
        }

        return INSTANCE;
    }

    @Override
    public MoveSameButton createButton(SlotRange slotRange, Position position, Offset offset, Size size, AbstractContainerMenu menu) {
        var buttonName = Component.translatable("nemos_inventory_sorting.gui.moveSame");
        var positionAfterOffset = new Position(getLeftPosWithOffset(position.x(), offset.x()), position.y() + offset.y());

        return new MoveSameButton(positionAfterOffset, offset.x(), size, slotRange, buttonName, menu);
    }
}
