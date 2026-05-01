package com.playpro.playpro.catalog.repository;

import com.playpro.playpro.catalog.entity.ProductCategoryMap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductCategoryMapRepository extends JpaRepository<ProductCategoryMap, Long> {
    Optional<ProductCategoryMap> findByProductIdAndCategoryId(Long productId, Long categoryId);
    List<ProductCategoryMap> findByProductId(Long productId);
    void deleteByProductIdAndCategoryId(Long productId, Long categoryId);
}
