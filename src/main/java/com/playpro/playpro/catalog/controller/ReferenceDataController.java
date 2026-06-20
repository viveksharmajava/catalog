package com.playpro.playpro.catalog.controller;

import com.playpro.playpro.catalog.dto.ProductCategoryDto;
import com.playpro.playpro.catalog.entity.product.ProductType;
import com.playpro.playpro.catalog.mapper.ProductMapper;
import com.playpro.playpro.catalog.repository.ProductCategoryRepository;
import com.playpro.playpro.catalog.repository.ProductTypeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog/reference")
public class ReferenceDataController {

    private final ProductTypeRepository productTypeRepository;
    private final ProductCategoryRepository categoryRepository;

    public ReferenceDataController(ProductTypeRepository productTypeRepository,
                                   ProductCategoryRepository categoryRepository) {
        this.productTypeRepository = productTypeRepository;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/product-types")
    public ResponseEntity<List<Map<String, String>>> productTypes() {
        List<Map<String, String>> types = productTypeRepository.findAll().stream()
                .map(this::toTypeOption)
                .collect(Collectors.toList());
        return ResponseEntity.ok(types);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<ProductCategoryDto>> categories() {
        return ResponseEntity.ok(categoryRepository.findAll().stream()
                .map(ProductMapper::toCategoryDto)
                .collect(Collectors.toList()));
    }

    @GetMapping("/product-statuses")
    public ResponseEntity<List<Map<String, String>>> productStatuses() {
        return ResponseEntity.ok(Arrays.asList(
                option("DRAFT", "Draft"),
                option("ACTIVE", "Active"),
                option("INACTIVE", "Inactive"),
                option("DISCONTINUED", "Discontinued")
        ));
    }

    @GetMapping("/category-types")
    public ResponseEntity<List<Map<String, String>>> categoryTypes() {
        return ResponseEntity.ok(Arrays.asList(
                option("CATALOG_CATEGORY", "Catalog Browse Category"),
                option("SEARCH_CATEGORY", "Search Category"),
                option("INTERNAL_CATEGORY", "Internal Merchandising Category")
        ));
    }

    private Map<String, String> toTypeOption(ProductType type) {
        Map<String, String> map = new HashMap<>();
        map.put("id", type.getProductTypeId());
        map.put("label", type.getDescription() != null ? type.getDescription() : type.getProductTypeId());
        return map;
    }

    private Map<String, String> option(String id, String label) {
        Map<String, String> map = new HashMap<>();
        map.put("id", id);
        map.put("label", label);
        return map;
    }
}
