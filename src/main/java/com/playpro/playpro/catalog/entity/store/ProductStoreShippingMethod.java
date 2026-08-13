package com.playpro.playpro.catalog.entity.store;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_store_shipping_method")
public class ProductStoreShippingMethod {

    @Id
    @Column(name = "shipping_method_id", length = 40)
    private String shippingMethodId;

    @Column(name = "product_store_id", length = 20, nullable = false)
    private String productStoreId;

    @Column(name = "shipping_type", length = 40, nullable = false)
    private String shippingType;

    @Column(name = "display_name", length = 100, nullable = false)
    private String displayName;

    @Column(name = "enabled", length = 1, nullable = false)
    private String enabled = "N";

    @Column(name = "sequence_num", nullable = false)
    private Integer sequenceNum = 0;

    @Column(name = "carrier_provider", length = 60)
    private String carrierProvider;

    @Column(name = "api_key", length = 500)
    private String apiKey;

    @Column(name = "api_secret", length = 500)
    private String apiSecret;

    @Column(name = "account_id", length = 200)
    private String accountId;

    @Column(name = "api_base_url", length = 500)
    private String apiBaseUrl;

    @Column(name = "track_order_url", length = 500)
    private String trackOrderUrl;

    @Column(name = "create_shipment_url", length = 500)
    private String createShipmentUrl;

    @Column(name = "webhook_url", length = 500)
    private String webhookUrl;

    @Column(name = "access_token", length = 1000)
    private String accessToken;

    @Column(name = "default_service_code", length = 100)
    private String defaultServiceCode;

    @Column(name = "flat_rate_amount", precision = 18, scale = 2)
    private BigDecimal flatRateAmount;

    @Lob
    @Column(name = "extra_config")
    private String extraConfig;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

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

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
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

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
