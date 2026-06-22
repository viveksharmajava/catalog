package com.playpro.playpro.catalog.entity.store;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "product_store")
public class ProductStore {

    @Id
    @Column(name = "product_store_id", length = 20)
    private String productStoreId;

    @Column(name = "primary_store_group_id", length = 20)
    private String primaryStoreGroupId;

    @Column(name = "store_name", nullable = false, length = 100)
    private String storeName;

    @Column(name = "company_name", length = 100)
    private String companyName;

    @Column(name = "title", length = 100)
    private String title;

    @Column(name = "subtitle", length = 255)
    private String subtitle;

    @Column(name = "pay_to_party_id", length = 20)
    private String payToPartyId;

    @Column(name = "inventory_facility_id", length = 20)
    private String inventoryFacilityId;

    @Column(name = "one_inventory_facility", length = 1)
    private String oneInventoryFacility = "N";

    @Column(name = "check_inventory", length = 1)
    private String checkInventory = "Y";

    @Column(name = "reserve_inventory", length = 1)
    private String reserveInventory = "Y";

    @Column(name = "require_inventory", length = 1)
    private String requireInventory = "N";

    @Column(name = "show_out_of_stock_products", length = 1)
    private String showOutOfStockProducts = "Y";

    @Column(name = "is_immediately_fulfilled", length = 1)
    private String isImmediatelyFulfilled = "N";

    @Column(name = "is_demo_store", length = 1)
    private String isDemoStore = "N";

    @Column(name = "default_locale_string", length = 10)
    private String defaultLocaleString;

    @Column(name = "default_currency_uom_id", length = 20)
    private String defaultCurrencyUomId;

    @Column(name = "default_time_zone_string", length = 255)
    private String defaultTimeZoneString;

    @Column(name = "default_sales_channel_enum_id", length = 20)
    private String defaultSalesChannelEnumId;

    @Column(name = "order_number_prefix", length = 60)
    private String orderNumberPrefix;

    @Column(name = "visual_theme_id", length = 20)
    private String visualThemeId;

    @Column(name = "manual_auth_is_capture", length = 1)
    private String manualAuthIsCapture = "N";

    @Column(name = "prorate_shipping", length = 1)
    private String prorateShipping = "Y";

    @Column(name = "prorate_taxes", length = 1)
    private String prorateTaxes = "Y";

    @Column(name = "view_cart_on_add", length = 1)
    private String viewCartOnAdd = "N";

    @Column(name = "auto_save_cart", length = 1)
    private String autoSaveCart = "N";

    @Column(name = "auto_approve_order", length = 1)
    private String autoApproveOrder = "Y";

    @Column(name = "auto_approve_invoice", length = 1)
    private String autoApproveInvoice = "Y";

    @Column(name = "days_to_cancel_non_pay")
    private BigDecimal daysToCancelNonPay;

    @Column(name = "retry_failed_auths", length = 1)
    private String retryFailedAuths = "Y";

    @Column(name = "allow_password", length = 1)
    private String allowPassword = "Y";

    @Column(name = "default_password", length = 255)
    private String defaultPassword;

    @Column(name = "prod_search_exclude_variants", length = 1)
    private String prodSearchExcludeVariants = "Y";

    @Column(name = "show_prices_with_vat_tax", length = 1)
    private String showPricesWithVatTax = "N";

    public String getProductStoreId() {
        return productStoreId;
    }

    public void setProductStoreId(String productStoreId) {
        this.productStoreId = productStoreId;
    }

    public String getPrimaryStoreGroupId() {
        return primaryStoreGroupId;
    }

