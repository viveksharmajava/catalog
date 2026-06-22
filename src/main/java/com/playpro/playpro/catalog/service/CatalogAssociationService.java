package com.playpro.playpro.catalog.service;

import com.playpro.playpro.catalog.dto.CategoryProdCatalogDto;
import com.playpro.playpro.catalog.dto.ProductStoreCatalogDto;

import java.util.List;

public interface CatalogAssociationService {

    List<CategoryProdCatalogDto> listCategories(String prodCatalogId);

    CategoryProdCatalogDto addCategory(String prodCatalogId, CategoryProdCatalogDto dto);

    CategoryProdCatalogDto updateCategory(CategoryProdCatalogDto dto);

    void removeCategory(CategoryProdCatalogDto dto);

    List<ProductStoreCatalogDto> listStores(String prodCatalogId);

    ProductStoreCatalogDto addStore(String prodCatalogId, ProductStoreCatalogDto dto);

    void removeStore(String prodCatalogId, String productStoreId, String fromDate);
}
