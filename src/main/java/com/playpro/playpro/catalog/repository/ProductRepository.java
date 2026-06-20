package com.playpro.playpro.catalog.repository;

import com.playpro.playpro.catalog.entity.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, String>, JpaSpecificationExecutor<Product> {

    Optional<Product> findByProductId(String productId);

    List<Product> findByProductTypeId(String productTypeId);

    List<Product> findByPrimaryProductCategoryId(String primaryProductCategoryId);

    List<Product> findByStatusId(String statusId);

    List<Product> findByIsVirtual(String isVirtual);

    List<Product> findByIsVariant(String isVariant);
}
