package com.nemonotfound.nemos.inventory.sorting.config.model;

public class FilterConfig {

    private boolean isFilterPersistent = false;
    private String filter = "";

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
