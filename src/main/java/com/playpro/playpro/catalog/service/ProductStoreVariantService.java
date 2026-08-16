package com.playpro.playpro.catalog.service;

import com.playpro.playpro.catalog.dto.ProductStoreVariantTypeDto;
import com.playpro.playpro.catalog.dto.ProductStoreVariantValueDto;

import java.util.List;

public interface ProductStoreVariantService {

    List<ProductStoreVariantTypeDto> listTypes(String productStoreId);

    ProductStoreVariantTypeDto getType(String productStoreId, String variantTypeId);

    ProductStoreVariantTypeDto createType(String productStoreId, ProductStoreVariantTypeDto dto);

    ProductStoreVariantTypeDto updateType(String productStoreId, String variantTypeId, ProductStoreVariantTypeDto dto);

    void deleteType(String productStoreId, String variantTypeId);

    List<ProductStoreVariantValueDto> listValues(String productStoreId, String variantTypeId);

    ProductStoreVariantValueDto createValue(String productStoreId, String variantTypeId, ProductStoreVariantValueDto dto);

    ProductStoreVariantValueDto updateValue(String productStoreId, String variantTypeId, String variantValueId,
                                             ProductStoreVariantValueDto dto);

    void deleteValue(String productStoreId, String variantTypeId, String variantValueId);
}
