package com.nemonotfound.nemos.inventory.sorting.service;

import com.nemonotfound.nemos.inventory.sorting.models.ContainerInputContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;

import java.util.Optional;

public class ContainerInputService {

    private static final int PRIMARY_MOUSE_BUTTON = 0;
    private static final int SECONDARY_MOUSE_BUTTON = 1;

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

    public void performClick(AbstractContainerMenu menu, ContainerInputContext context, int slot) {
        performContainerInput(menu, context, slot, PRIMARY_MOUSE_BUTTON, ContainerInput.PICKUP);
    }

    public void performRightClick(AbstractContainerMenu menu, ContainerInputContext context, int slot) {
        performContainerInput(menu, context, slot, SECONDARY_MOUSE_BUTTON, ContainerInput.PICKUP);
    }

    public void performPickup(int containerId, int slot, int mouseButton, MultiPlayerGameMode gameMode, LocalPlayer player) {
        gameMode.handleContainerInput(containerId, slot, mouseButton, ContainerInput.PICKUP, player);
    }

    private void performContainerInput(AbstractContainerMenu menu, ContainerInputContext context, int slot, int mouseButton, ContainerInput containerInput) {
        context.gameMode().handleContainerInput(menu.containerId, slot, mouseButton, containerInput, context.player());
    }
}
