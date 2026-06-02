package com.nemonotfound.nemos.inventory.sorting.factory;

import com.nemonotfound.nemos.inventory.sorting.gui.components.buttons.SortButton;
import com.nemonotfound.nemos.inventory.sorting.models.Offset;
import com.nemonotfound.nemos.inventory.sorting.models.Position;
import com.nemonotfound.nemos.inventory.sorting.models.Size;
import com.nemonotfound.nemos.inventory.sorting.models.SlotRange;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class SortButtonFactory extends ButtonFactory<SortButton> {

    private static SortButtonFactory INSTANCE;

    private SortButtonFactory() {}

    public static SortButtonFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SortButtonFactory();
        }

        return INSTANCE;
    }

    @Override
    public SortButton createButton(SlotRange slotRange, Position position, Offset offset, Size size, AbstractContainerMenu menu) {
        var buttonName = Component.translatable("nemos_inventory_sorting.gui.sortButton");
        var positionAfterOffset = new Position(getLeftPosWithOffset(position.x(), offset.x()), position.y() + offset.y());

        return new SortButton(positionAfterOffset, offset.x(), size, slotRange, buttonName, menu);
    }
}
