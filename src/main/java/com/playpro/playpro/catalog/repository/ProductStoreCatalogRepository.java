package com.playpro.playpro.catalog.repository;

import com.playpro.playpro.catalog.entity.store.ProductStoreCatalog;
import com.playpro.playpro.catalog.entity.store.ProductStoreCatalogId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductStoreCatalogRepository extends JpaRepository<ProductStoreCatalog, ProductStoreCatalogId> {

    List<ProductStoreCatalog> findByIdProductStoreId(String productStoreId);

    List<ProductStoreCatalog> findByIdProdCatalogId(String prodCatalogId);
}
