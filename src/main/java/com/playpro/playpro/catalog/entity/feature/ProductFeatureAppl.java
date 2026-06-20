package com.playpro.playpro.catalog.entity.feature;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_feature_appl")
public class ProductFeatureAppl {

    @EmbeddedId
    private ProductFeatureApplId id;

    @Column(name = "product_feature_appl_type_id", length = 20, nullable = false)
    private String productFeatureApplTypeId;

    @Column(name = "thru_date")
    private LocalDateTime thruDate;

    @Column(name = "sequence_num", precision = 20, scale = 0)
    private BigDecimal sequenceNum;

    @Column(precision = 18, scale = 2)
    private BigDecimal amount;

    public ProductFeatureApplId getId() {
        return id;
    }

    public void setId(ProductFeatureApplId id) {
        this.id = id;
    }

    public String getProductFeatureApplTypeId() {
        return productFeatureApplTypeId;
    }

    public void setProductFeatureApplTypeId(String productFeatureApplTypeId) {
        this.productFeatureApplTypeId = productFeatureApplTypeId;
    }

    public LocalDateTime getThruDate() {
        return thruDate;
    }

    public void setThruDate(LocalDateTime thruDate) {
        this.thruDate = thruDate;
    }

    public BigDecimal getSequenceNum() {
        return sequenceNum;
    }

    public void setSequenceNum(BigDecimal sequenceNum) {
        this.sequenceNum = sequenceNum;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
