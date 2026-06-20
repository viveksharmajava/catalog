package com.playpro.playpro.catalog.dto;

public class ProductFindRequest {

    private FieldSearchCriteria productId;
    private FieldSearchCriteria productName;
    private FieldSearchCriteria internalName;
    /** When true, list all products if no field criteria (OFBiz noConditionFind). */
    private boolean noConditionFind = true;
    private int page = 0;
    private int size = 20;
    private String sortField = "productId";
    private String sortDirection = "asc";

    public FieldSearchCriteria getProductId() {
        return productId;
    }

    public void setProductId(FieldSearchCriteria productId) {
        this.productId = productId;
    }

    public FieldSearchCriteria getProductName() {
        return productName;
    }

    public void setProductName(FieldSearchCriteria productName) {
        this.productName = productName;
    }

    public FieldSearchCriteria getInternalName() {
        return internalName;
    }

    public void setInternalName(FieldSearchCriteria internalName) {
        this.internalName = internalName;
    }

    public boolean isNoConditionFind() {
        return noConditionFind;
    }

    public void setNoConditionFind(boolean noConditionFind) {
        this.noConditionFind = noConditionFind;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }

    public boolean hasFieldConditions() {
        return criteriaPresent(productId) || criteriaPresent(productName) || criteriaPresent(internalName);
    }

    private boolean criteriaPresent(FieldSearchCriteria criteria) {
        return criteria != null && criteria.hasCondition();
    }
}
