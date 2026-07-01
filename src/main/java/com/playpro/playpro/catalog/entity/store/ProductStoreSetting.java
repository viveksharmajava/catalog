package com.playpro.playpro.catalog.entity.store;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_store_setting")
public class ProductStoreSetting {

    @Id
    @Column(name = "product_store_id", length = 20)
    private String productStoreId;

    @Column(name = "is_default_store", length = 1, nullable = false)
    private String isDefaultStore = "N";

    @Column(name = "contact_us_content", columnDefinition = "CLOB")
    private String contactUsContent;

    @Column(name = "about_us_content", columnDefinition = "CLOB")
    private String aboutUsContent;

    @Column(name = "shipping_policy_content", columnDefinition = "CLOB")
    private String shippingPolicyContent;

    @Column(name = "returns_content", columnDefinition = "CLOB")
    private String returnsContent;

    @Column(name = "privacy_policy_content", columnDefinition = "CLOB")
    private String privacyPolicyContent;

    @Column(name = "terms_and_conditions_content", columnDefinition = "CLOB")
    private String termsAndConditionsContent;

    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    public String getProductStoreId() {
        return productStoreId;
    }

    public void setProductStoreId(String productStoreId) {
        this.productStoreId = productStoreId;
    }

    public String getIsDefaultStore() {
        return isDefaultStore;
    }

    public void setIsDefaultStore(String isDefaultStore) {
        this.isDefaultStore = isDefaultStore;
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

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
