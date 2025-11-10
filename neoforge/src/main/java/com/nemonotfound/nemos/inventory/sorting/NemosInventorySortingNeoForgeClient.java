package com.nemonotfound.nemos.inventory.sorting;


import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(value = Constants.MOD_ID, dist = Dist.CLIENT)
public class NemosInventorySortingNeoForgeClient {

    public NemosInventorySortingNeoForgeClient() {
        NemosInventorySortingClientCommon.init();
    }
}