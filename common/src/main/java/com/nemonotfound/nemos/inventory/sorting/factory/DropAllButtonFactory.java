package com.nemonotfound.nemos.inventory.sorting.factory;

import com.nemonotfound.nemos.inventory.sorting.gui.components.buttons.DropAllButton;
import com.nemonotfound.nemos.inventory.sorting.model.Offset;
import com.nemonotfound.nemos.inventory.sorting.model.Position;
import com.nemonotfound.nemos.inventory.sorting.model.Size;
import com.nemonotfound.nemos.inventory.sorting.model.SlotRange;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class DropAllButtonFactory extends ButtonFactory<DropAllButton> {

    private static DropAllButtonFactory INSTANCE;

    private DropAllButtonFactory() {}

    public static DropAllButtonFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DropAllButtonFactory();
        }

        return INSTANCE;
    }

    @Override
    public DropAllButton createButton(SlotRange slotRange, Position position, Offset offset, Size size, AbstractContainerMenu menu) {
        var buttonName = Component.translatable("nemos_inventory_sorting.gui.dropAll");
        var positionAfterOffset = new Position(getLeftPosWithOffset(position.x(), offset.x()), position.y() + offset.y());

        return new DropAllButton(positionAfterOffset, offset.x(), size, slotRange, buttonName, menu);
    }
}
