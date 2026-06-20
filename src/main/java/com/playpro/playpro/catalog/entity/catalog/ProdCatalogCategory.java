package com.playpro.playpro.catalog.entity.catalog;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "prod_catalog_category")
public class ProdCatalogCategory {

    @EmbeddedId
    private ProdCatalogCategoryId id;

    @Column(name = "thru_date")
    private LocalDateTime thruDate;

    @Column(name = "sequence_num", precision = 20, scale = 0)
    private BigDecimal sequenceNum;

    public ProdCatalogCategoryId getId() {
        return id;
    }

    public void setId(ProdCatalogCategoryId id) {
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
