package com.playpro.playpro.catalog.entity.store;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_store_catalog")
public class ProductStoreCatalog {

    @EmbeddedId
    private ProductStoreCatalogId id;

    @Column(name = "thru_date")
    private LocalDateTime thruDate;

    @Column(name = "sequence_num")
    private BigDecimal sequenceNum;

    public ProductStoreCatalogId getId() {
        return id;
    }

    public void setId(ProductStoreCatalogId id) {
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
}
