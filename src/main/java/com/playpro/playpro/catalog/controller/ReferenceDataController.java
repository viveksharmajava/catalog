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

    @GetMapping("/prod-catalog-category-types")
    public ResponseEntity<List<Map<String, String>>> prodCatalogCategoryTypes() {
        return ResponseEntity.ok(Arrays.asList(
                option("PCCT_BROWSE_ROOT", "Browse Root (One)"),
                option("PCCT_SEARCH", "Default Search (One)"),
                option("PCCT_OTHER_SEARCH", "Other Search (Many)"),
                option("PCCT_QUICK_ADD", "Quick Add (Many)"),
                option("PCCT_PROMOTIONS", "Promotions (One)"),
                option("PCCT_MOST_POPULAR", "Most Popular (One)"),
                option("PCCT_WHATS_NEW", "What's New (One)")
        ));
    }

    /** OFBiz ProductPriceType seed (ProductSeedData.xml), ordered by description. */
    @GetMapping("/price-types")
    public ResponseEntity<List<Map<String, String>>> priceTypes() {
        return ResponseEntity.ok(Arrays.asList(
                option("AVERAGE_COST", "Average Cost"),
                option("BOX_PRICE", "Box Price"),
                option("COMPETITIVE_PRICE", "Competitive Price"),
                option("DEFAULT_PRICE", "Default Price"),
                option("LIST_PRICE", "List Price"),
                option("MAXIMUM_PRICE", "Maximum Price"),
                option("MINIMUM_ORDER_PRICE", "Minimum Order Price"),
                option("MINIMUM_PRICE", "Minimum Price"),
                option("PROMO_PRICE", "Promotional Price"),
                option("SHIPPING_ALLOWANCE", "Shipping Allowance Price"),
                option("SPECIAL_PROMO_PRICE", "Special Promo Price"),
                option("WHOLESALE_PRICE", "Wholesale Price")
        ));
    }

    /** OFBiz ProductPricePurpose seed (ProductSeedData.xml), ordered by description. */
    @GetMapping("/price-purposes")
    public ResponseEntity<List<Map<String, String>>> pricePurposes() {
        return ResponseEntity.ok(Arrays.asList(
                option("COMPONENT_PRICE", "Component Price"),
                option("DEPOSIT", "Deposit price"),
                option("PURCHASE", "Purchase/Initial"),
                option("RECURRING_CHARGE", "Recurring Charge"),
                option("USAGE_CHARGE", "Usage Charge")
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
