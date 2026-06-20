package com.playpro.playpro.catalog.entity.category;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Embeddable
public class ProductCategoryRollupId implements Serializable {

    @Column(name = "product_category_id", length = 20)
    private String productCategoryId;

    @Column(name = "parent_product_category_id", length = 20)
    private String parentProductCategoryId;

    @Column(name = "from_date")
    private LocalDateTime fromDate;

    public ProductCategoryRollupId() {
    }

    public ProductCategoryRollupId(String productCategoryId, String parentProductCategoryId, LocalDateTime fromDate) {
        this.productCategoryId = productCategoryId;
        this.parentProductCategoryId = parentProductCategoryId;
        this.fromDate = fromDate;
    }

    public String getProductCategoryId() {
        return productCategoryId;
    }

    public void setProductCategoryId(String productCategoryId) {
        this.productCategoryId = productCategoryId;
    }

    public String getParentProductCategoryId() {
        return parentProductCategoryId;
    }

    public void setParentProductCategoryId(String parentProductCategoryId) {
        this.parentProductCategoryId = parentProductCategoryId;
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
        ProductCategoryRollupId that = (ProductCategoryRollupId) o;
        return Objects.equals(productCategoryId, that.productCategoryId)
                && Objects.equals(parentProductCategoryId, that.parentProductCategoryId)
                && Objects.equals(fromDate, that.fromDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productCategoryId, parentProductCategoryId, fromDate);
    }
}
