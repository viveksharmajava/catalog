package com.playpro.playpro.catalog.service;

import com.playpro.playpro.catalog.dto.ProductCategoryDto;
import com.playpro.playpro.catalog.dto.CategoryFindRequest;
import com.playpro.playpro.catalog.dto.CategoryFindResponse;

import java.util.List;

public interface CategoryService {

    ProductCategoryDto createCategory(ProductCategoryDto dto, String principal);

    ProductCategoryDto updateCategory(String categoryId, ProductCategoryDto dto, String principal);

    ProductCategoryDto getCategory(String categoryId);

    CategoryFindResponse findCategories(CategoryFindRequest request);

    void deleteCategory(String categoryId);

    List<ProductCategoryDto> getCategoryTree(String rootCategoryId, boolean excludeEmpty);
}
