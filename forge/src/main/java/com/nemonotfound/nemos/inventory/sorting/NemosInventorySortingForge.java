package com.nemonotfound.nemos.inventory.sorting;

import com.nemonotfound.nemos.inventory.sorting.client.SortingKeyMappings;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.Optional;

import static com.nemonotfound.nemos.inventory.sorting.Constants.MOD_ID;

@Mod(MOD_ID)
public class NemosInventorySortingForge {

    public NemosInventorySortingForge() {
        if (FMLEnvironment.dist.isClient()) {
            SortingCommonClient.init();
        }

        AddPackFindersEvent.BUS.addListener(this::addBuiltInResourcePack);
        RegisterKeyMappingsEvent.BUS.addListener(this::registerKeyMappings);
    }

    private void addBuiltInResourcePack(AddPackFindersEvent event) {
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

    private void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(SortingKeyMappings.SORT.get());
        event.register(SortingKeyMappings.SORT_INVENTORY.get());
        event.register(SortingKeyMappings.MOVE_SAME.get());
        event.register(SortingKeyMappings.MOVE_SAME_INVENTORY.get());
        event.register(SortingKeyMappings.MOVE_ALL.get());
        event.register(SortingKeyMappings.MOVE_ALL_INVENTORY.get());
        event.register(SortingKeyMappings.DROP_ALL.get());
        event.register(SortingKeyMappings.DROP_ALL_INVENTORY.get());
        event.register(SortingKeyMappings.TOGGLE_FILTER_PERSISTENCE.get());
        event.register(SortingKeyMappings.QUICK_SEARCH.get());
    }
}