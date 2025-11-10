package com.nemonotfound.nemos.inventory.sorting.config.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nemonotfound.nemos.inventory.sorting.Constants;
import com.nemonotfound.nemos.inventory.sorting.config.model.ComponentConfig;
import com.nemonotfound.nemos.inventory.sorting.config.model.FilterConfig;

import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static com.nemonotfound.nemos.inventory.sorting.config.DefaultConfigValues.*;
import static com.nemonotfound.nemos.inventory.sorting.config.DefaultConfigs.DEFAULT_COMPONENT_CONFIGS;
import static com.nemonotfound.nemos.inventory.sorting.config.DefaultConfigs.DEFAULT_IRON_CHEST_COMPONENT_CONFIGS;

public class ConfigService {

    private static ConfigService INSTANCE;

    private final Gson gson;

    private static final TypeToken<List<ComponentConfig>> COMPONENT_CONFIG_TYPE = new TypeToken<>() {};
    private static final TypeToken<FilterConfig> FILTER_CONFIG_TYPE_TOKEN = new TypeToken<>() {};

    private ConfigService(Gson gson) {
        this.gson = gson;
    }

    public static ConfigService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ConfigService(new Gson());
        }

        return INSTANCE;
    }

    public <T> void writeConfig(boolean shouldUpdate, String filePath, T config) {
        if (!shouldUpdate && Files.exists(Paths.get(filePath))) {
            return;
        }

        try {
            Files.createDirectories(Paths.get(CONFIG_DIRECTORY_PATH));
        } catch (Exception e) {
            Constants.LOG.error("An error occurred while creating directories:\n", e);
        }

        try(FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(config, writer);
        } catch (Exception e) {
            Constants.LOG.error("An error occurred while writing the config:\n", e);
        }
    }

    public List<ComponentConfig> readOrGetDefaultComponentConfigs() {
        return readOrGetDefaultConfig(COMPONENT_CONFIG_PATH, COMPONENT_CONFIG_TYPE, DEFAULT_COMPONENT_CONFIGS);
    }

    public FilterConfig readOrGetDefaultFilterConfig() {
        return readOrGetDefaultConfig(FILTER_CONFIG_PATH, FILTER_CONFIG_TYPE_TOKEN, new FilterConfig());
    }

    public List<ComponentConfig> readOrGetDefaultIronChestComponentConfigs() {
        return readOrGetDefaultConfig(IRON_CHEST_COMPONENT_CONFIG_PATH, COMPONENT_CONFIG_TYPE, DEFAULT_IRON_CHEST_COMPONENT_CONFIGS);
    }

    private <T> T readOrGetDefaultConfig(String filePath, TypeToken<T> typeToken, T defaultValue) {
        try(FileReader reader = new FileReader(filePath)) {
            return gson.fromJson(reader, typeToken);
        } catch (Exception e) {
            Constants.LOG.error("An error occurred while reading the config:\n", e);

            return defaultValue;
        }
    }

    public Optional<ComponentConfig> getOrDefaultComponentConfig(List<ComponentConfig> configsList, String componentName) {
        var optionalConfig = configsList.stream()
                .filter(config -> config.componentName().equals(componentName))
                .findFirst();

        if (optionalConfig.isEmpty()) {
            return DEFAULT_COMPONENT_CONFIGS.stream()
                    .filter(config -> config.componentName().equals(componentName))
                    .findFirst();
        }

        return optionalConfig;
    }
}
