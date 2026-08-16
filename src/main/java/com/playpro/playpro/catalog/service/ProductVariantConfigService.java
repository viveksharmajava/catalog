package com.playpro.playpro.catalog.service;

import com.playpro.playpro.catalog.dto.ProductVariantConfigDto;
import com.playpro.playpro.catalog.dto.ProductVariantConfigRequest;

public interface ProductVariantConfigService {

    ProductVariantConfigDto getConfig(String productId, String productStoreId);

    ProductVariantConfigDto saveConfig(String productId, ProductVariantConfigRequest request, String principal);

    ProductVariantConfigDto generateVariants(String productId, String principal);
}
