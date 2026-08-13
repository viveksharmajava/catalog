package com.playpro.playpro.catalog.dto;

import java.math.BigDecimal;

public class ProductStoreShippingMethodDto {

    private String shippingMethodId;
    private String productStoreId;
    private String shippingType;
    private String displayName;
    private boolean enabled;
    private Integer sequenceNum;
    private String carrierProvider;
    private String apiKey;
    private String apiSecret;
    private String accountId;
    private String apiBaseUrl;
    private String trackOrderUrl;
    private String createShipmentUrl;
    private String webhookUrl;
    private String accessToken;
    private String defaultServiceCode;
    private BigDecimal flatRateAmount;
    private String extraConfig;

    public String getShippingMethodId() {
        return shippingMethodId;
    }

    public void setShippingMethodId(String shippingMethodId) {
        this.shippingMethodId = shippingMethodId;
    }

    public String getProductStoreId() {
        return productStoreId;
    }

    public void setProductStoreId(String productStoreId) {
        this.productStoreId = productStoreId;
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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getSequenceNum() {
        return sequenceNum;
    }

    public void setSequenceNum(Integer sequenceNum) {
        this.sequenceNum = sequenceNum;
    }

    public String getCarrierProvider() {
        return carrierProvider;
    }

    public void setCarrierProvider(String carrierProvider) {
        this.carrierProvider = carrierProvider;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    public void setApiSecret(String apiSecret) {
        this.apiSecret = apiSecret;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getApiBaseUrl() {
        return apiBaseUrl;
    }

    public void setApiBaseUrl(String apiBaseUrl) {
        this.apiBaseUrl = apiBaseUrl;
    }

    public String getTrackOrderUrl() {
        return trackOrderUrl;
    }

    public void setTrackOrderUrl(String trackOrderUrl) {
        this.trackOrderUrl = trackOrderUrl;
    }

    public String getCreateShipmentUrl() {
        return createShipmentUrl;
    }

    public void setCreateShipmentUrl(String createShipmentUrl) {
        this.createShipmentUrl = createShipmentUrl;
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public void setWebhookUrl(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getDefaultServiceCode() {
        return defaultServiceCode;
    }

    public void setDefaultServiceCode(String defaultServiceCode) {
        this.defaultServiceCode = defaultServiceCode;
    }

    public BigDecimal getFlatRateAmount() {
        return flatRateAmount;
    }

    public void setFlatRateAmount(BigDecimal flatRateAmount) {
        this.flatRateAmount = flatRateAmount;
    }

    public String getExtraConfig() {
        return extraConfig;
    }

    public void setExtraConfig(String extraConfig) {
        this.extraConfig = extraConfig;
    }
}
