package com.nemonotfound.nemos.inventory.sorting.factory;

import com.nemonotfound.nemos.inventory.sorting.gui.components.buttons.AbstractContainerButton;
import com.nemonotfound.nemos.inventory.sorting.model.Offset;
import com.nemonotfound.nemos.inventory.sorting.model.Position;
import com.nemonotfound.nemos.inventory.sorting.model.Size;
import com.nemonotfound.nemos.inventory.sorting.model.SlotRange;
import net.minecraft.world.inventory.AbstractContainerMenu;

public interface ButtonCreator<T extends AbstractContainerButton> {

    T createButton(
            SlotRange slotRange,
            Position position,
            Offset offset,
            Size size,
            AbstractContainerMenu menu
    );
}
