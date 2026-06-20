package com.playpro.playpro.catalog.repository;

import com.playpro.playpro.catalog.entity.feature.ProductFeatureAppl;
import com.playpro.playpro.catalog.entity.feature.ProductFeatureApplId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductFeatureApplRepository extends JpaRepository<ProductFeatureAppl, ProductFeatureApplId> {

    @Query("SELECT f FROM ProductFeatureAppl f WHERE f.id.productId = :productId "
            + "AND (f.thruDate IS NULL OR f.thruDate > :now)")
    List<ProductFeatureAppl> findActiveByProductId(@Param("productId") String productId, @Param("now") LocalDateTime now);
}
