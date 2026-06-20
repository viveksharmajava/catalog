package com.playpro.playpro.catalog.entity.product;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Embeddable
public class ProductAssocId implements Serializable {

    @Column(name = "product_id", length = 20)
    private String productId;

    @Column(name = "product_id_to", length = 20)
    private String productIdTo;

    @Column(name = "product_assoc_type_id", length = 20)
    private String productAssocTypeId;

    @Column(name = "from_date")
    private LocalDateTime fromDate;

    public ProductAssocId() {
    }

    public ProductAssocId(String productId, String productIdTo, String productAssocTypeId, LocalDateTime fromDate) {
        this.productId = productId;
        this.productIdTo = productIdTo;
        this.productAssocTypeId = productAssocTypeId;
        this.fromDate = fromDate;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductIdTo() {
        return productIdTo;
    }

    public void setProductIdTo(String productIdTo) {
        this.productIdTo = productIdTo;
    }

    public String getProductAssocTypeId() {
        return productAssocTypeId;
    }

    public void setProductAssocTypeId(String productAssocTypeId) {
        this.productAssocTypeId = productAssocTypeId;
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
        ProductAssocId that = (ProductAssocId) o;
        return Objects.equals(productId, that.productId)
                && Objects.equals(productIdTo, that.productIdTo)
                && Objects.equals(productAssocTypeId, that.productAssocTypeId)
                && Objects.equals(fromDate, that.fromDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, productIdTo, productAssocTypeId, fromDate);
    }
}
