package com.playpro.playpro.catalog.dto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProductVariantConfigDto {

    private String productId;
    private String productStoreId;
    private boolean virtualProduct;
    private List<ProductVariantConfigTypeDto> types = new ArrayList<>();
    private List<ProductVariantGeneratedDto> generatedVariants = new ArrayList<>();

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductStoreId() {
        return productStoreId;
    }

    public void setProductStoreId(String productStoreId) {
        this.productStoreId = productStoreId;
    }

    public boolean isVirtualProduct() {
        return virtualProduct;
    }

    public void setVirtualProduct(boolean virtualProduct) {
        this.virtualProduct = virtualProduct;
    }

    public List<ProductVariantConfigTypeDto> getTypes() {
        return types;
    }

    public void setTypes(List<ProductVariantConfigTypeDto> types) {
        this.types = types;
    }

    public List<ProductVariantGeneratedDto> getGeneratedVariants() {
        return generatedVariants;
    }

    public void setGeneratedVariants(List<ProductVariantGeneratedDto> generatedVariants) {
        this.generatedVariants = generatedVariants;
    }

    public static class ProductVariantConfigTypeDto {
        private String variantTypeId;
        private String name;
        private String code;
        private Integer sequenceNum;
        private List<ProductVariantConfigValueDto> values = new ArrayList<>();
        private List<String> selectedValueIds = new ArrayList<>();

        public String getVariantTypeId() {
            return variantTypeId;
        }

        public void setVariantTypeId(String variantTypeId) {
            this.variantTypeId = variantTypeId;
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

        public Integer getSequenceNum() {
            return sequenceNum;
        }

        public void setSequenceNum(Integer sequenceNum) {
            this.sequenceNum = sequenceNum;
        }

        public List<ProductVariantConfigValueDto> getValues() {
            return values;
        }

        public void setValues(List<ProductVariantConfigValueDto> values) {
            this.values = values;
        }

        public List<String> getSelectedValueIds() {
            return selectedValueIds;
        }

        public void setSelectedValueIds(List<String> selectedValueIds) {
            this.selectedValueIds = selectedValueIds;
        }
    }

    public static class ProductVariantConfigValueDto {
        private String variantValueId;
        private String value;
        private String abbreviation;
        private Integer sequenceNum;
        private boolean enabled;
        private boolean selected;

        public String getVariantValueId() {
            return variantValueId;
        }

        public void setVariantValueId(String variantValueId) {
            this.variantValueId = variantValueId;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getAbbreviation() {
            return abbreviation;
        }

        public void setAbbreviation(String abbreviation) {
            this.abbreviation = abbreviation;
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

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }
    }

    public static class ProductVariantGeneratedDto {
        private String productId;
        private String productName;
        private Map<String, String> selections = new LinkedHashMap<>();
        private boolean existing;

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public Map<String, String> getSelections() {
            return selections;
        }

        public void setSelections(Map<String, String> selections) {
            this.selections = selections;
        }

        public boolean isExisting() {
            return existing;
        }

        public void setExisting(boolean existing) {
            this.existing = existing;
        }
    }
}
