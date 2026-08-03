package com.playpro.playpro.catalog.dto;

import java.util.ArrayList;
import java.util.List;

public class ProductImageImportResultDto {

    private int totalEntries;
    private int imported;
    private int skipped;
    private int failed;
    private List<ProductImageImportItemDto> items = new ArrayList<>();

    public int getTotalEntries() {
        return totalEntries;
    }

    public void setTotalEntries(int totalEntries) {
        this.totalEntries = totalEntries;
    }

    public int getImported() {
        return imported;
    }

    public void setImported(int imported) {
        this.imported = imported;
    }

    public int getSkipped() {
        return skipped;
    }

    public void setSkipped(int skipped) {
        this.skipped = skipped;
    }

    public int getFailed() {
        return failed;
    }

    public void setFailed(int failed) {
        this.failed = failed;
    }

    public List<ProductImageImportItemDto> getItems() {
        return items;
    }

    public void setItems(List<ProductImageImportItemDto> items) {
        this.items = items;
    }
}
