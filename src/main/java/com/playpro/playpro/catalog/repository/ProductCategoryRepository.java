package com.playpro.playpro.catalog.repository;

import com.playpro.playpro.catalog.entity.category.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, String> {

    List<ProductCategory> findByPrimaryParentCategoryId(String primaryParentCategoryId);

    List<ProductCategory> findByProductCategoryTypeId(String productCategoryTypeId);
}
