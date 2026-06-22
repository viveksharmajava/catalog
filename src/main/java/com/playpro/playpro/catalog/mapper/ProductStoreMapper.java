package com.playpro.playpro.catalog.mapper;

import com.playpro.playpro.catalog.dto.ProductStoreCatalogDto;
import com.playpro.playpro.catalog.dto.ProductStoreDto;
import com.playpro.playpro.catalog.dto.ProductStoreSummaryDto;
import com.playpro.playpro.catalog.entity.catalog.ProdCatalog;
import com.playpro.playpro.catalog.entity.store.ProductStore;
import com.playpro.playpro.catalog.entity.store.ProductStoreCatalog;

public final class ProductStoreMapper {

    private ProductStoreMapper() {
    }

    public static ProductStoreDto toDto(ProductStore entity) {
        ProductStoreDto dto = new ProductStoreDto();
        dto.setProductStoreId(entity.getProductStoreId());
        dto.setPrimaryStoreGroupId(entity.getPrimaryStoreGroupId());
        dto.setStoreName(entity.getStoreName());
        dto.setCompanyName(entity.getCompanyName());
        dto.setTitle(entity.getTitle());
        dto.setSubtitle(entity.getSubtitle());
        dto.setPayToPartyId(entity.getPayToPartyId());
        dto.setInventoryFacilityId(entity.getInventoryFacilityId());
        dto.setOneInventoryFacility(entity.getOneInventoryFacility());
        dto.setCheckInventory(entity.getCheckInventory());
        dto.setReserveInventory(entity.getReserveInventory());
        dto.setRequireInventory(entity.getRequireInventory());
        dto.setShowOutOfStockProducts(entity.getShowOutOfStockProducts());
        dto.setIsImmediatelyFulfilled(entity.getIsImmediatelyFulfilled());
        dto.setIsDemoStore(entity.getIsDemoStore());
        dto.setDefaultLocaleString(entity.getDefaultLocaleString());
        dto.setDefaultCurrencyUomId(entity.getDefaultCurrencyUomId());
        dto.setDefaultTimeZoneString(entity.getDefaultTimeZoneString());
        dto.setDefaultSalesChannelEnumId(entity.getDefaultSalesChannelEnumId());
        dto.setOrderNumberPrefix(entity.getOrderNumberPrefix());
        dto.setVisualThemeId(entity.getVisualThemeId());
        dto.setManualAuthIsCapture(entity.getManualAuthIsCapture());
        dto.setProrateShipping(entity.getProrateShipping());
        dto.setProrateTaxes(entity.getProrateTaxes());
        dto.setViewCartOnAdd(entity.getViewCartOnAdd());
        dto.setAutoSaveCart(entity.getAutoSaveCart());
        dto.setAutoApproveOrder(entity.getAutoApproveOrder());
        dto.setAutoApproveInvoice(entity.getAutoApproveInvoice());
        dto.setDaysToCancelNonPay(entity.getDaysToCancelNonPay());
        dto.setRetryFailedAuths(entity.getRetryFailedAuths());
        dto.setAllowPassword(entity.getAllowPassword());
        dto.setDefaultPassword(entity.getDefaultPassword());
        dto.setProdSearchExcludeVariants(entity.getProdSearchExcludeVariants());
        dto.setShowPricesWithVatTax(entity.getShowPricesWithVatTax());
        return dto;
    }

    public static ProductStoreSummaryDto toSummaryDto(ProductStore entity) {
        ProductStoreSummaryDto dto = new ProductStoreSummaryDto();
        dto.setProductStoreId(entity.getProductStoreId());
        dto.setStoreName(entity.getStoreName());
        dto.setTitle(entity.getTitle());
        dto.setSubtitle(entity.getSubtitle());
        dto.setCompanyName(entity.getCompanyName());
        dto.setIsDemoStore(entity.getIsDemoStore());
        return dto;
    }

