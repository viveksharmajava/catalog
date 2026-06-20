package com.playpro.playpro.catalog.entity.product;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ProductAttributeId implements Serializable {

    @Column(name = "product_id", length = 20)
    private String productId;

    @Column(name = "attr_name", length = 60)
    private String attrName;

    public ProductAttributeId() {
    }

    public ProductAttributeId(String productId, String attrName) {
        this.productId = productId;
        this.attrName = attrName;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getAttrName() {
        return attrName;
    }

    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProductAttributeId that = (ProductAttributeId) o;
        return Objects.equals(productId, that.productId) && Objects.equals(attrName, that.attrName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, attrName);
    }
}
