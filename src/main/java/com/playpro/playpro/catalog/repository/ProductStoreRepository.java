package com.playpro.playpro.catalog.repository;

import com.playpro.playpro.catalog.entity.store.ProductStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductStoreRepository extends JpaRepository<ProductStore, String>, JpaSpecificationExecutor<ProductStore> {
}
