package com.nemonotfound.nemos.inventory.sorting.events;

import com.nemonotfound.nemos.inventory.sorting.client.InventorySortingKeyMappings;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;

import static com.nemonotfound.nemos.inventory.sorting.Constants.MOD_ID;

@EventBusSubscriber(value = Dist.CLIENT, modid = MOD_ID)
public class ClientEvents {

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(InventorySortingKeyMappings.SORT.get());
        event.register(InventorySortingKeyMappings.SORT_INVENTORY.get());
        event.register(InventorySortingKeyMappings.MOVE_SAME.get());
        event.register(InventorySortingKeyMappings.MOVE_SAME_INVENTORY.get());
        event.register(InventorySortingKeyMappings.MOVE_ALL.get());
        event.register(InventorySortingKeyMappings.MOVE_ALL_INVENTORY.get());
        event.register(InventorySortingKeyMappings.DROP_ALL.get());
        event.register(InventorySortingKeyMappings.DROP_ALL_INVENTORY.get());
        event.register(InventorySortingKeyMappings.TOGGLE_FILTER_PERSISTENCE.get());
        event.register(InventorySortingKeyMappings.QUICK_SEARCH.get());
    }

    @SubscribeEvent
    public static void addBuiltInResourcePack(AddPackFindersEvent event) {
        if (event.getPackType() == PackType.CLIENT_RESOURCES) {
            event.addPackFinders(
                    Identifier.fromNamespaceAndPath(MOD_ID, "resourcepacks/dark_mode"),
                    PackType.CLIENT_RESOURCES,
                    Component.translatable("nemos_inventory_sorting.resourcePack.darkMode.name"),
                    PackSource.BUILT_IN,
                    false,
                    Pack.Position.TOP
            );
        }
    }
}
