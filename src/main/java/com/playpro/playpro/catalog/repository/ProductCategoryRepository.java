package com.playpro.playpro.catalog.repository;

import com.playpro.playpro.catalog.entity.category.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, String>,
        JpaSpecificationExecutor<ProductCategory> {

    List<ProductCategory> findByPrimaryParentCategoryId(String primaryParentCategoryId);

    List<ProductCategory> findByProductCategoryTypeId(String productCategoryTypeId);
}
