package com.playpro.playpro.catalog.helper;

import com.playpro.playpro.catalog.entity.category.ProductCategory;
import com.playpro.playpro.catalog.entity.category.ProductCategoryRollup;
import com.playpro.playpro.catalog.repository.ProductCategoryMemberRepository;
import com.playpro.playpro.catalog.repository.ProductCategoryRepository;
import com.playpro.playpro.catalog.repository.ProductCategoryRollupRepository;
import com.playpro.playpro.catalog.util.IndicatorUtil;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Category domain helper inspired by Apache OFBiz {@code CategoryWorker}.
 */
@Component
public class CategoryWorker {

    private final ProductCategoryRepository categoryRepository;
    private final ProductCategoryRollupRepository rollupRepository;
    private final ProductCategoryMemberRepository memberRepository;

    public CategoryWorker(ProductCategoryRepository categoryRepository,
                          ProductCategoryRollupRepository rollupRepository,
                          ProductCategoryMemberRepository memberRepository) {
        this.categoryRepository = categoryRepository;
        this.rollupRepository = rollupRepository;
        this.memberRepository = memberRepository;
    }

    public boolean isCategoryVisible(ProductCategory category) {
        return category != null && IndicatorUtil.isYes(category.getShowInSelect());
    }

    public boolean isCategoryEmpty(String productCategoryId) {
        LocalDateTime now = LocalDateTime.now();
        long members = memberRepository.countActiveMembers(productCategoryId, now);
        if (members > 0) {
            return false;
        }
        return rollupRepository.countActiveChildren(productCategoryId, now) == 0;
    }

    public List<ProductCategory> getRelatedCategories(String parentCategoryId, boolean recursive, boolean excludeEmpty) {
        List<ProductCategory> categories = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        List<ProductCategoryRollup> rollups = rollupRepository.findActiveChildren(parentCategoryId, now);
        for (ProductCategoryRollup rollup : rollups) {
            ProductCategory category = categoryRepository.findById(rollup.getId().getProductCategoryId()).orElse(null);
            if (category == null) {
                continue;
            }
            if (excludeEmpty && isCategoryEmpty(category.getProductCategoryId())) {
                continue;
            }
            categories.add(category);
            if (recursive) {
                categories.addAll(getRelatedCategories(category.getProductCategoryId(), true, excludeEmpty));
            }
        }
        return categories;
    }

    public long categoryMemberCount(String productCategoryId) {
        return memberRepository.countActiveMembers(productCategoryId, LocalDateTime.now());
    }

    public long categoryRollupCount(String productCategoryId) {
        return rollupRepository.countActiveChildren(productCategoryId, LocalDateTime.now());
    }
}
