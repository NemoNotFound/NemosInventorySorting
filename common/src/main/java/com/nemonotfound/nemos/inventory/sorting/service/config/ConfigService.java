package com.nemonotfound.nemos.inventory.sorting.service.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nemonotfound.nemos.inventory.sorting.models.config.ComponentConfig;
import com.nemonotfound.nemos.inventory.sorting.models.config.FilterConfig;
import com.nemonotfound.nemos.inventory.sorting.enums.config.ConfigId;

import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static com.nemonotfound.nemos.inventory.sorting.Constants.LOGGER;
import static com.nemonotfound.nemos.inventory.sorting.config.DefaultConfigValues.*;
import static com.nemonotfound.nemos.inventory.sorting.config.DefaultConfigs.DEFAULT_COMPONENT_CONFIGS;
import static com.nemonotfound.nemos.inventory.sorting.config.DefaultConfigs.DEFAULT_IRON_CHEST_COMPONENT_CONFIGS;

public class ConfigService {

    public static ConfigService INSTANCE = new ConfigService();

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static final TypeToken<List<ComponentConfig>> COMPONENT_CONFIG_TYPE = new TypeToken<>() {};
    private static final TypeToken<FilterConfig> FILTER_CONFIG_TYPE_TOKEN = new TypeToken<>() {};

    public <T> void writeConfig(boolean update, String filePath, T config) {
        if (!update && Files.exists(Paths.get(filePath))) {
            return;
        }

        try {
            Files.createDirectories(Paths.get(CONFIG_DIRECTORY_PATH));
        } catch (Exception e) {
            LOGGER.error("An error occurred while creating directories:\n", e);
        }

        try(FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(config, writer);
        } catch (Exception e) {
            LOGGER.error("An error occurred while writing the config:\n", e);
        }
    }

    public List<ComponentConfig> readOrGetDefaultComponentConfigs() {
        return readOrDefault(COMPONENT_CONFIG_PATH, COMPONENT_CONFIG_TYPE, DEFAULT_COMPONENT_CONFIGS);
    }

    public static void loadFilterConfig() {
        FilterConfig.INSTANCE = readOrDefault(FILTER_CONFIG_PATH, FILTER_CONFIG_TYPE_TOKEN, FilterConfig.INSTANCE);
    }

    public List<ComponentConfig> readOrGetDefaultIronChestComponentConfigs() {
        return readOrDefault(IRON_CHEST_COMPONENT_CONFIG_PATH, COMPONENT_CONFIG_TYPE, DEFAULT_IRON_CHEST_COMPONENT_CONFIGS);
    }

    private static <T> T readOrDefault(String filePath, TypeToken<T> typeToken, T defaultValue) {
        try(FileReader reader = new FileReader(filePath)) {
            return gson.fromJson(reader, typeToken);
        } catch (Exception e) {
            LOGGER.error("An error occurred while reading the config:\n", e);

            return defaultValue;
        }
    }

    public Optional<ComponentConfig> getOrDefault(List<ComponentConfig> configs, ConfigId configId) { //TODO: Refactor
        var optionalConfig = configs.stream()
                .filter(config -> config.componentName().equals(configId.getId()))
                .findFirst();

        if (optionalConfig.isEmpty()) {
            return DEFAULT_COMPONENT_CONFIGS.stream()
                    .filter(config -> config.componentName().equals(configId.getId()))
                    .findFirst();
        }

        return optionalConfig;
    }
}
