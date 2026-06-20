package com.playpro.playpro.catalog.repository;

import com.playpro.playpro.catalog.entity.product.ProductAttribute;
import com.playpro.playpro.catalog.entity.product.ProductAttributeId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, ProductAttributeId> {

    List<ProductAttribute> findByIdProductId(String productId);
}
