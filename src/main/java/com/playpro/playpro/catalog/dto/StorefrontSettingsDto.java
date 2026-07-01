package com.playpro.playpro.catalog.dto;

import java.util.ArrayList;
import java.util.List;

public class StorefrontSettingsDto {

    private String productStoreId;
    private String storeName;
    private String defaultCurrencyUomId;
    private String contactUsContent;
    private String aboutUsContent;
    private String shippingPolicyContent;
    private String returnsContent;
    private String privacyPolicyContent;
    private String termsAndConditionsContent;
    private List<String> catalogIds = new ArrayList<>();

    public String getProductStoreId() {
        return productStoreId;
    }

    public void setProductStoreId(String productStoreId) {
        this.productStoreId = productStoreId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getDefaultCurrencyUomId() {
        return defaultCurrencyUomId;
    }

    public void setDefaultCurrencyUomId(String defaultCurrencyUomId) {
        this.defaultCurrencyUomId = defaultCurrencyUomId;
    }

    public String getContactUsContent() {
        return contactUsContent;
    }

    public void setContactUsContent(String contactUsContent) {
        this.contactUsContent = contactUsContent;
    }

    public String getAboutUsContent() {
        return aboutUsContent;
    }

    public void setAboutUsContent(String aboutUsContent) {
        this.aboutUsContent = aboutUsContent;
    }

    public String getShippingPolicyContent() {
        return shippingPolicyContent;
    }

    public void setShippingPolicyContent(String shippingPolicyContent) {
        this.shippingPolicyContent = shippingPolicyContent;
    }

    public String getReturnsContent() {
        return returnsContent;
    }

    public void setReturnsContent(String returnsContent) {
        this.returnsContent = returnsContent;
    }

    public String getPrivacyPolicyContent() {
        return privacyPolicyContent;
    }

    public void setPrivacyPolicyContent(String privacyPolicyContent) {
        this.privacyPolicyContent = privacyPolicyContent;
    }

    public String getTermsAndConditionsContent() {
        return termsAndConditionsContent;
    }

    public void setTermsAndConditionsContent(String termsAndConditionsContent) {
        this.termsAndConditionsContent = termsAndConditionsContent;
    }

    public List<String> getCatalogIds() {
        return catalogIds;
    }

    public void setCatalogIds(List<String> catalogIds) {
        this.catalogIds = catalogIds;
    }
}
