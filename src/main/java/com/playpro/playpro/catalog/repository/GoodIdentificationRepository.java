package com.playpro.playpro.catalog.repository;

import com.playpro.playpro.catalog.entity.product.GoodIdentification;
import com.playpro.playpro.catalog.entity.product.GoodIdentificationId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GoodIdentificationRepository extends JpaRepository<GoodIdentification, GoodIdentificationId> {

    List<GoodIdentification> findByIdProductId(String productId);

    Optional<GoodIdentification> findByIdValue(String idValue);

    Optional<GoodIdentification> findByIdGoodIdentificationTypeIdAndIdValue(String typeId, String idValue);
}
