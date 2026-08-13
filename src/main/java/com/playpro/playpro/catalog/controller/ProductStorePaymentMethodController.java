package com.playpro.playpro.catalog.controller;

import com.playpro.playpro.catalog.dto.ProductStorePaymentMethodDto;
import com.playpro.playpro.catalog.dto.StorefrontPaymentMethodDto;
import com.playpro.playpro.catalog.service.ProductStorePaymentMethodService;
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
public class ProductStorePaymentMethodController {

    private final ProductStorePaymentMethodService paymentMethodService;

    public ProductStorePaymentMethodController(ProductStorePaymentMethodService paymentMethodService) {
        this.paymentMethodService = paymentMethodService;
    }

    @GetMapping("/storefront/payment-methods")
    public ResponseEntity<List<StorefrontPaymentMethodDto>> listEnabledForStorefront() {
        return ResponseEntity.ok(paymentMethodService.listEnabledForDefaultStore());
    }

    @GetMapping("/product-stores/{productStoreId}/payment-methods")
    public ResponseEntity<List<ProductStorePaymentMethodDto>> list(@PathVariable String productStoreId) {
        return ResponseEntity.ok(paymentMethodService.listForStore(productStoreId));
    }

    @GetMapping("/product-stores/{productStoreId}/payment-methods/{paymentMethodId}")
    public ResponseEntity<ProductStorePaymentMethodDto> get(@PathVariable String productStoreId,
                                                            @PathVariable String paymentMethodId) {
        return ResponseEntity.ok(paymentMethodService.get(productStoreId, paymentMethodId));
    }

    @PostMapping("/product-stores/{productStoreId}/payment-methods")
    public ResponseEntity<ProductStorePaymentMethodDto> create(@PathVariable String productStoreId,
                                                               @RequestBody ProductStorePaymentMethodDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentMethodService.create(productStoreId, dto));
    }

    @PutMapping("/product-stores/{productStoreId}/payment-methods/{paymentMethodId}")
    public ResponseEntity<ProductStorePaymentMethodDto> update(@PathVariable String productStoreId,
                                                               @PathVariable String paymentMethodId,
                                                               @RequestBody ProductStorePaymentMethodDto dto) {
        return ResponseEntity.ok(paymentMethodService.update(productStoreId, paymentMethodId, dto));
    }

    @DeleteMapping("/product-stores/{productStoreId}/payment-methods/{paymentMethodId}")
    public ResponseEntity<Void> delete(@PathVariable String productStoreId,
                                       @PathVariable String paymentMethodId) {
        paymentMethodService.delete(productStoreId, paymentMethodId);
        return ResponseEntity.noContent().build();
    }
}
