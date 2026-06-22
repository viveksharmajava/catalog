package com.playpro.playpro.catalog.service;

import com.playpro.playpro.catalog.dto.ProductStoreCatalogDto;
import com.playpro.playpro.catalog.dto.ProductStoreDto;
import com.playpro.playpro.catalog.dto.ProductStoreSummaryDto;

import java.util.List;

public interface ProductStoreService {

    List<ProductStoreSummaryDto> listStores();

    ProductStoreDto getStore(String productStoreId);

    ProductStoreDto createStore(ProductStoreDto dto);

    ProductStoreDto updateStore(String productStoreId, ProductStoreDto dto);

    List<ProductStoreCatalogDto> listStoreCatalogs(String productStoreId);

    ProductStoreCatalogDto addStoreCatalog(String productStoreId, ProductStoreCatalogDto dto);

    void removeStoreCatalog(String productStoreId, String prodCatalogId, String fromDate);
}
