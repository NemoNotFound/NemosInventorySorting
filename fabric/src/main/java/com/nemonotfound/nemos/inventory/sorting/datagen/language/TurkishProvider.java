package com.nemonotfound.nemos.inventory.sorting.datagen.language;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class TurkishProvider extends FabricLanguageProvider {
    public TurkishProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, "tr_tr", registries);
    }

    @Override
    public void generateTranslations(HolderLookup.@NonNull Provider registries, TranslationBuilder translationBuilder) {
        translationBuilder.add("nemos_inventory_sorting.gui.sortButton", "Sırala");
        translationBuilder.add("nemos_inventory_sorting.gui.dropAll", "Tümünü At");
        translationBuilder.add("nemos_inventory_sorting.gui.dropAllShift", "Tümünü At (Hızlı Erişim Çubuğu Dahil)");
        translationBuilder.add("nemos_inventory_sorting.gui.moveSame", "Aynı Eşyaları Taşı");
        translationBuilder.add("nemos_inventory_sorting.gui.moveSameShift", "Aynı Eşyaları Taşı (Hızlı Erişim Çubuğu Dahil)");
        translationBuilder.add("nemos_inventory_sorting.gui.moveAll", "Tümünü Taşı");
        translationBuilder.add("nemos_inventory_sorting.gui.moveAllShift", "Tümünü Taşı (Hızlı Erişim Çubuğu Dahil)");
        translationBuilder.add("nemos_inventory_sorting.gui.includeHotbar", "(Hızlı Erişim Çubuğu Dahil)");
        translationBuilder.add("nemos_inventory_sorting.gui.excludeHotbar", "(Hızlı Erişim Çubuğu Hariç)");
        translationBuilder.add("nemos_inventory_sorting.gui.toggleFilterPersistence", "Filtre Kalıcılığını Değiştir");
        translationBuilder.add("nemos_inventory_sorting.gui.toggleFilterPersistence.toggleOn", "Filtre Kalıcılığını Etkinleştir");
        translationBuilder.add("nemos_inventory_sorting.gui.toggleFilterPersistence.toggleOff", "Filtre Kalıcılığını Devre Dışı Bırak");
        translationBuilder.add("nemos_inventory_sorting.itemFilter", "Eşyayı Filtrele");
        translationBuilder.add("nemos_inventory_sorting.gui.inventory.itemFilter", "Filtrele...");
        translationBuilder.add("nemos_inventory_sorting.resourcePack.darkMode.name", "Karanlık Mod");
        translationBuilder.add("nemos_inventory_sorting.resourcePack.darkMode.description", "Karanlık düğmeler");
        translationBuilder.add("key.category.nemos_inventory_sorting.nemos_inventory_sorting", "Nemo's Inventory Sorting");
        translationBuilder.add("nemos_inventory_sorting.key.hoverDropAll", "Tümünü At (Üzerine Gelinen)");
        translationBuilder.add("nemos_inventory_sorting.key.hoverMoveAll", "Tümünü Taşı (Üzerine Gelinen)");
        translationBuilder.add("nemos_inventory_sorting.key.hoverMoveSame", "Aynı Eşyaları Taşı (Üzerine Gelinen)");
        translationBuilder.add("nemos_inventory_sorting.key.hoverSort", "Sırala (Üzerine Gelinen)");
        translationBuilder.add("nemos_inventory_sorting.key.dropAll", "Tümünü At (Kapsayıcı)");
        translationBuilder.add("nemos_inventory_sorting.key.dropAllInventory", "Tümünü At (Envanter)");
        translationBuilder.add("nemos_inventory_sorting.key.moveAll", "Tümünü Taşı (Kapsayıcı)");
        translationBuilder.add("nemos_inventory_sorting.key.moveAllInventory", "Tümünü Taşı (Envanter)");
        translationBuilder.add("nemos_inventory_sorting.key.moveSame", "Aynı Eşyaları Taşı (Kapsayıcı)");
        translationBuilder.add("nemos_inventory_sorting.key.moveSameInventory", "Aynı Eşyaları Taşı (Envanter)");
        translationBuilder.add("nemos_inventory_sorting.key.sort", "Sırala (Kapsayıcı)");
        translationBuilder.add("nemos_inventory_sorting.key.sortInventory", "Sırala (Envanter)");
        translationBuilder.add("nemos_inventory_sorting.key.toggleFilterPersistence", "Filtre Kalıcılığını Değiştir");
        translationBuilder.add("nemos_inventory_sorting.key.quickSearch", "Hızlı Arama (Ctrl/Command + Tuş Ataması)");
    }
}
