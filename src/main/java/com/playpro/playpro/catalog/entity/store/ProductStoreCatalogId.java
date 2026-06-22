package com.playpro.playpro.catalog.entity.store;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Embeddable
public class ProductStoreCatalogId implements Serializable {

    @Column(name = "product_store_id", length = 20)
    private String productStoreId;

    @Column(name = "prod_catalog_id", length = 20)
    private String prodCatalogId;

    @Column(name = "from_date")
    private LocalDateTime fromDate;

    public ProductStoreCatalogId() {
    }

    public ProductStoreCatalogId(String productStoreId, String prodCatalogId, LocalDateTime fromDate) {
        this.productStoreId = productStoreId;
        this.prodCatalogId = prodCatalogId;
        this.fromDate = fromDate;
    }

    public String getProductStoreId() {
        return productStoreId;
    }

    public void setProductStoreId(String productStoreId) {
        this.productStoreId = productStoreId;
    }

    public String getProdCatalogId() {
        return prodCatalogId;
    }

    public void setProdCatalogId(String prodCatalogId) {
        this.prodCatalogId = prodCatalogId;
    }

    public LocalDateTime getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDateTime fromDate) {
        this.fromDate = fromDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductStoreCatalogId)) return false;
        ProductStoreCatalogId that = (ProductStoreCatalogId) o;
        return Objects.equals(productStoreId, that.productStoreId)
                && Objects.equals(prodCatalogId, that.prodCatalogId)
                && Objects.equals(fromDate, that.fromDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productStoreId, prodCatalogId, fromDate);
    }
}
