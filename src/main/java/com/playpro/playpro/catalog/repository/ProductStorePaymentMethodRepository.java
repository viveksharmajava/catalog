package com.playpro.playpro.catalog.repository;

import com.playpro.playpro.catalog.entity.store.ProductStorePaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductStorePaymentMethodRepository extends JpaRepository<ProductStorePaymentMethod, String> {

    List<ProductStorePaymentMethod> findByProductStoreIdOrderBySequenceNumAscDisplayNameAsc(String productStoreId);

    List<ProductStorePaymentMethod> findByProductStoreIdAndEnabledOrderBySequenceNumAscDisplayNameAsc(
            String productStoreId, String enabled);

    Optional<ProductStorePaymentMethod> findByPaymentMethodIdAndProductStoreId(String paymentMethodId, String productStoreId);
}
