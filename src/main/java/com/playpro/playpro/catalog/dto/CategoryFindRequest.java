package com.playpro.playpro.catalog.dto;

public class CategoryFindRequest {

    private FieldSearchCriteria productCategoryId;
    private FieldSearchCriteria categoryName;
    private boolean noConditionFind = true;
    private int page = 0;
    private int size = 20;
    private String sortField = "categoryName";
    private String sortDirection = "asc";

    public FieldSearchCriteria getProductCategoryId() {
        return productCategoryId;
    }

    public void setProductCategoryId(FieldSearchCriteria productCategoryId) {
        this.productCategoryId = productCategoryId;
    }

    public FieldSearchCriteria getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(FieldSearchCriteria categoryName) {
        this.categoryName = categoryName;
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
        return criteriaPresent(productCategoryId) || criteriaPresent(categoryName);
    }

    private boolean criteriaPresent(FieldSearchCriteria criteria) {
        return criteria != null && criteria.hasCondition();
    }
}
