package com.playpro.playpro.catalog.entity.store;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_store_payment_method")
public class ProductStorePaymentMethod {

    @Id
    @Column(name = "payment_method_id", length = 40)
    private String paymentMethodId;

    @Column(name = "product_store_id", length = 20, nullable = false)
    private String productStoreId;

    @Column(name = "payment_type", length = 40, nullable = false)
    private String paymentType;

    @Column(name = "display_name", length = 100, nullable = false)
    private String displayName;

    @Column(name = "enabled", length = 1, nullable = false)
    private String enabled = "N";

    @Column(name = "sequence_num", nullable = false)
    private Integer sequenceNum = 0;

    @Column(name = "gateway_provider", length = 60)
    private String gatewayProvider;

    @Column(name = "api_key", length = 500)
    private String apiKey;

    @Column(name = "api_secret", length = 500)
    private String apiSecret;

    @Column(name = "merchant_id", length = 200)
    private String merchantId;

    @Column(name = "gateway_url", length = 500)
    private String gatewayUrl;

    @Column(name = "redirect_url", length = 500)
    private String redirectUrl;

    @Column(name = "webhook_url", length = 500)
    private String webhookUrl;

    @Column(name = "access_token", length = 1000)
    private String accessToken;

    @Column(name = "publishable_key", length = 500)
    private String publishableKey;

    @Lob
    @Column(name = "extra_config")
    private String extraConfig;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    public String getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(String paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public String getProductStoreId() {
        return productStoreId;
    }

    public void setProductStoreId(String productStoreId) {
        this.productStoreId = productStoreId;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
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

    public String getGatewayProvider() {
        return gatewayProvider;
    }

    public void setGatewayProvider(String gatewayProvider) {
        this.gatewayProvider = gatewayProvider;
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

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getGatewayUrl() {
        return gatewayUrl;
    }

    public void setGatewayUrl(String gatewayUrl) {
        this.gatewayUrl = gatewayUrl;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
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

    public String getPublishableKey() {
        return publishableKey;
    }

    public void setPublishableKey(String publishableKey) {
        this.publishableKey = publishableKey;
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
