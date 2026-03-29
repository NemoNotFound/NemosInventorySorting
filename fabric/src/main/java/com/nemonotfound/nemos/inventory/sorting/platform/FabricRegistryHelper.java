package com.nemonotfound.nemos.inventory.sorting.platform;

import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;

import java.util.function.Supplier;

public class FabricRegistryHelper implements IRegistryHelper {

    @Override
    public Supplier<KeyMapping> registerKeyMapping(KeyMapping keyMapping) {
        var registeredKeyMapping = KeyMappingHelper.registerKeyMapping(keyMapping);
        return () -> registeredKeyMapping;
    }
}
