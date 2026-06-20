package com.playpro.playpro.catalog.repository;

import com.playpro.playpro.catalog.entity.product.ProductAssoc;
import com.playpro.playpro.catalog.entity.product.ProductAssocId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductAssocRepository extends JpaRepository<ProductAssoc, ProductAssocId> {

    @Query("SELECT a FROM ProductAssoc a WHERE a.id.productId = :productId "
            + "AND a.id.productAssocTypeId = :assocType "
            + "AND (a.thruDate IS NULL OR a.thruDate > :now)")
    List<ProductAssoc> findActiveAssocsFrom(@Param("productId") String productId,
                                            @Param("assocType") String assocType,
                                            @Param("now") LocalDateTime now);

    @Query("SELECT a FROM ProductAssoc a WHERE a.id.productIdTo = :productId "
            + "AND a.id.productAssocTypeId = :assocType "
            + "AND (a.thruDate IS NULL OR a.thruDate > :now)")
    List<ProductAssoc> findActiveAssocsTo(@Param("productId") String productId,
                                          @Param("assocType") String assocType,
                                          @Param("now") LocalDateTime now);
}
