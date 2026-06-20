package com.playpro.playpro.catalog.entity.product;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "product_keyword")
public class ProductKeyword {

    @EmbeddedId
    private ProductKeywordId id;

    @Column(name = "relevancy_weight", precision = 20, scale = 0)
    private BigDecimal relevancyWeight;

    @Column(name = "status_id", length = 20)
    private String statusId;

    public ProductKeywordId getId() {
        return id;
    }

    public void setId(ProductKeywordId id) {
        this.id = id;
    }

    public BigDecimal getRelevancyWeight() {
        return relevancyWeight;
    }

    public void setRelevancyWeight(BigDecimal relevancyWeight) {
        this.relevancyWeight = relevancyWeight;
    }

    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }
}
