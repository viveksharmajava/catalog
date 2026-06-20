package com.playpro.playpro.catalog.repository;

import com.playpro.playpro.catalog.entity.product.ProductKeyword;
import com.playpro.playpro.catalog.entity.product.ProductKeywordId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductKeywordRepository extends JpaRepository<ProductKeyword, ProductKeywordId> {

    List<ProductKeyword> findByIdProductId(String productId);

    List<ProductKeyword> findByIdKeywordContainingIgnoreCase(String keyword);
}
