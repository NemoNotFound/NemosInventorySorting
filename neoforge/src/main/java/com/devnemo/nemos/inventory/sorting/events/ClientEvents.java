package com.devnemo.nemos.inventory.sorting.events;

import com.devnemo.nemos.inventory.sorting.ModKeyMappings;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;

import java.util.Optional;

import static com.devnemo.nemos.inventory.sorting.Constants.MOD_ID;

@EventBusSubscriber(value = Dist.CLIENT, modid = MOD_ID)
public class ClientEvents {

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(ModKeyMappings.SORT.get());
        event.register(ModKeyMappings.SORT_INVENTORY.get());
        event.register(ModKeyMappings.MOVE_SAME.get());
        event.register(ModKeyMappings.MOVE_SAME_INVENTORY.get());
        event.register(ModKeyMappings.MOVE_ALL.get());
        event.register(ModKeyMappings.MOVE_ALL_INVENTORY.get());
        event.register(ModKeyMappings.DROP_ALL.get());
        event.register(ModKeyMappings.DROP_ALL_INVENTORY.get());
        event.register(ModKeyMappings.TOGGLE_FILTER_PERSISTENCE.get());
    }

    @SubscribeEvent
    public static void addBuiltInResourcePack(AddPackFindersEvent event) {
        if (event.getPackType() == PackType.CLIENT_RESOURCES) {
            var resourcePath = ModList.get().getModFileById(MOD_ID).getFile().findResource("resourcepacks/dark_mode");
            var packLocationInfo = new PackLocationInfo(
                    "builtin/dark_mode",
                    Component.translatable("nemos_inventory_sorting.resourcePack.darkMode.name"),
                    PackSource.BUILT_IN,
                    Optional.empty());
            var pathResourcesSupplier = new PathPackResources.PathResourcesSupplier(resourcePath);
            var packSelectionConfig = new PackSelectionConfig(false, Pack.Position.TOP, false);
            var pack = Pack.readMetaAndCreate(packLocationInfo,
                    pathResourcesSupplier,
                    PackType.CLIENT_RESOURCES,
                    packSelectionConfig);

            event.addRepositorySource((packConsumer) -> packConsumer.accept(pack));
        }
    }
}
