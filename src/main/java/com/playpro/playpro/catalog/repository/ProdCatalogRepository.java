package com.playpro.playpro.catalog.repository;

import com.playpro.playpro.catalog.entity.catalog.ProdCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProdCatalogRepository extends JpaRepository<ProdCatalog, String>, JpaSpecificationExecutor<ProdCatalog> {
}
