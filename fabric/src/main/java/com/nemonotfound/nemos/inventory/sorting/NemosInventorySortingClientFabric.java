package com.nemonotfound.nemos.inventory.sorting;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import static com.nemonotfound.nemos.inventory.sorting.Constants.MOD_ID;

public class NemosInventorySortingClientFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        registerBuiltInResourcePack();
        NemosInventorySortingClientCommon.init();
    }

    private void registerBuiltInResourcePack() {
        FabricLoader.getInstance().getModContainer(MOD_ID).ifPresent(container ->
                ResourceManagerHelper.registerBuiltinResourcePack(
                        new ResourceLocation(MOD_ID, "dark_mode"),
                        container,
                        Component.translatable("resourcePack.nemos_inventory_sorting.dark_mode.name"),
                        ResourcePackActivationType.NORMAL
                ));
    }
}
