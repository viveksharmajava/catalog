package com.playpro.playpro.catalog.repository;

import com.playpro.playpro.catalog.entity.store.ProductStoreVariantValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductStoreVariantValueRepository extends JpaRepository<ProductStoreVariantValue, String> {

    List<ProductStoreVariantValue> findByVariantTypeIdOrderBySequenceNumAscValueAsc(String variantTypeId);

    Optional<ProductStoreVariantValue> findByVariantValueIdAndVariantTypeIdAndProductStoreId(
            String variantValueId, String variantTypeId, String productStoreId);

    boolean existsByVariantTypeIdAndValueIgnoreCase(String variantTypeId, String value);

    boolean existsByVariantTypeIdAndValueIgnoreCaseAndVariantValueIdNot(
            String variantTypeId, String value, String variantValueId);

    void deleteByVariantTypeId(String variantTypeId);

    long countByVariantTypeId(String variantTypeId);
}
