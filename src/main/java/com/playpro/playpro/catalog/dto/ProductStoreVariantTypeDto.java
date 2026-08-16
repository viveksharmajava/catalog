package com.playpro.playpro.catalog.dto;

import java.util.ArrayList;
import java.util.List;

public class ProductStoreVariantTypeDto {

    private String variantTypeId;
    private String productStoreId;
    private String name;
    private String code;
    private String description;
    private Integer sequenceNum;
    private boolean enabled;
    private List<ProductStoreVariantValueDto> values = new ArrayList<>();

    public String getVariantTypeId() {
        return variantTypeId;
    }

    public void setVariantTypeId(String variantTypeId) {
        this.variantTypeId = variantTypeId;
    }

    public String getProductStoreId() {
        return productStoreId;
    }

    public void setProductStoreId(String productStoreId) {
        this.productStoreId = productStoreId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSequenceNum() {
        return sequenceNum;
    }

    public void setSequenceNum(Integer sequenceNum) {
        this.sequenceNum = sequenceNum;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<ProductStoreVariantValueDto> getValues() {
        return values;
    }

    public void setValues(List<ProductStoreVariantValueDto> values) {
        this.values = values;
    }
}
