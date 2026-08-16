package com.playpro.playpro.catalog.entity.product;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "product_variant_option_value")
@IdClass(ProductVariantOptionValue.Pk.class)
public class ProductVariantOptionValue {

    @Id
    @Column(name = "product_id", length = 20)
    private String productId;

    @Id
    @Column(name = "variant_type_id", length = 40)
    private String variantTypeId;

    @Id
    @Column(name = "variant_value_id", length = 40)
    private String variantValueId;

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

    public String getVariantValueId() {
        return variantValueId;
    }

    public void setVariantValueId(String variantValueId) {
        this.variantValueId = variantValueId;
    }

    public static class Pk implements Serializable {
        private String productId;
        private String variantTypeId;
        private String variantValueId;

        public Pk() {
        }

        public Pk(String productId, String variantTypeId, String variantValueId) {
            this.productId = productId;
            this.variantTypeId = variantTypeId;
            this.variantValueId = variantValueId;
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

        public String getVariantValueId() {
            return variantValueId;
        }

        public void setVariantValueId(String variantValueId) {
            this.variantValueId = variantValueId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Pk)) return false;
            Pk pk = (Pk) o;
            return Objects.equals(productId, pk.productId)
                    && Objects.equals(variantTypeId, pk.variantTypeId)
                    && Objects.equals(variantValueId, pk.variantValueId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(productId, variantTypeId, variantValueId);
        }
    }
}
