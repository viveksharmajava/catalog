package com.playpro.playpro.catalog.entity.category;

import com.playpro.playpro.catalog.entity.AuditableEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "product_category")
public class ProductCategory extends AuditableEntity {

    @Id
    @Column(name = "product_category_id", length = 20)
    private String productCategoryId;

    @Column(name = "product_category_type_id", length = 20, nullable = false)
    private String productCategoryTypeId;

    @Column(name = "primary_parent_category_id", length = 20)
    private String primaryParentCategoryId;

    @Column(name = "category_name", length = 100, nullable = false)
    private String categoryName;

    @Column(length = 255)
    private String description;

    @Column(name = "long_description", columnDefinition = "CLOB")
    private String longDescription;

    @Column(name = "category_image_url", length = 2000)
    private String categoryImageUrl;

    @Column(name = "show_in_select", length = 1)
    private String showInSelect = "Y";

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

    public String getShowInSelect() {
        return showInSelect;
    }

    public void setShowInSelect(String showInSelect) {
        this.showInSelect = showInSelect;
    }
}
