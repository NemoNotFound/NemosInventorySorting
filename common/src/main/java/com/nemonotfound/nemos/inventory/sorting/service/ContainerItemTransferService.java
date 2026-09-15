package com.nemonotfound.nemos.inventory.sorting.service;

import com.nemonotfound.nemos.inventory.sorting.models.ContainerInputContext;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class ContainerItemTransferService {
    private static ContainerItemTransferService INSTANCE;
    private final ContainerInputService containerInputService;
    private final ScrollTransferTargetService targetService;

    private ContainerItemTransferService(ContainerInputService containerInputService, ScrollTransferTargetService targetService) {
        this.containerInputService = containerInputService;
        this.targetService = targetService;
    }

    public static ContainerItemTransferService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ContainerItemTransferService(ContainerInputService.getInstance(), ScrollTransferTargetService.getInstance());
        }

        return INSTANCE;
    }

    public boolean transfer(AbstractContainerMenu menu, ContainerInputContext context, ScrollTransferTargetService.SlotTransfer transfer) {
        return transfer.result() ? transferResult(menu, context, transfer) : transferSingleItem(menu, context, transfer);
    }

    private boolean transferSingleItem(AbstractContainerMenu menu, ContainerInputContext context, ScrollTransferTargetService.SlotTransfer transfer) {
        var sourceSlotIndex = transfer.sourceSlot().index;
        var targetSlotIndex = transfer.targetSlot().index;
        var temporaryCarriedSlot = targetService.getTemporaryCarriedSlot(menu, menu.getCarried(), sourceSlotIndex, targetSlotIndex);

        if (!menu.getCarried().isEmpty() && temporaryCarriedSlot.isEmpty()) {
            return false;
        }

        temporaryCarriedSlot.ifPresent(slot -> containerInputService.leftClickPickup(menu, context, slot.index));

        containerInputService.rightClickPickup(menu, context, sourceSlotIndex);
        containerInputService.rightClickPickup(menu, context, targetSlotIndex);

        returnCarriedStackToSource(menu, context, sourceSlotIndex);

        temporaryCarriedSlot.filter(_ -> menu.getCarried().isEmpty())
                .ifPresent(slot -> containerInputService.leftClickPickup(menu, context, slot.index));

        return true;
    }

    private boolean transferResult(AbstractContainerMenu menu, ContainerInputContext context, ScrollTransferTargetService.SlotTransfer transfer) {
        if (!menu.getCarried().isEmpty()) {
            return false;
        }

        containerInputService.leftClickPickup(menu, context, transfer.sourceSlot().index);
        containerInputService.leftClickPickup(menu, context, transfer.targetSlot().index);

        return menu.getCarried().isEmpty();
    }

    private void returnCarriedStackToSource(AbstractContainerMenu menu, ContainerInputContext context, int sourceSlotIndex) {
        if (!menu.getCarried().isEmpty()) {
            containerInputService.leftClickPickup(menu, context, sourceSlotIndex);
        }
    }
}
