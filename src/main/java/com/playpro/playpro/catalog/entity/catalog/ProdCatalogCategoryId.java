package com.playpro.playpro.catalog.entity.catalog;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Embeddable
public class ProdCatalogCategoryId implements Serializable {

    @Column(name = "prod_catalog_id", length = 20)
    private String prodCatalogId;

    @Column(name = "product_category_id", length = 20)
    private String productCategoryId;

    @Column(name = "prod_catalog_category_type_id", length = 20)
    private String prodCatalogCategoryTypeId;

    @Column(name = "from_date")
    private LocalDateTime fromDate;

    public ProdCatalogCategoryId() {
    }

    public ProdCatalogCategoryId(String prodCatalogId, String productCategoryId,
                                 String prodCatalogCategoryTypeId, LocalDateTime fromDate) {
        this.prodCatalogId = prodCatalogId;
        this.productCategoryId = productCategoryId;
        this.prodCatalogCategoryTypeId = prodCatalogCategoryTypeId;
        this.fromDate = fromDate;
    }

    public String getProdCatalogId() {
        return prodCatalogId;
    }

    public void setProdCatalogId(String prodCatalogId) {
        this.prodCatalogId = prodCatalogId;
    }

    public String getProductCategoryId() {
        return productCategoryId;
    }

    public void setProductCategoryId(String productCategoryId) {
        this.productCategoryId = productCategoryId;
    }

    public String getProdCatalogCategoryTypeId() {
        return prodCatalogCategoryTypeId;
    }

    public void setProdCatalogCategoryTypeId(String prodCatalogCategoryTypeId) {
        this.prodCatalogCategoryTypeId = prodCatalogCategoryTypeId;
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
        ProdCatalogCategoryId that = (ProdCatalogCategoryId) o;
        return Objects.equals(prodCatalogId, that.prodCatalogId)
                && Objects.equals(productCategoryId, that.productCategoryId)
                && Objects.equals(prodCatalogCategoryTypeId, that.prodCatalogCategoryTypeId)
                && Objects.equals(fromDate, that.fromDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(prodCatalogId, productCategoryId, prodCatalogCategoryTypeId, fromDate);
    }
}
