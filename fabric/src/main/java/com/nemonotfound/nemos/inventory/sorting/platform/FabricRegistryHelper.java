package com.nemonotfound.nemos.inventory.sorting.platform;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;

import java.util.function.Supplier;

public class FabricRegistryHelper implements IRegistryHelper {

    @Override
    public Supplier<KeyMapping> registerKeyMapping(KeyMapping keyMapping) {
        var registeredKeyMapping = KeyBindingHelper.registerKeyBinding(keyMapping);
        return () -> registeredKeyMapping;
    }
}
