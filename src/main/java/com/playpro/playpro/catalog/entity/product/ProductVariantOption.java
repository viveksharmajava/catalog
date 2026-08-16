package com.playpro.playpro.catalog.entity.product;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "product_variant_option")
@IdClass(ProductVariantOption.Pk.class)
public class ProductVariantOption {

    @Id
    @Column(name = "product_id", length = 20)
    private String productId;

    @Id
    @Column(name = "variant_type_id", length = 40)
    private String variantTypeId;

    @Column(name = "product_store_id", length = 20, nullable = false)
    private String productStoreId;

    @Column(name = "sequence_num", nullable = false)
    private Integer sequenceNum = 0;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getVariantTypeId() {
        return variantTypeId;
    }

    public void setVariantTypeId(String variantTypeId) {
        this.variantTypeId = variantTypeId;
    }

    public String getProductStoreId() {
        return productStoreId;
    }

    public void setProductStoreId(String productStoreId) {
        this.productStoreId = productStoreId;
    }

    public Integer getSequenceNum() {
        return sequenceNum;
    }

    public void setSequenceNum(Integer sequenceNum) {
        this.sequenceNum = sequenceNum;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public static class Pk implements Serializable {
        private String productId;
        private String variantTypeId;

        public Pk() {
        }

        public Pk(String productId, String variantTypeId) {
            this.productId = productId;
            this.variantTypeId = variantTypeId;
        }

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public String getVariantTypeId() {
            return variantTypeId;
        }

        public void setVariantTypeId(String variantTypeId) {
            this.variantTypeId = variantTypeId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Pk)) return false;
            Pk pk = (Pk) o;
            return Objects.equals(productId, pk.productId)
                    && Objects.equals(variantTypeId, pk.variantTypeId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(productId, variantTypeId);
        }
    }
}
