package com.playpro.playpro.catalog.dto;

public class ProductImportRowErrorDto {

    private int rowNumber;
    private String productId;
    private String sku;
    private String message;

    public ProductImportRowErrorDto() {
    }

    public ProductImportRowErrorDto(int rowNumber, String productId, String sku, String message) {
        this.rowNumber = rowNumber;
        this.productId = productId;
        this.sku = sku;
        this.message = message;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
