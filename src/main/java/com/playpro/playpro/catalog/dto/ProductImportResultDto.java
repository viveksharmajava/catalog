package com.playpro.playpro.catalog.dto;

import java.util.ArrayList;
import java.util.List;

public class ProductImportResultDto {

    private int totalRows;
    private int created;
    private int updated;
    private int failed;
    private List<ProductImportRowErrorDto> errors = new ArrayList<>();

    public int getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }

    public int getCreated() {
        return created;
    }

    public void setCreated(int created) {
        this.created = created;
    }

    public int getUpdated() {
        return updated;
    }

    public void setUpdated(int updated) {
        this.updated = updated;
    }

    public int getFailed() {
        return failed;
    }

    public void setFailed(int failed) {
        this.failed = failed;
    }

    public List<ProductImportRowErrorDto> getErrors() {
        return errors;
    }

    public void setErrors(List<ProductImportRowErrorDto> errors) {
        this.errors = errors;
    }
}
