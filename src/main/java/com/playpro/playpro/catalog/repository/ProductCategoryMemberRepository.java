package com.playpro.playpro.catalog.repository;

import com.playpro.playpro.catalog.entity.category.ProductCategoryMember;
import com.playpro.playpro.catalog.entity.category.ProductCategoryMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductCategoryMemberRepository extends JpaRepository<ProductCategoryMember, ProductCategoryMemberId> {

    List<ProductCategoryMember> findByIdProductId(String productId);

    List<ProductCategoryMember> findByIdProductCategoryId(String productCategoryId);

    @Query("SELECT COUNT(m) FROM ProductCategoryMember m WHERE m.id.productCategoryId = :categoryId "
            + "AND (m.thruDate IS NULL OR m.thruDate > :now)")
    long countActiveMembers(@Param("categoryId") String categoryId, @Param("now") LocalDateTime now);
}