    public void setPrimaryStoreGroupId(String primaryStoreGroupId) {
        this.primaryStoreGroupId = primaryStoreGroupId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getPayToPartyId() {
        return payToPartyId;
    }

    public void setPayToPartyId(String payToPartyId) {
        this.payToPartyId = payToPartyId;
    }

    public String getInventoryFacilityId() {
        return inventoryFacilityId;
    }

    public void setInventoryFacilityId(String inventoryFacilityId) {
        this.inventoryFacilityId = inventoryFacilityId;
    }

    public String getOneInventoryFacility() {
        return oneInventoryFacility;
    }

    public void setOneInventoryFacility(String oneInventoryFacility) {
        this.oneInventoryFacility = oneInventoryFacility;
    }

    public String getCheckInventory() {
        return checkInventory;
    }

    public void setCheckInventory(String checkInventory) {
        this.checkInventory = checkInventory;
    }

    public String getReserveInventory() {
        return reserveInventory;
    }

    public void setReserveInventory(String reserveInventory) {
        this.reserveInventory = reserveInventory;
    }

    public String getRequireInventory() {
        return requireInventory;
    }

    public void setRequireInventory(String requireInventory) {
        this.requireInventory = requireInventory;
    }

    public String getShowOutOfStockProducts() {
        return showOutOfStockProducts;
    }

    public void setShowOutOfStockProducts(String showOutOfStockProducts) {
        this.showOutOfStockProducts = showOutOfStockProducts;
    }

    public String getIsImmediatelyFulfilled() {
        return isImmediatelyFulfilled;
    }

    public void setIsImmediatelyFulfilled(String isImmediatelyFulfilled) {
        this.isImmediatelyFulfilled = isImmediatelyFulfilled;
    }

    public String getIsDemoStore() {
        return isDemoStore;
    }

    public void setIsDemoStore(String isDemoStore) {
        this.isDemoStore = isDemoStore;
    }

    public String getDefaultLocaleString() {
        return defaultLocaleString;
    }

    public void setDefaultLocaleString(String defaultLocaleString) {
        this.defaultLocaleString = defaultLocaleString;
    }

    public String getDefaultCurrencyUomId() {
        return defaultCurrencyUomId;
    }

    public void setDefaultCurrencyUomId(String defaultCurrencyUomId) {
        this.defaultCurrencyUomId = defaultCurrencyUomId;
    }

    public String getDefaultTimeZoneString() {
        return defaultTimeZoneString;
    }

    public void setDefaultTimeZoneString(String defaultTimeZoneString) {
        this.defaultTimeZoneString = defaultTimeZoneString;
    }

    public String getDefaultSalesChannelEnumId() {
        return defaultSalesChannelEnumId;
    }

    public void setDefaultSalesChannelEnumId(String defaultSalesChannelEnumId) {
        this.defaultSalesChannelEnumId = defaultSalesChannelEnumId;
    }

    public String getOrderNumberPrefix() {
        return orderNumberPrefix;
    }

    public void setOrderNumberPrefix(String orderNumberPrefix) {
        this.orderNumberPrefix = orderNumberPrefix;
    }

    public String getVisualThemeId() {
        return visualThemeId;
    }

    public void setVisualThemeId(String visualThemeId) {
        this.visualThemeId = visualThemeId;
    }

    public String getManualAuthIsCapture() {
        return manualAuthIsCapture;
    }

    public void setManualAuthIsCapture(String manualAuthIsCapture) {
        this.manualAuthIsCapture = manualAuthIsCapture;
    }

    public String getProrateShipping() {
        return prorateShipping;
    }

    public void setProrateShipping(String prorateShipping) {
        this.prorateShipping = prorateShipping;
    }

    public String getProrateTaxes() {
        return prorateTaxes;
    }

    public void setProrateTaxes(String prorateTaxes) {
        this.prorateTaxes = prorateTaxes;
    }

    public String getViewCartOnAdd() {
        return viewCartOnAdd;
    }

    public void setViewCartOnAdd(String viewCartOnAdd) {
        this.viewCartOnAdd = viewCartOnAdd;
    }

    public String getAutoSaveCart() {
        return autoSaveCart;
    }

    public void setAutoSaveCart(String autoSaveCart) {
        this.autoSaveCart = autoSaveCart;
    }

    public String getAutoApproveOrder() {
        return autoApproveOrder;
    }

    public void setAutoApproveOrder(String autoApproveOrder) {
        this.autoApproveOrder = autoApproveOrder;
    }

    public String getAutoApproveInvoice() {
        return autoApproveInvoice;
    }

    public void setAutoApproveInvoice(String autoApproveInvoice) {
        this.autoApproveInvoice = autoApproveInvoice;
    }

    public BigDecimal getDaysToCancelNonPay() {
        return daysToCancelNonPay;
    }

    public void setDaysToCancelNonPay(BigDecimal daysToCancelNonPay) {
        this.daysToCancelNonPay = daysToCancelNonPay;
    }

    public String getRetryFailedAuths() {
        return retryFailedAuths;
    }

    public void setRetryFailedAuths(String retryFailedAuths) {
        this.retryFailedAuths = retryFailedAuths;
    }

    public String getAllowPassword() {
        return allowPassword;
    }

    public void setAllowPassword(String allowPassword) {
        this.allowPassword = allowPassword;
    }

    public String getDefaultPassword() {
        return defaultPassword;
    }

    public void setDefaultPassword(String defaultPassword) {
        this.defaultPassword = defaultPassword;
    }

    public String getProdSearchExcludeVariants() {
        return prodSearchExcludeVariants;
    }

    public void setProdSearchExcludeVariants(String prodSearchExcludeVariants) {
        this.prodSearchExcludeVariants = prodSearchExcludeVariants;
    }

    public String getShowPricesWithVatTax() {
        return showPricesWithVatTax;
    }

    public void setShowPricesWithVatTax(String showPricesWithVatTax) {
        this.showPricesWithVatTax = showPricesWithVatTax;
    }
}
