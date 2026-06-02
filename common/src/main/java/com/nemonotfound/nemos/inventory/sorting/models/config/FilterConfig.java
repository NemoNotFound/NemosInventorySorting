package com.nemonotfound.nemos.inventory.sorting.models.config;

public class FilterConfig {

    public static FilterConfig INSTANCE = new FilterConfig();

    private boolean isFilterPersistent = false;
    private String filter = "";

    private FilterConfig() {
    }

    public boolean isFilterPersistent() {
        return isFilterPersistent;
    }

    public void toggleFilterPersistence() {
        isFilterPersistent = !isFilterPersistent;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }
}
