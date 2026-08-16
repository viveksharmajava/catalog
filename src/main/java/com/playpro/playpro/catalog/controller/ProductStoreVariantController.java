package com.playpro.playpro.catalog.controller;

import com.playpro.playpro.catalog.dto.ProductStoreVariantTypeDto;
import com.playpro.playpro.catalog.dto.ProductStoreVariantValueDto;
import com.playpro.playpro.catalog.service.ProductStoreVariantService;
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
@RequestMapping("/catalog/product-stores/{productStoreId}")
public class ProductStoreVariantController {

    private final ProductStoreVariantService variantService;

    public ProductStoreVariantController(ProductStoreVariantService variantService) {
        this.variantService = variantService;
    }

    @GetMapping("/variant-types")
    public ResponseEntity<List<ProductStoreVariantTypeDto>> listTypes(@PathVariable String productStoreId) {
        return ResponseEntity.ok(variantService.listTypes(productStoreId));
    }

    @GetMapping("/variant-types/{variantTypeId}")
    public ResponseEntity<ProductStoreVariantTypeDto> getType(@PathVariable String productStoreId,
                                                                @PathVariable String variantTypeId) {
        return ResponseEntity.ok(variantService.getType(productStoreId, variantTypeId));
    }

    @PostMapping("/variant-types")
    public ResponseEntity<ProductStoreVariantTypeDto> createType(@PathVariable String productStoreId,
                                                                  @RequestBody ProductStoreVariantTypeDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(variantService.createType(productStoreId, dto));
    }

    @PutMapping("/variant-types/{variantTypeId}")
    public ResponseEntity<ProductStoreVariantTypeDto> updateType(@PathVariable String productStoreId,
                                                                   @PathVariable String variantTypeId,
                                                                   @RequestBody ProductStoreVariantTypeDto dto) {
        return ResponseEntity.ok(variantService.updateType(productStoreId, variantTypeId, dto));
    }

    @DeleteMapping("/variant-types/{variantTypeId}")
    public ResponseEntity<Void> deleteType(@PathVariable String productStoreId,
                                            @PathVariable String variantTypeId) {
        variantService.deleteType(productStoreId, variantTypeId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/variant-types/{variantTypeId}/values")
    public ResponseEntity<List<ProductStoreVariantValueDto>> listValues(@PathVariable String productStoreId,
                                                                        @PathVariable String variantTypeId) {
        return ResponseEntity.ok(variantService.listValues(productStoreId, variantTypeId));
    }

    @PostMapping("/variant-types/{variantTypeId}/values")
    public ResponseEntity<ProductStoreVariantValueDto> createValue(@PathVariable String productStoreId,
                                                                    @PathVariable String variantTypeId,
                                                                    @RequestBody ProductStoreVariantValueDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(variantService.createValue(productStoreId, variantTypeId, dto));
    }

    @PutMapping("/variant-types/{variantTypeId}/values/{variantValueId}")
    public ResponseEntity<ProductStoreVariantValueDto> updateValue(@PathVariable String productStoreId,
                                                                   @PathVariable String variantTypeId,
                                                                   @PathVariable String variantValueId,
                                                                   @RequestBody ProductStoreVariantValueDto dto) {
        return ResponseEntity.ok(variantService.updateValue(productStoreId, variantTypeId, variantValueId, dto));
    }

    @DeleteMapping("/variant-types/{variantTypeId}/values/{variantValueId}")
    public ResponseEntity<Void> deleteValue(@PathVariable String productStoreId,
                                             @PathVariable String variantTypeId,
                                             @PathVariable String variantValueId) {
        variantService.deleteValue(productStoreId, variantTypeId, variantValueId);
        return ResponseEntity.noContent().build();
    }
}
