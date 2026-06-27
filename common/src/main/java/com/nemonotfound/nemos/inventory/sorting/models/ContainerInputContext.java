package com.nemonotfound.nemos.inventory.sorting.models;

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;

public record ContainerInputContext(MultiPlayerGameMode gameMode, LocalPlayer player) {
}
