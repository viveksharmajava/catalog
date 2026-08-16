package com.playpro.playpro.catalog.repository;

import com.playpro.playpro.catalog.entity.product.ProductVariantOptionValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductVariantOptionValueRepository extends JpaRepository<ProductVariantOptionValue, ProductVariantOptionValue.Pk> {

    List<ProductVariantOptionValue> findByProductId(String productId);

    List<ProductVariantOptionValue> findByProductIdAndVariantTypeId(String productId, String variantTypeId);

    void deleteByProductId(String productId);
}
