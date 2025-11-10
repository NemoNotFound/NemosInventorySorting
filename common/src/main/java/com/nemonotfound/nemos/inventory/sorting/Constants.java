package com.nemonotfound.nemos.inventory.sorting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants {

	public static final String MOD_ID = "nemos_inventory_sorting";
	public static final String IRON_CHEST_MOD_ID = "ironchest";
	public static final String NEMOS_BACKPACKS_MOD_ID = "nemos_backpacks";
	public static final String REINFORCED_CHESTS_MOD_ID = "reinfchest";
	public static final String REINFORCED_BARRELS_MOD_ID = "reinfbarrel";
	public static final String REINFORCED_SHULKER_BOXES_MOD_ID = "reinfshulker";
	public static final Logger LOG = LoggerFactory.getLogger(MOD_ID);

	public static final int MAX_MERGING_CYCLES = 1000;
	public static final int MAX_SORTING_CYCLES = 1000;
}