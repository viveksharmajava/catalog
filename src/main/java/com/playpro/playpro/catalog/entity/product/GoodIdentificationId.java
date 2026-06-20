package com.playpro.playpro.catalog.entity.product;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class GoodIdentificationId implements Serializable {

    @Column(name = "good_identification_type_id", length = 20)
    private String goodIdentificationTypeId;

    @Column(name = "product_id", length = 20)
    private String productId;

    public GoodIdentificationId() {
    }

    public GoodIdentificationId(String goodIdentificationTypeId, String productId) {
        this.goodIdentificationTypeId = goodIdentificationTypeId;
        this.productId = productId;
    }

    public String getGoodIdentificationTypeId() {
        return goodIdentificationTypeId;
    }

    public void setGoodIdentificationTypeId(String goodIdentificationTypeId) {
        this.goodIdentificationTypeId = goodIdentificationTypeId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GoodIdentificationId that = (GoodIdentificationId) o;
        return Objects.equals(goodIdentificationTypeId, that.goodIdentificationTypeId)
                && Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(goodIdentificationTypeId, productId);
    }
}
