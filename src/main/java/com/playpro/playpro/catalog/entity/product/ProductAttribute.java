package com.playpro.playpro.catalog.entity.product;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "product_attribute")
public class ProductAttribute {

    @EmbeddedId
    private ProductAttributeId id;

    @Column(name = "attr_value", length = 255)
    private String attrValue;

    @Column(name = "attr_type", length = 255)
    private String attrType;

    @Column(name = "attr_description", length = 255)
    private String attrDescription;

    public ProductAttributeId getId() {
        return id;
    }

    public void setId(ProductAttributeId id) {
        this.id = id;
    }

    public String getAttrValue() {
        return attrValue;
    }

    public void setAttrValue(String attrValue) {
        this.attrValue = attrValue;
    }

    public String getAttrType() {
        return attrType;
    }

    public void setAttrType(String attrType) {
        this.attrType = attrType;
    }

    public String getAttrDescription() {
        return attrDescription;
    }

    public void setAttrDescription(String attrDescription) {
        this.attrDescription = attrDescription;
    }
}
