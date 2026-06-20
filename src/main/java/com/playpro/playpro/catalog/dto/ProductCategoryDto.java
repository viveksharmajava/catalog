package com.playpro.playpro.catalog.dto;

import java.util.ArrayList;
import java.util.List;

public class ProductCategoryDto {

    private String productCategoryId;
    private String productCategoryTypeId;
    private String primaryParentCategoryId;
    private String categoryName;
    private String description;
    private String longDescription;
    private String categoryImageUrl;
    private Boolean showInSelect;
    private Long memberCount;
    private Long childCategoryCount;
    private List<ProductCategoryDto> children = new ArrayList<>();

    public String getProductCategoryId() {
        return productCategoryId;
    }

    public void setProductCategoryId(String productCategoryId) {
        this.productCategoryId = productCategoryId;
    }

    public String getProductCategoryTypeId() {
        return productCategoryTypeId;
    }

    public void setProductCategoryTypeId(String productCategoryTypeId) {
        this.productCategoryTypeId = productCategoryTypeId;
    }

    public String getPrimaryParentCategoryId() {
        return primaryParentCategoryId;
    }

    public void setPrimaryParentCategoryId(String primaryParentCategoryId) {
        this.primaryParentCategoryId = primaryParentCategoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public String getCategoryImageUrl() {
        return categoryImageUrl;
    }

    public void setCategoryImageUrl(String categoryImageUrl) {
        this.categoryImageUrl = categoryImageUrl;
    }

    public Boolean getShowInSelect() {
        return showInSelect;
    }

    public void setShowInSelect(Boolean showInSelect) {
        this.showInSelect = showInSelect;
    }

    public Long getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(Long memberCount) {
        this.memberCount = memberCount;
    }

    public Long getChildCategoryCount() {
        return childCategoryCount;
    }

    public void setChildCategoryCount(Long childCategoryCount) {
        this.childCategoryCount = childCategoryCount;
    }

    public List<ProductCategoryDto> getChildren() {
        return children;
    }

    public void setChildren(List<ProductCategoryDto> children) {
        this.children = children;
    }
}
