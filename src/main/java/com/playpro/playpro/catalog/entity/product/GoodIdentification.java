package com.playpro.playpro.catalog.entity.product;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "good_identification")
public class GoodIdentification {

    @EmbeddedId
    private GoodIdentificationId id;

    @Column(name = "id_value", length = 255, nullable = false)
    private String idValue;

    public GoodIdentificationId getId() {
        return id;
    }

    public void setId(GoodIdentificationId id) {
        this.id = id;
    }

    public String getIdValue() {
        return idValue;
    }

    public void setIdValue(String idValue) {
        this.idValue = idValue;
    }
}
