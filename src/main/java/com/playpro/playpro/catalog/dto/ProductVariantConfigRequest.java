package com.playpro.playpro.catalog.dto;

import java.util.ArrayList;
import java.util.List;

/** Request body to save which store variant types/values apply to a virtual product. */
public class ProductVariantConfigRequest {

    private String productStoreId;
    private List<TypeSelection> types = new ArrayList<>();

    public String getProductStoreId() {
        return productStoreId;
    }

    public void setProductStoreId(String productStoreId) {
        this.productStoreId = productStoreId;
    }

    public List<TypeSelection> getTypes() {
        return types;
    }

    public void setTypes(List<TypeSelection> types) {
        this.types = types;
    }

    public static class TypeSelection {
        private String variantTypeId;
        private Integer sequenceNum;
        private List<String> valueIds = new ArrayList<>();

        public String getVariantTypeId() {
            return variantTypeId;
        }

        public void setVariantTypeId(String variantTypeId) {
            this.variantTypeId = variantTypeId;
        }

        public Integer getSequenceNum() {
            return sequenceNum;
        }

        public void setSequenceNum(Integer sequenceNum) {
            this.sequenceNum = sequenceNum;
        }

        public List<String> getValueIds() {
            return valueIds;
        }

        public void setValueIds(List<String> valueIds) {
            this.valueIds = valueIds;
        }
    }
}
