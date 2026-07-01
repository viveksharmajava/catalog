package com.playpro.playpro.catalog.repository;

import com.playpro.playpro.catalog.entity.store.ProductStoreSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductStoreSettingRepository extends JpaRepository<ProductStoreSetting, String> {

    Optional<ProductStoreSetting> findByIsDefaultStore(String isDefaultStore);

    List<ProductStoreSetting> findByIsDefaultStoreOrderByProductStoreIdAsc(String isDefaultStore);

    @Modifying
    @Query("UPDATE ProductStoreSetting s SET s.isDefaultStore = 'N' WHERE s.productStoreId <> ?1")
    void clearDefaultExcept(String productStoreId);
}
