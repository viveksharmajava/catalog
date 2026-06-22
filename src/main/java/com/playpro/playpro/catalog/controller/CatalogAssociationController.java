package com.playpro.playpro.catalog.controller;

import com.playpro.playpro.catalog.dto.CategoryProdCatalogDto;
import com.playpro.playpro.catalog.dto.ProductStoreCatalogDto;
import com.playpro.playpro.catalog.service.CatalogAssociationService;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/catalog/prod-catalogs/{prodCatalogId}")
public class CatalogAssociationController {

    private final CatalogAssociationService catalogAssociationService;

    public CatalogAssociationController(CatalogAssociationService catalogAssociationService) {
        this.catalogAssociationService = catalogAssociationService;
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryProdCatalogDto>> listCategories(@PathVariable String prodCatalogId) {
        return ResponseEntity.ok(catalogAssociationService.listCategories(prodCatalogId));
    }

    @PostMapping("/categories")
    public ResponseEntity<CategoryProdCatalogDto> addCategory(@PathVariable String prodCatalogId,
                                                              @RequestBody CategoryProdCatalogDto dto) {
        return ResponseEntity.ok(catalogAssociationService.addCategory(prodCatalogId, dto));
    }

    @PutMapping("/categories")
    public ResponseEntity<CategoryProdCatalogDto> updateCategory(@PathVariable String prodCatalogId,
                                                                 @RequestBody CategoryProdCatalogDto dto) {
        dto.setProdCatalogId(prodCatalogId);
        return ResponseEntity.ok(catalogAssociationService.updateCategory(dto));
    }

    @DeleteMapping("/categories")
    public ResponseEntity<Void> removeCategory(@PathVariable String prodCatalogId,
                                               @RequestBody CategoryProdCatalogDto dto) {
        dto.setProdCatalogId(prodCatalogId);
        catalogAssociationService.removeCategory(dto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stores")
    public ResponseEntity<List<ProductStoreCatalogDto>> listStores(@PathVariable String prodCatalogId) {
        return ResponseEntity.ok(catalogAssociationService.listStores(prodCatalogId));
    }

    @PostMapping("/stores")
    public ResponseEntity<ProductStoreCatalogDto> addStore(@PathVariable String prodCatalogId,
                                                            @RequestBody ProductStoreCatalogDto dto) {
        return ResponseEntity.ok(catalogAssociationService.addStore(prodCatalogId, dto));
    }

    @DeleteMapping("/stores/{productStoreId}")
    public ResponseEntity<Void> removeStore(@PathVariable String prodCatalogId,
                                            @PathVariable String productStoreId,
                                            @RequestParam String fromDate) {
        catalogAssociationService.removeStore(prodCatalogId, productStoreId, fromDate);
        return ResponseEntity.noContent().build();
    }
}
