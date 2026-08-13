package com.playpro.playpro.catalog.service;

import com.playpro.playpro.catalog.dto.ProductStoreShippingMethodDto;
import com.playpro.playpro.catalog.dto.StorefrontShippingMethodDto;

import java.util.List;

public interface ProductStoreShippingMethodService {

    List<ProductStoreShippingMethodDto> listForStore(String productStoreId);

    ProductStoreShippingMethodDto get(String productStoreId, String shippingMethodId);

    ProductStoreShippingMethodDto create(String productStoreId, ProductStoreShippingMethodDto dto);

    ProductStoreShippingMethodDto update(String productStoreId, String shippingMethodId,
                                         ProductStoreShippingMethodDto dto);

    void delete(String productStoreId, String shippingMethodId);

    List<StorefrontShippingMethodDto> listEnabledForDefaultStore();
}
