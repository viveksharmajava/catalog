package com.playpro.playpro.catalog.service;

import com.playpro.playpro.catalog.dto.ProductCategoryDto;

import java.util.List;

public interface CategoryService {

    ProductCategoryDto createCategory(ProductCategoryDto dto, String principal);

    ProductCategoryDto updateCategory(String categoryId, ProductCategoryDto dto, String principal);

    ProductCategoryDto getCategory(String categoryId);

    void deleteCategory(String categoryId);

    List<ProductCategoryDto> getCategoryTree(String rootCategoryId, boolean excludeEmpty);
}
