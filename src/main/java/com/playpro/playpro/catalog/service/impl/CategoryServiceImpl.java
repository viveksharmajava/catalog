package com.playpro.playpro.catalog.service.impl;

import com.playpro.playpro.catalog.dto.ProductCategoryDto;
import com.playpro.playpro.catalog.entity.category.ProductCategory;
import com.playpro.playpro.catalog.entity.category.ProductCategoryRollup;
import com.playpro.playpro.catalog.entity.category.ProductCategoryRollupId;
import com.playpro.playpro.catalog.exception.ResourceNotFoundException;
import com.playpro.playpro.catalog.helper.CategoryWorker;
import com.playpro.playpro.catalog.mapper.ProductMapper;
import com.playpro.playpro.catalog.repository.ProductCategoryRepository;
import com.playpro.playpro.catalog.repository.ProductCategoryRollupRepository;
import com.playpro.playpro.catalog.service.CategoryService;
import com.playpro.playpro.catalog.util.IndicatorUtil;
import com.playpro.playpro.catalog.util.ProductIdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final ProductCategoryRepository categoryRepository;
    private final ProductCategoryRollupRepository rollupRepository;
    private final CategoryWorker categoryWorker;

    public CategoryServiceImpl(ProductCategoryRepository categoryRepository,
                               ProductCategoryRollupRepository rollupRepository,
                               CategoryWorker categoryWorker) {
        this.categoryRepository = categoryRepository;
        this.rollupRepository = rollupRepository;
        this.categoryWorker = categoryWorker;
    }

    @Override
    public ProductCategoryDto createCategory(ProductCategoryDto dto, String principal) {
        if (dto.getCategoryName() == null || dto.getCategoryName().trim().isEmpty()) {
            throw new IllegalArgumentException("categoryName is required");
        }
        ProductCategory category = new ProductCategory();
        category.setProductCategoryId(dto.getProductCategoryId() != null ? dto.getProductCategoryId() : ProductIdGenerator.nextCategoryId());
        category.setProductCategoryTypeId(dto.getProductCategoryTypeId() != null ? dto.getProductCategoryTypeId() : "CATALOG_CATEGORY");
        category.setCategoryName(dto.getCategoryName());
        category.setDescription(dto.getDescription());
        category.setLongDescription(dto.getLongDescription());
        category.setCategoryImageUrl(dto.getCategoryImageUrl());
        category.setShowInSelect(dto.getShowInSelect() != null ? IndicatorUtil.toIndicator(dto.getShowInSelect()) : IndicatorUtil.YES);
        category.setPrimaryParentCategoryId(dto.getPrimaryParentCategoryId());
        category.applyAuditOnCreate(principal);
        categoryRepository.save(category);

        if (dto.getPrimaryParentCategoryId() != null) {
            linkCategoryToParent(category.getProductCategoryId(), dto.getPrimaryParentCategoryId());
        }
        return enrichCategoryDto(category);
    }

    @Override
    public ProductCategoryDto updateCategory(String categoryId, ProductCategoryDto dto, String principal) {
        ProductCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + categoryId));
        if (dto.getCategoryName() != null) {
            category.setCategoryName(dto.getCategoryName());
        }
        if (dto.getDescription() != null) {
            category.setDescription(dto.getDescription());
        }
        if (dto.getLongDescription() != null) {
            category.setLongDescription(dto.getLongDescription());
        }
        if (dto.getCategoryImageUrl() != null) {
            category.setCategoryImageUrl(dto.getCategoryImageUrl());
        }
        if (dto.getShowInSelect() != null) {
            category.setShowInSelect(IndicatorUtil.toIndicator(dto.getShowInSelect()));
        }
        category.applyAuditOnUpdate(principal);
        categoryRepository.save(category);
        return enrichCategoryDto(category);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductCategoryDto getCategory(String categoryId) {
        ProductCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + categoryId));
        return enrichCategoryDto(category);
    }

    @Override
    public void deleteCategory(String categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category not found: " + categoryId);
        }
        if (!categoryWorker.isCategoryEmpty(categoryId)) {
            throw new IllegalArgumentException("Category is not empty and cannot be deleted: " + categoryId);
        }
        categoryRepository.deleteById(categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductCategoryDto> getCategoryTree(String rootCategoryId, boolean excludeEmpty) {
        List<ProductCategory> children = categoryWorker.getRelatedCategories(rootCategoryId, true, excludeEmpty);
        return children.stream().map(this::enrichCategoryDto).collect(Collectors.toList());
    }

    private void linkCategoryToParent(String categoryId, String parentCategoryId) {
        ensureCategoryExists(parentCategoryId);
        LocalDateTime now = LocalDateTime.now();
        ProductCategoryRollupId rollupId = new ProductCategoryRollupId(categoryId, parentCategoryId, now);
        if (rollupRepository.findById(rollupId).isPresent()) {
            return;
        }
        ProductCategoryRollup rollup = new ProductCategoryRollup();
        rollup.setId(rollupId);
        rollup.setSequenceNum(BigDecimal.ONE);
        rollupRepository.save(rollup);
    }

    private void ensureCategoryExists(String categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category not found: " + categoryId);
        }
    }

    private ProductCategoryDto enrichCategoryDto(ProductCategory category) {
        ProductCategoryDto dto = ProductMapper.toCategoryDto(category);
        dto.setMemberCount(categoryWorker.categoryMemberCount(category.getProductCategoryId()));
        dto.setChildCategoryCount(categoryWorker.categoryRollupCount(category.getProductCategoryId()));
        return dto;
    }
}
