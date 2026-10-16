package com.nemonotfound.nemos.inventory.sorting.datagen.language;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class RussianProvider extends FabricLanguageProvider {
    public RussianProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, "ru_ru", registries);
    }

    @Override
    public void generateTranslations(HolderLookup.@NonNull Provider registries, TranslationBuilder translationBuilder) {
        translationBuilder.add("nemos_inventory_sorting.gui.sortButton", "Сортировать");
        translationBuilder.add("nemos_inventory_sorting.gui.dropAll", "Выбросить всё");
        translationBuilder.add("nemos_inventory_sorting.gui.dropAllShift", "Выбросить всё (включая панель быстрого доступа)");
        translationBuilder.add("nemos_inventory_sorting.gui.moveSame", "Переместить такие же предметы");
        translationBuilder.add("nemos_inventory_sorting.gui.moveSameShift", "Переместить такие же предметы (включая панель быстрого доступа)");
        translationBuilder.add("nemos_inventory_sorting.gui.moveAll", "Переместить всё");
        translationBuilder.add("nemos_inventory_sorting.gui.moveAllShift", "Переместить всё (включая панель быстрого доступа)");
        translationBuilder.add("nemos_inventory_sorting.gui.includeHotbar", "(включая панель быстрого доступа)");
        translationBuilder.add("nemos_inventory_sorting.gui.excludeHotbar", "(без панели быстрого доступа)");
        translationBuilder.add("nemos_inventory_sorting.gui.toggleFilterPersistence", "Переключить сохранение фильтра");
        translationBuilder.add("nemos_inventory_sorting.gui.toggleFilterPersistence.toggleOn", "Включить сохранение фильтра");
        translationBuilder.add("nemos_inventory_sorting.gui.toggleFilterPersistence.toggleOff", "Выключить сохранение фильтра");
        translationBuilder.add("nemos_inventory_sorting.itemFilter", "Фильтр предметов");
        translationBuilder.add("nemos_inventory_sorting.gui.inventory.itemFilter", "Фильтр...");
        translationBuilder.add("nemos_inventory_sorting.resourcePack.darkMode.name", "Тёмный режим");
        translationBuilder.add("nemos_inventory_sorting.resourcePack.darkMode.description", "Тёмные кнопки");
        translationBuilder.add("key.category.nemos_inventory_sorting.nemos_inventory_sorting", "Nemo's Inventory Sorting");
        translationBuilder.add("nemos_inventory_sorting.key.hoverDropAll", "Выбросить всё (под курсором)");
        translationBuilder.add("nemos_inventory_sorting.key.hoverMoveAll", "Переместить всё (под курсором)");
        translationBuilder.add("nemos_inventory_sorting.key.hoverMoveSame", "Переместить такие же предметы (под курсором)");
        translationBuilder.add("nemos_inventory_sorting.key.hoverSort", "Сортировать (под курсором)");
        translationBuilder.add("nemos_inventory_sorting.key.dropAll", "Выбросить всё (контейнер)");
        translationBuilder.add("nemos_inventory_sorting.key.dropAllInventory", "Выбросить всё (инвентарь)");
        translationBuilder.add("nemos_inventory_sorting.key.moveAll", "Переместить всё (контейнер)");
        translationBuilder.add("nemos_inventory_sorting.key.moveAllInventory", "Переместить всё (инвентарь)");
        translationBuilder.add("nemos_inventory_sorting.key.moveSame", "Переместить такие же предметы (контейнер)");
        translationBuilder.add("nemos_inventory_sorting.key.moveSameInventory", "Переместить такие же предметы (инвентарь)");
        translationBuilder.add("nemos_inventory_sorting.key.sort", "Сортировать (контейнер)");
        translationBuilder.add("nemos_inventory_sorting.key.sortInventory", "Сортировать (инвентарь)");
        translationBuilder.add("nemos_inventory_sorting.key.toggleFilterPersistence", "Переключить сохранение фильтра");
        translationBuilder.add("nemos_inventory_sorting.key.quickSearch", "Быстрый поиск (Ctrl/Command + назначенная клавиша)");
    }
}
