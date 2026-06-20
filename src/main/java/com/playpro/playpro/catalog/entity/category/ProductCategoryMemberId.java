package com.playpro.playpro.catalog.entity.category;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Embeddable
public class ProductCategoryMemberId implements Serializable {

    @Column(name = "product_category_id", length = 20)
    private String productCategoryId;

    @Column(name = "product_id", length = 20)
    private String productId;

    @Column(name = "from_date")
    private LocalDateTime fromDate;

    public ProductCategoryMemberId() {
    }

    public ProductCategoryMemberId(String productCategoryId, String productId, LocalDateTime fromDate) {
        this.productCategoryId = productCategoryId;
        this.productId = productId;
        this.fromDate = fromDate;
    }

    public String getProductCategoryId() {
        return productCategoryId;
    }

    public void setProductCategoryId(String productCategoryId) {
        this.productCategoryId = productCategoryId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
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
        ProductCategoryMemberId that = (ProductCategoryMemberId) o;
        return Objects.equals(productCategoryId, that.productCategoryId)
                && Objects.equals(productId, that.productId)
                && Objects.equals(fromDate, that.fromDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productCategoryId, productId, fromDate);
    }
}
