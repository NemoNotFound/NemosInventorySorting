package com.nemonotfound.nemos.inventory.sorting.datagen.language;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class LiteraryChineseProvider extends FabricLanguageProvider {
    public LiteraryChineseProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, "lzh", registries);
    }

    @Override
    public void generateTranslations(HolderLookup.@NonNull Provider registries, TranslationBuilder translationBuilder) {
        translationBuilder.add("nemos_inventory_sorting.gui.sortButton", "理");
        translationBuilder.add("nemos_inventory_sorting.gui.dropAll", "皆棄");
        translationBuilder.add("nemos_inventory_sorting.gui.dropAllShift", "皆棄（兼捷槽）");
        translationBuilder.add("nemos_inventory_sorting.gui.moveSame", "移同物");
        translationBuilder.add("nemos_inventory_sorting.gui.moveSameShift", "移同物（兼捷槽）");
        translationBuilder.add("nemos_inventory_sorting.gui.moveAll", "皆移");
        translationBuilder.add("nemos_inventory_sorting.gui.moveAllShift", "皆移（兼捷槽）");
        translationBuilder.add("nemos_inventory_sorting.gui.includeHotbar", "（兼捷槽）");
        translationBuilder.add("nemos_inventory_sorting.gui.excludeHotbar", "（不兼捷槽）");
        translationBuilder.add("nemos_inventory_sorting.gui.toggleFilterPersistence", "切篩之恆存");
        translationBuilder.add("nemos_inventory_sorting.gui.toggleFilterPersistence.toggleOn", "啟篩之恆存");
        translationBuilder.add("nemos_inventory_sorting.gui.toggleFilterPersistence.toggleOff", "棄篩之恆存");
        translationBuilder.add("nemos_inventory_sorting.itemFilter", "篩物");
        translationBuilder.add("nemos_inventory_sorting.gui.inventory.itemFilter", "篩...");
        translationBuilder.add("nemos_inventory_sorting.resourcePack.darkMode.name", "暗式");
        translationBuilder.add("nemos_inventory_sorting.resourcePack.darkMode.description", "暗鈕");
        translationBuilder.add("key.category.nemos_inventory_sorting.nemos_inventory_sorting", "Nemo's Inventory Sorting");
        translationBuilder.add("nemos_inventory_sorting.key.hoverDropAll", "皆棄（所指）");
        translationBuilder.add("nemos_inventory_sorting.key.hoverMoveAll", "皆移（所指）");
        translationBuilder.add("nemos_inventory_sorting.key.hoverMoveSame", "移同物（所指）");
        translationBuilder.add("nemos_inventory_sorting.key.hoverSort", "理（所指）");
        translationBuilder.add("nemos_inventory_sorting.key.dropAll", "皆棄（箱）");
        translationBuilder.add("nemos_inventory_sorting.key.dropAllInventory", "皆棄（行囊）");
        translationBuilder.add("nemos_inventory_sorting.key.moveAll", "皆移（箱）");
        translationBuilder.add("nemos_inventory_sorting.key.moveAllInventory", "皆移（行囊）");
        translationBuilder.add("nemos_inventory_sorting.key.moveSame", "移同物（箱）");
        translationBuilder.add("nemos_inventory_sorting.key.moveSameInventory", "移同物（行囊）");
        translationBuilder.add("nemos_inventory_sorting.key.sort", "理（箱）");
        translationBuilder.add("nemos_inventory_sorting.key.sortInventory", "理（行囊）");
        translationBuilder.add("nemos_inventory_sorting.key.toggleFilterPersistence", "切篩之恆存");
        translationBuilder.add("nemos_inventory_sorting.key.quickSearch", "速尋（Ctrl/Command 兼鍵）");
    }
}
