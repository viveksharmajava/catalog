package com.playpro.playpro.catalog.repository;

import com.playpro.playpro.catalog.entity.catalog.ProdCatalogCategory;
import com.playpro.playpro.catalog.entity.catalog.ProdCatalogCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProdCatalogCategoryRepository extends JpaRepository<ProdCatalogCategory, ProdCatalogCategoryId> {

    List<ProdCatalogCategory> findByIdProductCategoryIdOrderBySequenceNumAsc(String productCategoryId);

    List<ProdCatalogCategory> findByIdProdCatalogIdOrderBySequenceNumAsc(String prodCatalogId);
}
