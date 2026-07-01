package com.playpro.playpro.catalog.dto;

public class ProductStoreSettingDto {

    private String productStoreId;
    private String storeName;
    private boolean defaultStore;
    private String contactUsContent;
    private String aboutUsContent;
    private String shippingPolicyContent;
    private String returnsContent;
    private String privacyPolicyContent;
    private String termsAndConditionsContent;

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

    public boolean isDefaultStore() {
        return defaultStore;
    }

    public void setDefaultStore(boolean defaultStore) {
        this.defaultStore = defaultStore;
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
}
