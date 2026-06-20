package com.playpro.playpro.catalog.controller;

import com.playpro.playpro.catalog.dto.ProductAttributeDto;
import com.playpro.playpro.catalog.dto.ProductCategoryDto;
import com.playpro.playpro.catalog.dto.ProductDto;
import com.playpro.playpro.catalog.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/catalog/products")
@Validated
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@RequestHeader(value = "X-User", required = false) String xUser,
                                                    @Valid @RequestBody ProductDto dto) {
        String principal = xUser == null ? "system" : xUser;
        return ResponseEntity.ok(productService.createProduct(dto, principal));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductDto> updateProduct(@RequestHeader(value = "X-User", required = false) String xUser,
                                                    @PathVariable String productId,
                                                    @RequestBody ProductDto dto) {
        String principal = xUser == null ? "system" : xUser;
        return ResponseEntity.ok(productService.updateProduct(productId, dto, principal));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable String productId) {
        return ResponseEntity.ok(productService.getProduct(productId));
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<ProductDto> getProductBySku(@PathVariable String sku) {
        return ResponseEntity.ok(productService.getProductBySku(sku));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductDto>> search(@RequestParam("keyword") String keyword) {
        return ResponseEntity.ok(productService.searchByKeyword(keyword));
    }

    @GetMapping("/{productId}/variants")
    public ResponseEntity<List<ProductDto>> getVariants(@PathVariable String productId) {
        return ResponseEntity.ok(productService.getVariants(productId));
    }

    @PostMapping("/{productId}/categories/{categoryId}")
    public ResponseEntity<Void> addCategory(@RequestHeader(value = "X-User", required = false) String xUser,
                                            @PathVariable String productId,
                                            @PathVariable String categoryId) {
        String principal = xUser == null ? "system" : xUser;
        productService.addCategoryToProduct(productId, categoryId, principal);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{productId}/categories/{categoryId}")
    public ResponseEntity<Void> removeCategory(@RequestHeader(value = "X-User", required = false) String xUser,
                                               @PathVariable String productId,
                                               @PathVariable String categoryId) {
        String principal = xUser == null ? "system" : xUser;
        productService.removeCategoryFromProduct(productId, categoryId, principal);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{productId}/categories")
    public ResponseEntity<List<ProductCategoryDto>> listCategories(@PathVariable String productId) {
        return ResponseEntity.ok(productService.getCategoriesForProduct(productId));
    }

    @PostMapping("/{productId}/attributes")
    public ResponseEntity<ProductAttributeDto> addAttribute(@RequestHeader(value = "X-User", required = false) String xUser,
                                                              @PathVariable String productId,
                                                              @RequestBody ProductAttributeDto dto) {
        String principal = xUser == null ? "system" : xUser;
        return ResponseEntity.ok(productService.addAttribute(productId, dto, principal));
    }

    @PostMapping("/{productId}/keywords")
    public ResponseEntity<Void> addKeyword(@RequestHeader(value = "X-User", required = false) String xUser,
                                           @PathVariable String productId,
                                           @RequestBody Map<String, String> body) {
        String principal = xUser == null ? "system" : xUser;
        productService.addKeyword(productId, body.get("keyword"), principal);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{virtualProductId}/variants/{variantProductId}")
    public ResponseEntity<Void> associateVariant(@RequestHeader(value = "X-User", required = false) String xUser,
                                                 @PathVariable String virtualProductId,
                                                 @PathVariable String variantProductId) {
        String principal = xUser == null ? "system" : xUser;
        productService.associateVariant(virtualProductId, variantProductId, principal);
        return ResponseEntity.ok().build();
    }
}
