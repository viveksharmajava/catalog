package com.playpro.playpro.catalog.entity.category;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_category_member")
public class ProductCategoryMember {

    @EmbeddedId
    private ProductCategoryMemberId id;

    @Column(name = "thru_date")
    private LocalDateTime thruDate;

    @Column(columnDefinition = "CLOB")
    private String comments;

    @Column(name = "sequence_num", precision = 20, scale = 0)
    private BigDecimal sequenceNum;

    @Column(precision = 18, scale = 6)
    private BigDecimal quantity;

    public ProductCategoryMemberId getId() {
        return id;
    }

    public void setId(ProductCategoryMemberId id) {
        this.id = id;
    }

    public LocalDateTime getThruDate() {
        return thruDate;
    }

    public void setThruDate(LocalDateTime thruDate) {
        this.thruDate = thruDate;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
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
}
