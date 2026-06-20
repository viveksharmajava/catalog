package com.playpro.playpro.catalog.entity.product;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "product_type")
public class ProductType {

    @Id
    @Column(name = "product_type_id", length = 20)
    private String productTypeId;

    @Column(name = "parent_type_id", length = 20)
    private String parentTypeId;

    @Column(name = "is_physical", length = 1)
    private String isPhysical;

    @Column(name = "is_digital", length = 1)
    private String isDigital;

    @Column(length = 255)
    private String description;

    public String getProductTypeId() {
        return productTypeId;
    }

    public void setProductTypeId(String productTypeId) {
        this.productTypeId = productTypeId;
    }

    public String getParentTypeId() {
        return parentTypeId;
    }

    public void setParentTypeId(String parentTypeId) {
        this.parentTypeId = parentTypeId;
    }

    public String getIsPhysical() {
        return isPhysical;
    }

    public void setIsPhysical(String isPhysical) {
        this.isPhysical = isPhysical;
    }

    public String getIsDigital() {
        return isDigital;
    }

    public void setIsDigital(String isDigital) {
        this.isDigital = isDigital;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
