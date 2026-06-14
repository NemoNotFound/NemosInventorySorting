package com.nemonotfound.nemos.inventory.sorting.service;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.stream.Collectors;

public class TooltipService {

    public static final TooltipService INSTANCE = new TooltipService(Minecraft.getInstance());

    private final Minecraft minecraft;

    private TooltipService(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    public static TooltipService getInstance() {
        return INSTANCE;
    }

    public List<Component> retrieveTooltipLines(ItemStack itemStack) {
        return itemStack.getTooltipLines(
                Item.TooltipContext.of(minecraft.level),
                minecraft.player,
                minecraft.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL
        );
    }

    public String retrieveEnchantmentNames(List<Component> tooltipComponents) {
        return tooltipComponents.stream()
                .filter(component -> component.toString().contains("enchantment"))
                .map(Component::getString)
                .collect(Collectors.joining(","));
    }

    public String retrieveJukeboxSongName(List<Component> tooltipComponents) {
        return tooltipComponents.stream()
                .filter(component -> component.toString().contains("jukebox_song"))
                .map(Component::getString)
                .findFirst()
                .orElse("");
    }

    public String retrievePotionName(List<Component> tooltipComponents) {
        return tooltipComponents.stream()
                .filter(component -> component.toString().contains("potion.withDuration") ||
                        component.toString().contains("potion.withAmplifier"))
                .map(Component::getString)
                .findFirst()
                .orElse("");
    }
}
