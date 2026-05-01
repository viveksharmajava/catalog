package com.playpro.playpro.catalog.controller;

import com.playpro.playpro.catalog.dto.CategoryDto;
import com.playpro.playpro.catalog.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/catalog/categories")
public class CategoryController {

    private final CategoryService service;

    public CategoryController(CategoryService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CategoryDto> create(@RequestHeader(value = "X-User", required = false) String xUser,
                                              @RequestBody CategoryDto dto) {
        String principal = (xUser == null ? "system" : xUser);
        CategoryDto created = service.createCategory(dto, principal);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> update(@RequestHeader(value = "X-User", required = false) String xUser,
                                              @PathVariable("id") Long id,
                                              @RequestBody CategoryDto dto) {
        String principal = (xUser == null ? "system" : xUser);
        CategoryDto updated = service.updateCategory(id, dto, principal);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        service.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> get(@PathVariable("id") Long id) {
        CategoryDto dto = service.getCategory(id);
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/tree")
    public ResponseEntity<List<CategoryDto>> tree() {
        return ResponseEntity.ok(service.getCategoryTree());
    }
}
