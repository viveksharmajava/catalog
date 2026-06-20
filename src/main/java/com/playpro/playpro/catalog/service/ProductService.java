package com.playpro.playpro.catalog.service;

import com.playpro.playpro.catalog.dto.ProductAttributeDto;
import com.playpro.playpro.catalog.dto.ProductCategoryDto;
import com.playpro.playpro.catalog.dto.ProductDto;

import java.util.List;

public interface ProductService {

    ProductDto createProduct(ProductDto dto, String principal);

    ProductDto updateProduct(String productId, ProductDto dto, String principal);

    ProductDto getProduct(String productId);

    ProductDto getProductBySku(String sku);

    List<ProductDto> searchByKeyword(String keyword);

    List<ProductDto> getVariants(String virtualProductId);

    void addCategoryToProduct(String productId, String categoryId, String principal);

    void removeCategoryFromProduct(String productId, String categoryId, String principal);

    List<ProductCategoryDto> getCategoriesForProduct(String productId);

    ProductAttributeDto addAttribute(String productId, ProductAttributeDto dto, String principal);

    void addKeyword(String productId, String keyword, String principal);

    void associateVariant(String virtualProductId, String variantProductId, String principal);
}
