package com.playpro.playpro.catalog.repository;

import com.playpro.playpro.catalog.entity.store.ProductStoreVariantType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductStoreVariantTypeRepository extends JpaRepository<ProductStoreVariantType, String> {

    List<ProductStoreVariantType> findByProductStoreIdOrderBySequenceNumAscNameAsc(String productStoreId);

    Optional<ProductStoreVariantType> findByVariantTypeIdAndProductStoreId(String variantTypeId, String productStoreId);

    boolean existsByProductStoreIdAndNameIgnoreCase(String productStoreId, String name);

    boolean existsByProductStoreIdAndNameIgnoreCaseAndVariantTypeIdNot(
            String productStoreId, String name, String variantTypeId);
}
