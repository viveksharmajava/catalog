package com.playpro.playpro.catalog.dto;

import java.math.BigDecimal;

public class ProductStoreDto {

    private String productStoreId;
    private String primaryStoreGroupId;
    private String storeName;
    private String companyName;
    private String title;
    private String subtitle;
    private String payToPartyId;
    private String inventoryFacilityId;
    private String oneInventoryFacility = "N";
    private String checkInventory = "Y";
    private String reserveInventory = "Y";
    private String requireInventory = "N";
    private String showOutOfStockProducts = "Y";
    private String isImmediatelyFulfilled = "N";
    private String isDemoStore = "N";
    private String defaultLocaleString;
    private String defaultCurrencyUomId;
    private String defaultTimeZoneString;
    private String defaultSalesChannelEnumId;
    private String orderNumberPrefix;
    private String visualThemeId;
    private String manualAuthIsCapture = "N";
    private String prorateShipping = "Y";
    private String prorateTaxes = "Y";
    private String viewCartOnAdd = "N";
    private String autoSaveCart = "N";
    private String autoApproveOrder = "Y";
    private String autoApproveInvoice = "Y";
    private BigDecimal daysToCancelNonPay;
    private String retryFailedAuths = "Y";
    private String allowPassword = "Y";
    private String defaultPassword;
    private String prodSearchExcludeVariants = "Y";
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
