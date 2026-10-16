package com.nemonotfound.nemos.inventory.sorting.datagen.language;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class HongKongChineseProvider extends FabricLanguageProvider {
    public HongKongChineseProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, "zh_hk", registries);
    }

    @Override
    public void generateTranslations(HolderLookup.@NonNull Provider registries, TranslationBuilder translationBuilder) {
        translationBuilder.add("nemos_inventory_sorting.gui.sortButton", "整理");
        translationBuilder.add("nemos_inventory_sorting.gui.dropAll", "全部丟棄");
        translationBuilder.add("nemos_inventory_sorting.gui.dropAllShift", "全部丟棄（包含快捷欄）");
        translationBuilder.add("nemos_inventory_sorting.gui.moveSame", "移動相同物品");
        translationBuilder.add("nemos_inventory_sorting.gui.moveSameShift", "移動相同物品（包含快捷欄）");
        translationBuilder.add("nemos_inventory_sorting.gui.moveAll", "全部移動");
        translationBuilder.add("nemos_inventory_sorting.gui.moveAllShift", "全部移動（包含快捷欄）");
        translationBuilder.add("nemos_inventory_sorting.gui.includeHotbar", "（包含快捷欄）");
        translationBuilder.add("nemos_inventory_sorting.gui.excludeHotbar", "（不包含快捷欄）");
        translationBuilder.add("nemos_inventory_sorting.gui.toggleFilterPersistence", "切換篩選器持續生效");
        translationBuilder.add("nemos_inventory_sorting.gui.toggleFilterPersistence.toggleOn", "啟用篩選器持續生效");
        translationBuilder.add("nemos_inventory_sorting.gui.toggleFilterPersistence.toggleOff", "停用篩選器持續生效");
        translationBuilder.add("nemos_inventory_sorting.itemFilter", "篩選物品");
        translationBuilder.add("nemos_inventory_sorting.gui.inventory.itemFilter", "篩選⋯⋯");
        translationBuilder.add("nemos_inventory_sorting.resourcePack.darkMode.name", "深色模式");
        translationBuilder.add("nemos_inventory_sorting.resourcePack.darkMode.description", "深色按鈕");
        translationBuilder.add("key.category.nemos_inventory_sorting.nemos_inventory_sorting", "Nemo's Inventory Sorting");
        translationBuilder.add("nemos_inventory_sorting.key.hoverDropAll", "全部丟棄（滑鼠懸停）");
        translationBuilder.add("nemos_inventory_sorting.key.hoverMoveAll", "全部移動（滑鼠懸停）");
        translationBuilder.add("nemos_inventory_sorting.key.hoverMoveSame", "移動相同物品（滑鼠懸停）");
        translationBuilder.add("nemos_inventory_sorting.key.hoverSort", "整理（滑鼠懸停）");
        translationBuilder.add("nemos_inventory_sorting.key.dropAll", "全部丟棄（容器）");
        translationBuilder.add("nemos_inventory_sorting.key.dropAllInventory", "全部丟棄（物品欄）");
        translationBuilder.add("nemos_inventory_sorting.key.moveAll", "全部移動（容器）");
        translationBuilder.add("nemos_inventory_sorting.key.moveAllInventory", "全部移動（物品欄）");
        translationBuilder.add("nemos_inventory_sorting.key.moveSame", "移動相同物品（容器）");
        translationBuilder.add("nemos_inventory_sorting.key.moveSameInventory", "移動相同物品（物品欄）");
        translationBuilder.add("nemos_inventory_sorting.key.sort", "整理（容器）");
        translationBuilder.add("nemos_inventory_sorting.key.sortInventory", "整理（物品欄）");
        translationBuilder.add("nemos_inventory_sorting.key.toggleFilterPersistence", "切換篩選器持續生效");
        translationBuilder.add("nemos_inventory_sorting.key.quickSearch", "快速搜尋（Ctrl/Command + 按鍵綁定）");
    }
}
