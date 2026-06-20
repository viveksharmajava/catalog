package com.playpro.playpro.catalog.entity.feature;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "product_feature")
public class ProductFeature {

    @Id
    @Column(name = "product_feature_id", length = 20)
    private String productFeatureId;

    @Column(name = "product_feature_type_id", length = 20, nullable = false)
    private String productFeatureTypeId;

    @Column(name = "product_feature_category_id", length = 20)
    private String productFeatureCategoryId;

    @Column(length = 255)
    private String description;

    @Column(length = 20)
    private String abbrev;

    @Column(name = "id_code", length = 60)
    private String idCode;

    @Column(name = "default_sequence_num", precision = 20, scale = 0)
    private BigDecimal defaultSequenceNum;

    public String getProductFeatureId() {
        return productFeatureId;
    }

    public void setProductFeatureId(String productFeatureId) {
        this.productFeatureId = productFeatureId;
    }

    public String getProductFeatureTypeId() {
        return productFeatureTypeId;
    }

    public void setProductFeatureTypeId(String productFeatureTypeId) {
        this.productFeatureTypeId = productFeatureTypeId;
    }

    public String getProductFeatureCategoryId() {
        return productFeatureCategoryId;
    }

    public void setProductFeatureCategoryId(String productFeatureCategoryId) {
        this.productFeatureCategoryId = productFeatureCategoryId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAbbrev() {
        return abbrev;
    }

    public void setAbbrev(String abbrev) {
        this.abbrev = abbrev;
    }

    public String getIdCode() {
        return idCode;
    }

    public void setIdCode(String idCode) {
        this.idCode = idCode;
    }

    public BigDecimal getDefaultSequenceNum() {
        return defaultSequenceNum;
    }

    public void setDefaultSequenceNum(BigDecimal defaultSequenceNum) {
        this.defaultSequenceNum = defaultSequenceNum;
    }
}
