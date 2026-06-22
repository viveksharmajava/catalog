package com.playpro.playpro.catalog.controller;

import com.playpro.playpro.catalog.dto.ProductStoreCatalogDto;
import com.playpro.playpro.catalog.dto.ProductStoreDto;
import com.playpro.playpro.catalog.dto.ProductStoreSummaryDto;
import com.playpro.playpro.catalog.service.ProductStoreService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/catalog/product-stores")
@Validated
public class ProductStoreController {

    private final ProductStoreService productStoreService;

    public ProductStoreController(ProductStoreService productStoreService) {
        this.productStoreService = productStoreService;
    }

    @GetMapping
    public ResponseEntity<List<ProductStoreSummaryDto>> listStores() {
        return ResponseEntity.ok(productStoreService.listStores());
    }

    @GetMapping("/{productStoreId}")
    public ResponseEntity<ProductStoreDto> getStore(@PathVariable String productStoreId) {
        return ResponseEntity.ok(productStoreService.getStore(productStoreId));
    }

    @PostMapping
    public ResponseEntity<ProductStoreDto> createStore(@RequestBody ProductStoreDto dto) {
        return ResponseEntity.ok(productStoreService.createStore(dto));
    }

    @PutMapping("/{productStoreId}")
    public ResponseEntity<ProductStoreDto> updateStore(@PathVariable String productStoreId,
                                                       @RequestBody ProductStoreDto dto) {
        return ResponseEntity.ok(productStoreService.updateStore(productStoreId, dto));
    }

    @GetMapping("/{productStoreId}/catalogs")
    public ResponseEntity<List<ProductStoreCatalogDto>> listStoreCatalogs(@PathVariable String productStoreId) {
        return ResponseEntity.ok(productStoreService.listStoreCatalogs(productStoreId));
    }

    @PostMapping("/{productStoreId}/catalogs")
    public ResponseEntity<ProductStoreCatalogDto> addStoreCatalog(@PathVariable String productStoreId,
                                                                 @RequestBody ProductStoreCatalogDto dto) {
        return ResponseEntity.ok(productStoreService.addStoreCatalog(productStoreId, dto));
    }

    @DeleteMapping("/{productStoreId}/catalogs/{prodCatalogId}")
    public ResponseEntity<Void> removeStoreCatalog(@PathVariable String productStoreId,
                                                   @PathVariable String prodCatalogId,
                                                   @RequestParam String fromDate) {
        productStoreService.removeStoreCatalog(productStoreId, prodCatalogId, fromDate);
        return ResponseEntity.noContent().build();
    }
}
