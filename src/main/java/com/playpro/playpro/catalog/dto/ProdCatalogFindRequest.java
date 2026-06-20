package com.playpro.playpro.catalog.dto;

public class ProdCatalogFindRequest {

    private FieldSearchCriteria prodCatalogId;
    private FieldSearchCriteria catalogName;
    private boolean noConditionFind = true;
    private int page = 0;
    private int size = 20;
    private String sortField = "catalogName";
    private String sortDirection = "asc";

    public FieldSearchCriteria getProdCatalogId() {
        return prodCatalogId;
    }

    public void setProdCatalogId(FieldSearchCriteria prodCatalogId) {
        this.prodCatalogId = prodCatalogId;
    }

    public FieldSearchCriteria getCatalogName() {
        return catalogName;
    }

    public void setCatalogName(FieldSearchCriteria catalogName) {
        this.catalogName = catalogName;
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
        return criteriaPresent(prodCatalogId) || criteriaPresent(catalogName);
    }

    private boolean criteriaPresent(FieldSearchCriteria criteria) {
        return criteria != null && criteria.hasCondition();
    }
}
