package com.playpro.playpro.catalog.controller;

import com.playpro.playpro.catalog.dto.ProductCategoryDto;
import com.playpro.playpro.catalog.service.CategoryService;
import org.springframework.http.ResponseEntity;
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

import java.util.List;

@RestController
@RequestMapping("/catalog/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<ProductCategoryDto> create(@RequestHeader(value = "X-User", required = false) String xUser,
                                                     @RequestBody ProductCategoryDto dto) {
        String principal = xUser == null ? "system" : xUser;
        return ResponseEntity.ok(categoryService.createCategory(dto, principal));
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<ProductCategoryDto> update(@RequestHeader(value = "X-User", required = false) String xUser,
                                                       @PathVariable("categoryId") String categoryId,
                                                       @RequestBody ProductCategoryDto dto) {
        String principal = xUser == null ? "system" : xUser;
        return ResponseEntity.ok(categoryService.updateCategory(categoryId, dto, principal));
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> delete(@PathVariable("categoryId") String categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<ProductCategoryDto> get(@PathVariable("categoryId") String categoryId) {
        return ResponseEntity.ok(categoryService.getCategory(categoryId));
    }

    @GetMapping("/tree")
    public ResponseEntity<List<ProductCategoryDto>> tree(
            @RequestParam(value = "root", defaultValue = "CAT-ROOT") String rootCategoryId,
            @RequestParam(value = "excludeEmpty", defaultValue = "false") boolean excludeEmpty) {
        return ResponseEntity.ok(categoryService.getCategoryTree(rootCategoryId, excludeEmpty));
    }
}
