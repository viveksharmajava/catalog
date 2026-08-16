package com.playpro.playpro.catalog.repository;

import com.playpro.playpro.catalog.entity.product.ProductVariantOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductVariantOptionRepository extends JpaRepository<ProductVariantOption, ProductVariantOption.Pk> {

    List<ProductVariantOption> findByProductIdOrderBySequenceNumAsc(String productId);

    void deleteByProductId(String productId);
}
