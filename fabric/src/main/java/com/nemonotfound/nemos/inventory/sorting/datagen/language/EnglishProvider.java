package com.nemonotfound.nemos.inventory.sorting.datagen.language;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class EnglishProvider extends FabricLanguageProvider {
    public EnglishProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    public void generateTranslations(HolderLookup.@NonNull Provider registries, TranslationBuilder translationBuilder) {
        translationBuilder.add("nemos_inventory_sorting.gui.sortButton", "Sort");
        translationBuilder.add("nemos_inventory_sorting.gui.dropAll", "Drop All");
        translationBuilder.add("nemos_inventory_sorting.gui.dropAllShift", "Drop All (Including Hotbar)");
        translationBuilder.add("nemos_inventory_sorting.gui.moveSame", "Move Same");
        translationBuilder.add("nemos_inventory_sorting.gui.moveSameShift", "Move Same (Including Hotbar)");
        translationBuilder.add("nemos_inventory_sorting.gui.moveAll", "Move All");
        translationBuilder.add("nemos_inventory_sorting.gui.moveAllShift", "Move All (Including Hotbar)");
        translationBuilder.add("nemos_inventory_sorting.gui.includeHotbar", "(Including Hotbar)");
        translationBuilder.add("nemos_inventory_sorting.gui.excludeHotbar", "(Excluding Hotbar)");
        translationBuilder.add("nemos_inventory_sorting.gui.toggleFilterPersistence", "Toggle Filter Persistence");
        translationBuilder.add("nemos_inventory_sorting.gui.toggleFilterPersistence.toggleOn", "Enable Filter Persistance");
        translationBuilder.add("nemos_inventory_sorting.gui.toggleFilterPersistence.toggleOff", "Disable Filter Persistance");
        translationBuilder.add("nemos_inventory_sorting.itemFilter", "Filter Item");
        translationBuilder.add("nemos_inventory_sorting.gui.inventory.itemFilter", "Filter...");
        translationBuilder.add("nemos_inventory_sorting.resourcePack.darkMode.name", "Dark Mode");
        translationBuilder.add("nemos_inventory_sorting.resourcePack.darkMode.description", "Dark buttons");
        translationBuilder.add("key.category.nemos_inventory_sorting.nemos_inventory_sorting", "Nemo's Inventory Sorting");
        translationBuilder.add("nemos_inventory_sorting.key.hoverDropAll", "Drop All (Hovered)");
        translationBuilder.add("nemos_inventory_sorting.key.hoverMoveAll", "Move All (Hovered)");
        translationBuilder.add("nemos_inventory_sorting.key.hoverMoveSame", "Move Same (Hovered)");
        translationBuilder.add("nemos_inventory_sorting.key.hoverSort", "Sort (Hovered)");
        translationBuilder.add("nemos_inventory_sorting.key.dropAll", "Drop All (Container)");
        translationBuilder.add("nemos_inventory_sorting.key.dropAllInventory", "Drop All (Inventory)");
        translationBuilder.add("nemos_inventory_sorting.key.moveAll", "Move All (Container)");
        translationBuilder.add("nemos_inventory_sorting.key.moveAllInventory", "Move All (Inventory)");
        translationBuilder.add("nemos_inventory_sorting.key.moveSame", "Move Same (Container)");
        translationBuilder.add("nemos_inventory_sorting.key.moveSameInventory", "Move Same (Inventory)");
        translationBuilder.add("nemos_inventory_sorting.key.sort", "Sort (Container)");
        translationBuilder.add("nemos_inventory_sorting.key.sortInventory", "Sort (Inventory)");
        translationBuilder.add("nemos_inventory_sorting.key.toggleFilterPersistence", "Toggle Filter Persistence");
        translationBuilder.add("nemos_inventory_sorting.key.quickSearch", "Quick Search (Ctrl/Command + Key Bind)");
    }
}
