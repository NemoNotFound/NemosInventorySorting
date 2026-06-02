package com.nemonotfound.nemos.inventory.sorting;

import com.nemonotfound.nemos.inventory.sorting.client.SortingKeymappingCategories;
import com.nemonotfound.nemos.inventory.sorting.client.SortingKeyMappings;
import com.nemonotfound.nemos.inventory.sorting.config.DefaultConfigs;
import com.nemonotfound.nemos.inventory.sorting.models.config.FilterConfig;
import com.nemonotfound.nemos.inventory.sorting.service.config.ConfigService;
import com.nemonotfound.nemos.inventory.sorting.helper.SortOrder;
import com.nemonotfound.nemos.inventory.sorting.platform.IModLoaderHelper;
import com.nemonotfound.nemos.inventory.sorting.platform.IRegistryHelper;

import java.util.ServiceLoader;

import static com.nemonotfound.nemos.inventory.sorting.config.DefaultConfigValues.*;
import static com.nemonotfound.nemos.inventory.sorting.config.DefaultConfigs.*;

public class SortingCommonClient {

    public static final IRegistryHelper REGISTRY_HELPER = ServiceLoader.load(IRegistryHelper.class).findFirst().orElseThrow();
    public static final IModLoaderHelper MOD_LOADER_HELPER = ServiceLoader.load(IModLoaderHelper.class).findFirst().orElseThrow();

    public static void init() {
        Constants.LOGGER.info("Thank you for using Nemo's Inventory Sorting!");
        SortingKeymappingCategories.init();
        SortingKeyMappings.init();
        DefaultConfigs.setupDefaultConfigs();

        ConfigService.INSTANCE.writeConfig(false, COMPONENT_CONFIG_PATH, DEFAULT_COMPONENT_CONFIGS);
        ConfigService.INSTANCE.writeConfig(false, FILTER_CONFIG_PATH, FilterConfig.INSTANCE);

        if (MOD_LOADER_HELPER.isModLoaded("ironchest")) {
            ConfigService.INSTANCE.writeConfig(false, IRON_CHEST_COMPONENT_CONFIG_PATH, DEFAULT_IRON_CHEST_COMPONENT_CONFIGS);
        }

        SortOrder.init();
        ConfigService.loadFilterConfig();
    }
}