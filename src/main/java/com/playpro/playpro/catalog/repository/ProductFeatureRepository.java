package com.playpro.playpro.catalog.repository;

import com.playpro.playpro.catalog.entity.feature.ProductFeature;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductFeatureRepository extends JpaRepository<ProductFeature, String> {
}
