package com.playpro.playpro.catalog.repository;

import com.playpro.playpro.catalog.entity.category.ProductCategoryRollup;
import com.playpro.playpro.catalog.entity.category.ProductCategoryRollupId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductCategoryRollupRepository extends JpaRepository<ProductCategoryRollup, ProductCategoryRollupId> {

    @Query("SELECT r FROM ProductCategoryRollup r WHERE r.id.parentProductCategoryId = :parentId "
            + "AND (r.thruDate IS NULL OR r.thruDate > :now) ORDER BY r.sequenceNum")
    List<ProductCategoryRollup> findActiveChildren(@Param("parentId") String parentId, @Param("now") LocalDateTime now);

    @Query("SELECT COUNT(r) FROM ProductCategoryRollup r WHERE r.id.parentProductCategoryId = :parentId "
            + "AND (r.thruDate IS NULL OR r.thruDate > :now)")
    long countActiveChildren(@Param("parentId") String parentId, @Param("now") LocalDateTime now);

    List<ProductCategoryRollup> findByIdProductCategoryIdOrderBySequenceNumAsc(String productCategoryId);

    List<ProductCategoryRollup> findByIdParentProductCategoryIdOrderBySequenceNumAsc(String parentProductCategoryId);
}
