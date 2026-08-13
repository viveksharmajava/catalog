package com.playpro.playpro.catalog.service;

import com.playpro.playpro.catalog.dto.ProductStorePaymentMethodDto;
import com.playpro.playpro.catalog.dto.StorefrontPaymentMethodDto;

import java.util.List;

public interface ProductStorePaymentMethodService {

    List<ProductStorePaymentMethodDto> listForStore(String productStoreId);

    ProductStorePaymentMethodDto get(String productStoreId, String paymentMethodId);

    ProductStorePaymentMethodDto create(String productStoreId, ProductStorePaymentMethodDto dto);

    ProductStorePaymentMethodDto update(String productStoreId, String paymentMethodId, ProductStorePaymentMethodDto dto);

    void delete(String productStoreId, String paymentMethodId);

    List<StorefrontPaymentMethodDto> listEnabledForDefaultStore();
}
