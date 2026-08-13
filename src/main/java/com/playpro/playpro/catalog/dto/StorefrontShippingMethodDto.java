package com.playpro.playpro.catalog.dto;

import java.math.BigDecimal;

public class StorefrontShippingMethodDto {

    private String shippingMethodId;
    private String shippingType;
    private String displayName;
    private String carrierProvider;
    private String trackOrderUrl;
    private BigDecimal flatRateAmount;
    private Integer sequenceNum;

    public String getShippingMethodId() {
        return shippingMethodId;
    }

    public void setShippingMethodId(String shippingMethodId) {
        this.shippingMethodId = shippingMethodId;
    }

    public String getShippingType() {
        return shippingType;
    }

    public void setShippingType(String shippingType) {
        this.shippingType = shippingType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getCarrierProvider() {
        return carrierProvider;
    }

    public void setCarrierProvider(String carrierProvider) {
        this.carrierProvider = carrierProvider;
    }

    public String getTrackOrderUrl() {
        return trackOrderUrl;
    }

    public void setTrackOrderUrl(String trackOrderUrl) {
        this.trackOrderUrl = trackOrderUrl;
    }

    public BigDecimal getFlatRateAmount() {
        return flatRateAmount;
    }

    public void setFlatRateAmount(BigDecimal flatRateAmount) {
        this.flatRateAmount = flatRateAmount;
    }

    public Integer getSequenceNum() {
        return sequenceNum;
    }

    public void setSequenceNum(Integer sequenceNum) {
        this.sequenceNum = sequenceNum;
    }
}
