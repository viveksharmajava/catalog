package com.playpro.playpro.catalog.controller;

import com.playpro.playpro.catalog.dto.ProductStoreShippingMethodDto;
import com.playpro.playpro.catalog.dto.StorefrontShippingMethodDto;
import com.playpro.playpro.catalog.service.ProductStoreShippingMethodService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/catalog")
public class ProductStoreShippingMethodController {

    private final ProductStoreShippingMethodService shippingMethodService;

    public ProductStoreShippingMethodController(ProductStoreShippingMethodService shippingMethodService) {
        this.shippingMethodService = shippingMethodService;
    }

    @GetMapping("/storefront/shipping-methods")
    public ResponseEntity<List<StorefrontShippingMethodDto>> listEnabledForStorefront() {
        return ResponseEntity.ok(shippingMethodService.listEnabledForDefaultStore());
    }

    @GetMapping("/product-stores/{productStoreId}/shipping-methods")
    public ResponseEntity<List<ProductStoreShippingMethodDto>> list(@PathVariable String productStoreId) {
        return ResponseEntity.ok(shippingMethodService.listForStore(productStoreId));
    }

    @GetMapping("/product-stores/{productStoreId}/shipping-methods/{shippingMethodId}")
    public ResponseEntity<ProductStoreShippingMethodDto> get(@PathVariable String productStoreId,
                                                             @PathVariable String shippingMethodId) {
        return ResponseEntity.ok(shippingMethodService.get(productStoreId, shippingMethodId));
    }

    @PostMapping("/product-stores/{productStoreId}/shipping-methods")
    public ResponseEntity<ProductStoreShippingMethodDto> create(@PathVariable String productStoreId,
                                                                @RequestBody ProductStoreShippingMethodDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(shippingMethodService.create(productStoreId, dto));
    }

    @PutMapping("/product-stores/{productStoreId}/shipping-methods/{shippingMethodId}")
    public ResponseEntity<ProductStoreShippingMethodDto> update(@PathVariable String productStoreId,
                                                                @PathVariable String shippingMethodId,
                                                                @RequestBody ProductStoreShippingMethodDto dto) {
        return ResponseEntity.ok(shippingMethodService.update(productStoreId, shippingMethodId, dto));
    }

    @DeleteMapping("/product-stores/{productStoreId}/shipping-methods/{shippingMethodId}")
    public ResponseEntity<Void> delete(@PathVariable String productStoreId,
                                       @PathVariable String shippingMethodId) {
        shippingMethodService.delete(productStoreId, shippingMethodId);
        return ResponseEntity.noContent().build();
    }
}
