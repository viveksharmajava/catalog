package com.playpro.playpro.catalog.repository;

import com.playpro.playpro.catalog.entity.product.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductTypeRepository extends JpaRepository<ProductType, String> {
}
