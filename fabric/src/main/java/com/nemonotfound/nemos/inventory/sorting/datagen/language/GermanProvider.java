package com.nemonotfound.nemos.inventory.sorting.datagen.language;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class GermanProvider extends FabricLanguageProvider {
    public GermanProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, "de_de", registries);
    }

    @Override
    public void generateTranslations(HolderLookup.@NonNull Provider registries, TranslationBuilder translationBuilder) {
        translationBuilder.add("nemos_inventory_sorting.gui.sortButton", "Sortieren");
        translationBuilder.add("nemos_inventory_sorting.gui.dropAll", "Alles wegwerfen");
        translationBuilder.add("nemos_inventory_sorting.gui.dropAllShift", "Alles wegwerfen (inklusive Schnellzugriffsleiste)");
        translationBuilder.add("nemos_inventory_sorting.gui.moveSame", "Gleiche Gegenstände verschieben");
        translationBuilder.add("nemos_inventory_sorting.gui.moveSameShift", "Gleiche Gegenstände verschieben (inklusive Schnellzugriffsleiste)");
        translationBuilder.add("nemos_inventory_sorting.gui.moveAll", "Alles verschieben");
        translationBuilder.add("nemos_inventory_sorting.gui.moveAllShift", "Alles verschieben (inklusive Schnellzugriffsleiste)");
        translationBuilder.add("nemos_inventory_sorting.gui.includeHotbar", "(inklusive Schnellzugriffsleiste)");
        translationBuilder.add("nemos_inventory_sorting.gui.excludeHotbar", "(ohne Schnellzugriffsleiste)");
        translationBuilder.add("nemos_inventory_sorting.gui.toggleFilterPersistence", "Filterbeibehaltung umschalten");
        translationBuilder.add("nemos_inventory_sorting.gui.toggleFilterPersistence.toggleOn", "Filterbeibehaltung aktivieren");
        translationBuilder.add("nemos_inventory_sorting.gui.toggleFilterPersistence.toggleOff", "Filterbeibehaltung deaktivieren");
        translationBuilder.add("nemos_inventory_sorting.itemFilter", "Gegenstand filtern");
        translationBuilder.add("nemos_inventory_sorting.gui.inventory.itemFilter", "Filtern...");
        translationBuilder.add("nemos_inventory_sorting.resourcePack.darkMode.name", "Dunkelmodus");
        translationBuilder.add("nemos_inventory_sorting.resourcePack.darkMode.description", "Dunkle Schaltflächen");
        translationBuilder.add("key.category.nemos_inventory_sorting.nemos_inventory_sorting", "Nemo's Inventory Sorting");
        translationBuilder.add("nemos_inventory_sorting.key.hoverDropAll", "Alles wegwerfen (unter Mauszeiger)");
        translationBuilder.add("nemos_inventory_sorting.key.hoverMoveAll", "Alles verschieben (unter Mauszeiger)");
        translationBuilder.add("nemos_inventory_sorting.key.hoverMoveSame", "Gleiche Gegenstände verschieben (unter Mauszeiger)");
        translationBuilder.add("nemos_inventory_sorting.key.hoverSort", "Sortieren (unter Mauszeiger)");
        translationBuilder.add("nemos_inventory_sorting.key.dropAll", "Alles wegwerfen (Behälter)");
        translationBuilder.add("nemos_inventory_sorting.key.dropAllInventory", "Alles wegwerfen (Inventar)");
        translationBuilder.add("nemos_inventory_sorting.key.moveAll", "Alles verschieben (Behälter)");
        translationBuilder.add("nemos_inventory_sorting.key.moveAllInventory", "Alles verschieben (Inventar)");
        translationBuilder.add("nemos_inventory_sorting.key.moveSame", "Gleiche Gegenstände verschieben (Behälter)");
        translationBuilder.add("nemos_inventory_sorting.key.moveSameInventory", "Gleiche Gegenstände verschieben (Inventar)");
        translationBuilder.add("nemos_inventory_sorting.key.sort", "Sortieren (Behälter)");
        translationBuilder.add("nemos_inventory_sorting.key.sortInventory", "Sortieren (Inventar)");
        translationBuilder.add("nemos_inventory_sorting.key.toggleFilterPersistence", "Filterbeibehaltung umschalten");
        translationBuilder.add("nemos_inventory_sorting.key.quickSearch", "Schnellsuche (Strg/Befehl + Tastenbelegung)");
    }
}
