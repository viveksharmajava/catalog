package com.playpro.playpro.catalog.service;

import com.playpro.playpro.catalog.dto.ProductStoreSettingDto;
import com.playpro.playpro.catalog.dto.StorefrontSettingsDto;

public interface ProductStoreSettingService {

    ProductStoreSettingDto getSettings(String productStoreId);

    ProductStoreSettingDto saveSettings(String productStoreId, ProductStoreSettingDto dto);

    StorefrontSettingsDto getDefaultStorefrontSettings();

    void initializeSettingsForNewStore(String productStoreId);
}
