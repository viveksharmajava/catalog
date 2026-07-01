package com.playpro.playpro.catalog.controller;

import com.playpro.playpro.catalog.dto.ProductStoreSettingDto;
import com.playpro.playpro.catalog.dto.StorefrontSettingsDto;
import com.playpro.playpro.catalog.service.ProductStoreSettingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/catalog")
public class StorefrontController {

    private final ProductStoreSettingService productStoreSettingService;

    public StorefrontController(ProductStoreSettingService productStoreSettingService) {
        this.productStoreSettingService = productStoreSettingService;
    }

    @GetMapping("/storefront/settings")
    public ResponseEntity<StorefrontSettingsDto> defaultStorefrontSettings() {
        return ResponseEntity.ok(productStoreSettingService.getDefaultStorefrontSettings());
    }

    @GetMapping("/product-stores/{productStoreId}/settings")
    public ResponseEntity<ProductStoreSettingDto> getStoreSettings(@PathVariable String productStoreId) {
        return ResponseEntity.ok(productStoreSettingService.getSettings(productStoreId));
    }

    @PutMapping("/product-stores/{productStoreId}/settings")
    public ResponseEntity<ProductStoreSettingDto> saveStoreSettings(@PathVariable String productStoreId,
                                                                    @RequestBody ProductStoreSettingDto dto) {
        return ResponseEntity.ok(productStoreSettingService.saveSettings(productStoreId, dto));
    }
}
