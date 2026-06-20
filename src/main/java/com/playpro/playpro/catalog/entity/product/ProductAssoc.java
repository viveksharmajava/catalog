package com.playpro.playpro.catalog.entity.product;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_assoc")
public class ProductAssoc {

    @EmbeddedId
    private ProductAssocId id;

    @Column(name = "thru_date")
    private LocalDateTime thruDate;

    @Column(name = "sequence_num", precision = 20, scale = 0)
    private BigDecimal sequenceNum;

    @Column(precision = 18, scale = 6)
    private BigDecimal quantity;

    @Column(length = 255)
    private String reason;

    public ProductAssocId getId() {
        return id;
    }

    public void setId(ProductAssocId id) {
        this.id = id;
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

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
