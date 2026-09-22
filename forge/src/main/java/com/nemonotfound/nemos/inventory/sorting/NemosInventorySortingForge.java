package com.nemonotfound.nemos.inventory.sorting;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

import static com.nemonotfound.nemos.inventory.sorting.Constants.MOD_ID;
import static com.nemonotfound.nemos.inventory.sorting.config.model.SettingsConfig.INSTANCE;

@Mod(MOD_ID)
public class NemosInventorySortingForge {

    public NemosInventorySortingForge(FMLJavaModLoadingContext context) {
        if (FMLEnvironment.dist.isClient()) {
            NemosInventorySortingClientCommon.init();
        }

        var modEventBus = context.getModEventBus();
        modEventBus.addListener(this::addBuiltInResourcePack);
        modEventBus.addListener(this::registerKeyMappings);
    }

    private void addBuiltInResourcePack(AddPackFindersEvent event) {
        if (event.getPackType() == PackType.CLIENT_RESOURCES) {
            var resourcePath = ModList.get().getModFileById(MOD_ID).getFile().findResource("resourcepacks/dark_mode");
            var pathPackResources = new PathPackResources("dark_mode", resourcePath, true);
            var pack = Pack.readMetaAndCreate(
                    "builtin/dark_mode",
                    Component.translatable("nemos_inventory_sorting.resourcePack.darkMode.name"),
                    false,
                    (supplier) -> pathPackResources,
                    PackType.CLIENT_RESOURCES,
                    Pack.Position.TOP,
                    PackSource.BUILT_IN
            );

            event.addRepositorySource((packConsumer) -> packConsumer.accept(pack));
        }
    }

    private void registerKeyMappings(RegisterKeyMappingsEvent event) {
        if (!INSTANCE.areKeyMappingsEnabled()) {
            return;
        }

        if (INSTANCE.areHoverKeyMappingsEnabled()) {
            event.register(ModKeyMappings.HOVER_SORT.get());
            event.register(ModKeyMappings.HOVER_MOVE_SAME.get());
            event.register(ModKeyMappings.HOVER_MOVE_ALL.get());
            event.register(ModKeyMappings.HOVER_DROP_ALL.get());
        }

        if (INSTANCE.areContainerKeyMappingsEnabled()) {
            event.register(ModKeyMappings.SORT.get());
            event.register(ModKeyMappings.SORT_INVENTORY.get());
            event.register(ModKeyMappings.MOVE_SAME.get());
            event.register(ModKeyMappings.MOVE_SAME_INVENTORY.get());
            event.register(ModKeyMappings.MOVE_ALL.get());
            event.register(ModKeyMappings.MOVE_ALL_INVENTORY.get());
            event.register(ModKeyMappings.DROP_ALL.get());
            event.register(ModKeyMappings.DROP_ALL_INVENTORY.get());
        }

        event.register(ModKeyMappings.TOGGLE_FILTER_PERSISTENCE.get());
        event.register(ModKeyMappings.QUICK_SEARCH.get());
    }
}
