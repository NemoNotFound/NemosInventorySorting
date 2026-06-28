package com.nemonotfound.nemos.inventory.sorting.service;

import com.nemonotfound.nemos.inventory.sorting.models.ContainerInputContext;
import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;

import java.util.Optional;

public class ContainerInputService {

    public static final int PRIMARY_MOUSE_BUTTON = 0;
    public static final int SECONDARY_MOUSE_BUTTON = 1;

    private static ContainerInputService INSTANCE;

    public static ContainerInputService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ContainerInputService();
        }

        return INSTANCE;
    }

    public Optional<ContainerInputContext> getContext() {
        var minecraft = Minecraft.getInstance();
        var player = minecraft.player;
        var gameMode = minecraft.gameMode;

        if (player == null || gameMode == null) {
            return Optional.empty();
        }

        return Optional.of(new ContainerInputContext(gameMode, player));
    }

    public void leftClickPickup(AbstractContainerMenu menu, ContainerInputContext context, int slot) {
        pickup(menu, context, slot, PRIMARY_MOUSE_BUTTON);
    }

    public void rightCLickPickup(AbstractContainerMenu menu, ContainerInputContext context, int slot) {
        pickup(menu, context, slot, SECONDARY_MOUSE_BUTTON);
    }

    public void pickup(AbstractContainerMenu menu, ContainerInputContext context, int slot, int mouseButton) {
        context.gameMode().handleContainerInput(menu.containerId, slot, mouseButton, ContainerInput.PICKUP, context.player());
    }
}
