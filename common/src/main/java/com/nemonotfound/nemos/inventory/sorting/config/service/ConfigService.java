package com.nemonotfound.nemos.inventory.sorting.config.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.nemonotfound.nemos.inventory.sorting.Constants;
import com.nemonotfound.nemos.inventory.sorting.config.model.ComponentConfig;
import com.nemonotfound.nemos.inventory.sorting.config.model.FilterConfig;
import com.nemonotfound.nemos.inventory.sorting.config.model.LockedSlotsConfig;
import com.nemonotfound.nemos.inventory.sorting.config.model.SettingsConfig;

import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

import static com.nemonotfound.nemos.inventory.sorting.config.DefaultConfigValues.*;
import static com.nemonotfound.nemos.inventory.sorting.config.DefaultConfigs.DEFAULT_COMPONENT_CONFIGS;

public class ConfigService {

    private static ConfigService INSTANCE;

    private final Gson gson;

    private static final TypeToken<List<ComponentConfig>> COMPONENT_CONFIG_TYPE = new TypeToken<>() {};
    private static final TypeToken<FilterConfig> FILTER_CONFIG_TYPE_TOKEN = new TypeToken<>() {};
    private static final TypeToken<SettingsConfig> SETTINGS_CONFIG_TYPE_TOKEN = new TypeToken<>() {};
    private static final TypeToken<LockedSlotsConfig> LOCKED_SLOTS_CONFIG_TYPE_TOKEN = new TypeToken<>() {};

    private ConfigService(Gson gson) {
        this.gson = gson;
    }

    public static ConfigService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ConfigService(new GsonBuilder().setPrettyPrinting().create());
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
        var configs = readOrGetDefaultConfig(COMPONENT_CONFIG_PATH, COMPONENT_CONFIG_TYPE, DEFAULT_COMPONENT_CONFIGS);
        var migratedConfigs = configs.stream().map(this::migrateDefaultComponentValues).toList();

        if (!configs.equals(migratedConfigs)) {
            writeConfig(true, COMPONENT_CONFIG_PATH, migratedConfigs);
        }

        return migratedConfigs;
    }

    public FilterConfig readOrGetDefaultFilterConfig() {
        return readOrGetDefaultConfig(FILTER_CONFIG_PATH, FILTER_CONFIG_TYPE_TOKEN, new FilterConfig());
    }

    public synchronized void loadSettingsConfig() {
        SettingsConfig.INSTANCE = readOrGetDefaultConfig(GENERAL_CONFIG_PATH, SETTINGS_CONFIG_TYPE_TOKEN, SettingsConfig.INSTANCE);
    }

    public void loadLockedSlotsConfig() {
        if (Files.exists(Paths.get(LOCKED_SLOTS_CONFIG_PATH))) {
            LockedSlotsConfig.INSTANCE = readOrGetDefaultConfig(LOCKED_SLOTS_CONFIG_PATH, LOCKED_SLOTS_CONFIG_TYPE_TOKEN, LockedSlotsConfig.INSTANCE);
        }
    }

    public void migrateLegacyConfigs() {
        migrateConfig("config.json", COMPONENT_CONFIG_PATH);
        migrateSettingsConfig();
        migrateConfig("filter-config.json", FILTER_CONFIG_PATH);
        migrateConfig("locked-slots-config.json", LOCKED_SLOTS_CONFIG_PATH);
        migrateConfig("iron-chest-config.json", IRON_CHEST_COMPONENT_CONFIG_PATH);
    }

    private void migrateSettingsConfig() {
        var oldPath = Paths.get(CONFIG_DIRECTORY_PATH, "settings.json");
        var newPath = Paths.get(GENERAL_CONFIG_PATH);

        if (!Files.exists(oldPath)) {
            return;
        }

        try {
            if (!Files.exists(newPath) || isDefaultSettingsFile(newPath)) {
                Files.move(oldPath, newPath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {
            Constants.LOG.error("An error occurred while migrating settings config {} to {}:\n", oldPath, newPath, e);
        }
    }

    private boolean isDefaultSettingsFile(java.nio.file.Path path) throws java.io.IOException {
        try (var reader = Files.newBufferedReader(path)) {
            return JsonParser.parseReader(reader).equals(gson.toJsonTree(SettingsConfig.INSTANCE));
        }
    }

    private ComponentConfig migrateDefaultComponentValues(ComponentConfig config) {
        if (isOldStorageButtonDefault(config)) {
            return new ComponentConfig(config.componentName(), config.isEnabled(), config.xOffset(), config.rightXOffset(),
                    Y_OFFSET_CONTAINER, config.width(), config.height());
        }

        if (config.componentName().equals(ITEM_FILTER) && config.yOffset() != null && config.yOffset() == -16
                && config.width() == 77 && config.height() == 15) {
            return new ComponentConfig(config.componentName(), config.isEnabled(), config.xOffset(), config.rightXOffset(),
                    Y_OFFSET_ITEM_FILTER, ITEM_FILTER_WIDTH, ITEM_FILTER_HEIGHT);
        }

        if (config.componentName().equals(FILTER_PERSISTENCE_TOGGLE) && config.yOffset() != null && config.yOffset() == -15) {
            return new ComponentConfig(config.componentName(), config.isEnabled(), config.xOffset(), config.rightXOffset(),
                    Y_OFFSET_FILTER_PERSISTENCE_TOGGLE, config.width(), config.height());
        }

        return config;
    }

    private boolean isOldStorageButtonDefault(ComponentConfig config) {
        return config.yOffset() != null && config.yOffset() == -6
                && (config.componentName().equals(SORT_STORAGE_CONTAINER)
                || config.componentName().equals(MOVE_SAME_STORAGE_CONTAINER)
                || config.componentName().equals(MOVE_ALL_STORAGE_CONTAINER)
                || config.componentName().equals(DROP_ALL_STORAGE_CONTAINER));
    }

    private void migrateConfig(String oldFileName, String currentPath) {
        var oldPath = Paths.get(CONFIG_DIRECTORY_PATH, oldFileName);
        var newPath = Paths.get(currentPath);

        if (!Files.exists(oldPath) || Files.exists(newPath)) {
            return;
        }

        try {
            Files.move(oldPath, newPath);
        } catch (Exception e) {
            Constants.LOG.error("An error occurred while migrating config {} to {}:\n", oldPath, newPath, e);
        }
    }

    private <T> T readOrGetDefaultConfig(String filePath, TypeToken<T> typeToken, T defaultValue) {
        try(FileReader reader = new FileReader(filePath)) {
            var config = gson.fromJson(reader, typeToken);
            return config != null ? config : defaultValue;
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
