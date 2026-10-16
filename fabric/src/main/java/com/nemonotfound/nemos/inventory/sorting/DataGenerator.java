package com.nemonotfound.nemos.inventory.sorting;

import com.nemonotfound.nemos.inventory.sorting.datagen.language.EnglishProvider;
import com.nemonotfound.nemos.inventory.sorting.datagen.language.GermanProvider;
import com.nemonotfound.nemos.inventory.sorting.datagen.language.HongKongChineseProvider;
import com.nemonotfound.nemos.inventory.sorting.datagen.language.LiteraryChineseProvider;
import com.nemonotfound.nemos.inventory.sorting.datagen.language.RussianProvider;
import com.nemonotfound.nemos.inventory.sorting.datagen.language.SimplifiedChineseProvider;
import com.nemonotfound.nemos.inventory.sorting.datagen.language.TurkishProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class DataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        pack.addProvider(EnglishProvider::new);
        pack.addProvider(GermanProvider::new);
        pack.addProvider(RussianProvider::new);
        pack.addProvider(TurkishProvider::new);
        pack.addProvider(SimplifiedChineseProvider::new);
        pack.addProvider(HongKongChineseProvider::new);
        pack.addProvider(LiteraryChineseProvider::new);
    }
}
