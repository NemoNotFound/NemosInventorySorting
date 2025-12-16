package com.nemonotfound.nemos.inventory.sorting;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import static com.nemonotfound.nemos.inventory.sorting.Constants.MOD_ID;

public class NemosInventorySortingClientFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        registerBuiltInResourcePack();
        NemosInventorySortingClientCommon.init();
    }

    private void registerBuiltInResourcePack() {
        FabricLoader.getInstance().getModContainer(MOD_ID).ifPresent(container ->
                ResourceLoader.registerBuiltinPack(
                        Identifier.fromNamespaceAndPath(MOD_ID, "dark_mode"),
                        container,
                        Component.translatable("nemos_inventory_sorting.resourcePack.darkMode.name"),
                        PackActivationType.NORMAL
                ));
    }
}
