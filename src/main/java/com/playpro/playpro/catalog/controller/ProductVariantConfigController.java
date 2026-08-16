package com.playpro.playpro.catalog.controller;

import com.playpro.playpro.catalog.dto.ProductVariantConfigDto;
import com.playpro.playpro.catalog.dto.ProductVariantConfigRequest;
import com.playpro.playpro.catalog.service.ProductVariantConfigService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/catalog/products/{productId}/variant-config")
public class ProductVariantConfigController {

    private final ProductVariantConfigService variantConfigService;

    public ProductVariantConfigController(ProductVariantConfigService variantConfigService) {
        this.variantConfigService = variantConfigService;
    }

    @GetMapping
    public ResponseEntity<ProductVariantConfigDto> getConfig(
            @PathVariable String productId,
            @RequestParam(value = "productStoreId", required = false) String productStoreId) {
        return ResponseEntity.ok(variantConfigService.getConfig(productId, productStoreId));
    }

    @PutMapping
    public ResponseEntity<ProductVariantConfigDto> saveConfig(
            @RequestHeader(value = "X-User", required = false) String xUser,
            @PathVariable String productId,
            @RequestBody ProductVariantConfigRequest request) {
        String principal = xUser == null ? "system" : xUser;
        return ResponseEntity.ok(variantConfigService.saveConfig(productId, request, principal));
    }

    @PostMapping("/generate")
    public ResponseEntity<ProductVariantConfigDto> generate(
            @RequestHeader(value = "X-User", required = false) String xUser,
            @PathVariable String productId) {
        String principal = xUser == null ? "system" : xUser;
        return ResponseEntity.ok(variantConfigService.generateVariants(productId, principal));
    }
}
