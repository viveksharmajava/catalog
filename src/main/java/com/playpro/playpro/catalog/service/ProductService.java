package com.playpro.playpro.catalog.service;

import com.playpro.playpro.catalog.dto.ProductDto;
import com.playpro.playpro.catalog.dto.CategoryDto;

import java.util.List;

public interface ProductService {
    ProductDto createProduct(ProductDto dto, String principal);
    ProductDto getProduct(Long id);

    void addCategoryToProduct(Long productId, Long categoryId, String principal);
    void removeCategoryFromProduct(Long productId, Long categoryId, String principal);
    List<CategoryDto> getCategoriesForProduct(Long productId);
}
