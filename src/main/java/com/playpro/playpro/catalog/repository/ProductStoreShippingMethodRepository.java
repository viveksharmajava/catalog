package com.playpro.playpro.catalog.repository;

import com.playpro.playpro.catalog.entity.store.ProductStoreShippingMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductStoreShippingMethodRepository extends JpaRepository<ProductStoreShippingMethod, String> {

    List<ProductStoreShippingMethod> findByProductStoreIdOrderBySequenceNumAscDisplayNameAsc(String productStoreId);

    List<ProductStoreShippingMethod> findByProductStoreIdAndEnabledOrderBySequenceNumAscDisplayNameAsc(
            String productStoreId, String enabled);

    Optional<ProductStoreShippingMethod> findByShippingMethodIdAndProductStoreId(
            String shippingMethodId, String productStoreId);
}