    public static void applyDtoToEntity(ProductStoreDto dto, ProductStore entity) {
        if (dto.getPrimaryStoreGroupId() != null) {
            entity.setPrimaryStoreGroupId(emptyToNull(dto.getPrimaryStoreGroupId()));
        }
        if (dto.getStoreName() != null) {
            entity.setStoreName(dto.getStoreName());
        }
        if (dto.getCompanyName() != null) {
            entity.setCompanyName(emptyToNull(dto.getCompanyName()));
        }
        if (dto.getTitle() != null) {
            entity.setTitle(emptyToNull(dto.getTitle()));
        }
        if (dto.getSubtitle() != null) {
            entity.setSubtitle(emptyToNull(dto.getSubtitle()));
        }
        if (dto.getPayToPartyId() != null) {
            entity.setPayToPartyId(emptyToNull(dto.getPayToPartyId()));
        }
        if (dto.getInventoryFacilityId() != null) {
            entity.setInventoryFacilityId(emptyToNull(dto.getInventoryFacilityId()));
        }
        if (dto.getOneInventoryFacility() != null) {
            entity.setOneInventoryFacility(dto.getOneInventoryFacility());
        }
        if (dto.getCheckInventory() != null) {
            entity.setCheckInventory(dto.getCheckInventory());
        }
        if (dto.getReserveInventory() != null) {
            entity.setReserveInventory(dto.getReserveInventory());
        }
        if (dto.getRequireInventory() != null) {
            entity.setRequireInventory(dto.getRequireInventory());
        }
        if (dto.getShowOutOfStockProducts() != null) {
            entity.setShowOutOfStockProducts(dto.getShowOutOfStockProducts());
        }
        if (dto.getIsImmediatelyFulfilled() != null) {
            entity.setIsImmediatelyFulfilled(dto.getIsImmediatelyFulfilled());
        }
        if (dto.getIsDemoStore() != null) {
            entity.setIsDemoStore(dto.getIsDemoStore());
        }
        if (dto.getDefaultLocaleString() != null) {
            entity.setDefaultLocaleString(emptyToNull(dto.getDefaultLocaleString()));
        }
        if (dto.getDefaultCurrencyUomId() != null) {
            entity.setDefaultCurrencyUomId(emptyToNull(dto.getDefaultCurrencyUomId()));
        }
        if (dto.getDefaultTimeZoneString() != null) {
            entity.setDefaultTimeZoneString(emptyToNull(dto.getDefaultTimeZoneString()));
        }
        if (dto.getDefaultSalesChannelEnumId() != null) {
            entity.setDefaultSalesChannelEnumId(emptyToNull(dto.getDefaultSalesChannelEnumId()));
        }
        if (dto.getOrderNumberPrefix() != null) {
            entity.setOrderNumberPrefix(emptyToNull(dto.getOrderNumberPrefix()));
        }
        if (dto.getVisualThemeId() != null) {
            entity.setVisualThemeId(emptyToNull(dto.getVisualThemeId()));
        }
        if (dto.getManualAuthIsCapture() != null) {
            entity.setManualAuthIsCapture(dto.getManualAuthIsCapture());
        }
        if (dto.getProrateShipping() != null) {
            entity.setProrateShipping(dto.getProrateShipping());
        }
        if (dto.getProrateTaxes() != null) {
            entity.setProrateTaxes(dto.getProrateTaxes());
        }
        if (dto.getViewCartOnAdd() != null) {
            entity.setViewCartOnAdd(dto.getViewCartOnAdd());
        }
        if (dto.getAutoSaveCart() != null) {
            entity.setAutoSaveCart(dto.getAutoSaveCart());
        }
        if (dto.getAutoApproveOrder() != null) {
            entity.setAutoApproveOrder(dto.getAutoApproveOrder());
        }
        if (dto.getAutoApproveInvoice() != null) {
            entity.setAutoApproveInvoice(dto.getAutoApproveInvoice());
        }
        if (dto.getDaysToCancelNonPay() != null) {
            entity.setDaysToCancelNonPay(dto.getDaysToCancelNonPay());
        }
        if (dto.getRetryFailedAuths() != null) {
            entity.setRetryFailedAuths(dto.getRetryFailedAuths());
        }
        if (dto.getAllowPassword() != null) {
            entity.setAllowPassword(dto.getAllowPassword());
        }
        if (dto.getDefaultPassword() != null) {
            entity.setDefaultPassword(emptyToNull(dto.getDefaultPassword()));
        }
        if (dto.getProdSearchExcludeVariants() != null) {
            entity.setProdSearchExcludeVariants(dto.getProdSearchExcludeVariants());
        }
        if (dto.getShowPricesWithVatTax() != null) {
            entity.setShowPricesWithVatTax(dto.getShowPricesWithVatTax());
        }
    }

    public static ProductStoreCatalogDto toCatalogDto(ProductStoreCatalog member, ProdCatalog catalog) {
        ProductStoreCatalogDto dto = new ProductStoreCatalogDto();
        dto.setProductStoreId(member.getId().getProductStoreId());
        dto.setProdCatalogId(member.getId().getProdCatalogId());
        dto.setFromDate(member.getId().getFromDate());
        dto.setThruDate(member.getThruDate());
        dto.setSequenceNum(member.getSequenceNum());
        if (catalog != null) {
            dto.setCatalogName(catalog.getCatalogName());
        }
        return dto;
    }

    private static String emptyToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }
}
