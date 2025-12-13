package com.nemonotfound.nemos.inventory.sorting.factory;

import com.nemonotfound.nemos.inventory.sorting.gui.components.buttons.MoveAllButton;
import com.nemonotfound.nemos.inventory.sorting.model.Offset;
import com.nemonotfound.nemos.inventory.sorting.model.Position;
import com.nemonotfound.nemos.inventory.sorting.model.Size;
import com.nemonotfound.nemos.inventory.sorting.model.SlotRange;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class MoveAllButtonFactory extends ButtonFactory<MoveAllButton> {

    private static MoveAllButtonFactory INSTANCE;

    private MoveAllButtonFactory() {}

    public static MoveAllButtonFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MoveAllButtonFactory();
        }

        return INSTANCE;
    }

    @Override
    public MoveAllButton createButton(SlotRange slotRange, Position position, Offset offset, Size size, AbstractContainerMenu menu) {
        var buttonName = Component.translatable("nemos_inventory_sorting.gui.moveAll");
        var positionAfterOffset = new Position(getLeftPosWithOffset(position.x(), offset.x()), position.y() + offset.y());

        return new MoveAllButton(positionAfterOffset, offset.x(), size, slotRange, buttonName, menu);
    }
}
