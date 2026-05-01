package com.playpro.playpro.catalog.service;

import com.playpro.playpro.catalog.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(CategoryDto dto, String principal);
    CategoryDto updateCategory(Long id, CategoryDto dto, String principal);
    void deleteCategory(Long id);
    CategoryDto getCategory(Long id);
    List<CategoryDto> getCategoryTree();
}
