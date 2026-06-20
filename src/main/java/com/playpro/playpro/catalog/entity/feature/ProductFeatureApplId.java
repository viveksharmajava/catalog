package com.playpro.playpro.catalog.entity.feature;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Embeddable
public class ProductFeatureApplId implements Serializable {

    @Column(name = "product_id", length = 20)
    private String productId;

    @Column(name = "product_feature_id", length = 20)
    private String productFeatureId;

    @Column(name = "from_date")
    private LocalDateTime fromDate;

    public ProductFeatureApplId() {
    }

    public ProductFeatureApplId(String productId, String productFeatureId, LocalDateTime fromDate) {
        this.productId = productId;
        this.productFeatureId = productFeatureId;
        this.fromDate = fromDate;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductFeatureId() {
        return productFeatureId;
    }

    public void setProductFeatureId(String productFeatureId) {
        this.productFeatureId = productFeatureId;
    }

    public LocalDateTime getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDateTime fromDate) {
        this.fromDate = fromDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProductFeatureApplId that = (ProductFeatureApplId) o;
        return Objects.equals(productId, that.productId)
                && Objects.equals(productFeatureId, that.productFeatureId)
                && Objects.equals(fromDate, that.fromDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, productFeatureId, fromDate);
    }
}
