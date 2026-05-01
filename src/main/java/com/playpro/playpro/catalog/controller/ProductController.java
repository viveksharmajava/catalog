package com.playpro.playpro.catalog.controller;

import com.playpro.playpro.catalog.dto.CategoryDto;
import com.playpro.playpro.catalog.dto.ProductDto;
import com.playpro.playpro.catalog.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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
        String principal = (xUser == null ? "system" : xUser);
        ProductDto created = productService.createProduct(dto, principal);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable("id") Long id) {
        ProductDto dto = productService.getProduct(id);
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{productId}/categories/{categoryId}")
    public ResponseEntity<Void> addCategory(@RequestHeader(value = "X-User", required = false) String xUser,
                                            @PathVariable("productId") Long productId,
                                            @PathVariable("categoryId") Long categoryId) {
        String principal = (xUser == null ? "system" : xUser);
        productService.addCategoryToProduct(productId, categoryId, principal);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{productId}/categories/{categoryId}")
    public ResponseEntity<Void> removeCategory(@RequestHeader(value = "X-User", required = false) String xUser,
                                               @PathVariable("productId") Long productId,
                                               @PathVariable("categoryId") Long categoryId) {
        String principal = (xUser == null ? "system" : xUser);
        productService.removeCategoryFromProduct(productId, categoryId, principal);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{productId}/categories")
    public ResponseEntity<List<CategoryDto>> listCategories(@PathVariable("productId") Long productId) {
        List<CategoryDto> list = productService.getCategoriesForProduct(productId);
        return ResponseEntity.ok(list);
    }
}
