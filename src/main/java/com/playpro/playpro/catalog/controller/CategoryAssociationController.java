package com.playpro.playpro.catalog.controller;

import com.playpro.playpro.catalog.dto.CategoryProdCatalogDto;
import com.playpro.playpro.catalog.dto.CategoryProductMemberDto;
import com.playpro.playpro.catalog.dto.CategoryRollupDto;
import com.playpro.playpro.catalog.service.CategoryAssociationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog/categories/{categoryId}")
public class CategoryAssociationController {

    private final CategoryAssociationService associationService;

    public CategoryAssociationController(CategoryAssociationService associationService) {
        this.associationService = associationService;
    }

    @GetMapping("/rollups/parents")
    public ResponseEntity<List<CategoryRollupDto>> listParentRollups(@PathVariable String categoryId) {
        return ResponseEntity.ok(associationService.listParentRollups(categoryId));
    }

    @GetMapping("/rollups/children")
    public ResponseEntity<List<CategoryRollupDto>> listChildRollups(@PathVariable String categoryId) {
        return ResponseEntity.ok(associationService.listChildRollups(categoryId));
    }

    @PostMapping("/rollups/parents")
    public ResponseEntity<CategoryRollupDto> addParentRollup(@PathVariable String categoryId,
                                                             @RequestBody CategoryRollupDto dto) {
        return ResponseEntity.ok(associationService.addParentRollup(categoryId, dto));
    }

    @PostMapping("/rollups/children")
    public ResponseEntity<CategoryRollupDto> addChildRollup(@PathVariable String categoryId,
                                                            @RequestBody CategoryRollupDto dto) {
        return ResponseEntity.ok(associationService.addChildRollup(categoryId, dto));
    }

    @PutMapping("/rollups")
    public ResponseEntity<CategoryRollupDto> updateRollup(@RequestBody CategoryRollupDto dto) {
        return ResponseEntity.ok(associationService.updateRollup(dto));
    }

    @DeleteMapping("/rollups")
    public ResponseEntity<Void> removeRollup(@RequestBody CategoryRollupDto dto) {
        associationService.removeRollup(dto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/products")
    public ResponseEntity<List<CategoryProductMemberDto>> listProducts(@PathVariable String categoryId) {
        return ResponseEntity.ok(associationService.listProductMembers(categoryId));
    }

    @PostMapping("/products")
    public ResponseEntity<CategoryProductMemberDto> addProduct(@PathVariable String categoryId,
                                                               @RequestBody CategoryProductMemberDto dto) {
        return ResponseEntity.ok(associationService.addProductMember(categoryId, dto));
    }

    @PutMapping("/products")
    public ResponseEntity<CategoryProductMemberDto> updateProduct(@RequestBody CategoryProductMemberDto dto) {
        return ResponseEntity.ok(associationService.updateProductMember(dto));
    }

    @DeleteMapping("/products")
    public ResponseEntity<Void> removeProduct(@RequestBody CategoryProductMemberDto dto) {
        associationService.removeProductMember(dto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/prod-catalogs")
    public ResponseEntity<List<CategoryProdCatalogDto>> listProdCatalogs(@PathVariable String categoryId) {
        return ResponseEntity.ok(associationService.listProdCatalogs(categoryId));
    }

    @PostMapping("/prod-catalogs")
    public ResponseEntity<CategoryProdCatalogDto> addProdCatalog(@PathVariable String categoryId,
                                                                 @RequestBody CategoryProdCatalogDto dto) {
        return ResponseEntity.ok(associationService.addProdCatalog(categoryId, dto));
    }

    @PutMapping("/prod-catalogs")
    public ResponseEntity<CategoryProdCatalogDto> updateProdCatalog(@RequestBody CategoryProdCatalogDto dto) {
        return ResponseEntity.ok(associationService.updateProdCatalog(dto));
    }

    @DeleteMapping("/prod-catalogs")
    public ResponseEntity<Void> removeProdCatalog(@RequestBody CategoryProdCatalogDto dto) {
        associationService.removeProdCatalog(dto);
        return ResponseEntity.noContent().build();
    }
}
