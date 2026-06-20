package com.playpro.playpro.catalog.service.impl;

import com.playpro.playpro.catalog.dto.CategoryProdCatalogDto;
import com.playpro.playpro.catalog.dto.CategoryProductMemberDto;
import com.playpro.playpro.catalog.dto.CategoryRollupDto;
import com.playpro.playpro.catalog.entity.catalog.ProdCatalog;
import com.playpro.playpro.catalog.entity.catalog.ProdCatalogCategory;
import com.playpro.playpro.catalog.entity.catalog.ProdCatalogCategoryId;
import com.playpro.playpro.catalog.entity.category.ProductCategory;
import com.playpro.playpro.catalog.entity.category.ProductCategoryMember;
import com.playpro.playpro.catalog.entity.category.ProductCategoryMemberId;
import com.playpro.playpro.catalog.entity.category.ProductCategoryRollup;
import com.playpro.playpro.catalog.entity.category.ProductCategoryRollupId;
import com.playpro.playpro.catalog.entity.product.Product;
import com.playpro.playpro.catalog.exception.ResourceNotFoundException;
import com.playpro.playpro.catalog.repository.ProdCatalogCategoryRepository;
import com.playpro.playpro.catalog.repository.ProdCatalogRepository;
import com.playpro.playpro.catalog.repository.ProductCategoryMemberRepository;
import com.playpro.playpro.catalog.repository.ProductCategoryRepository;
import com.playpro.playpro.catalog.repository.ProductCategoryRollupRepository;
import com.playpro.playpro.catalog.repository.ProductRepository;
import com.playpro.playpro.catalog.service.CategoryAssociationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryAssociationServiceImpl implements CategoryAssociationService {

    private static final Map<String, String> CATALOG_CATEGORY_TYPES = new HashMap<>();

    static {
        CATALOG_CATEGORY_TYPES.put("PCCT_BROWSE_ROOT", "Browse Root (One)");
        CATALOG_CATEGORY_TYPES.put("PCCT_SEARCH", "Default Search (One)");
        CATALOG_CATEGORY_TYPES.put("PCCT_OTHER_SEARCH", "Other Search (Many)");
        CATALOG_CATEGORY_TYPES.put("PCCT_QUICK_ADD", "Quick Add (Many)");
        CATALOG_CATEGORY_TYPES.put("PCCT_PROMOTIONS", "Promotions (One)");
        CATALOG_CATEGORY_TYPES.put("PCCT_MOST_POPULAR", "Most Popular (One)");
        CATALOG_CATEGORY_TYPES.put("PCCT_WHATS_NEW", "What's New (One)");
    }

    private final ProductCategoryRepository categoryRepository;
    private final ProductCategoryRollupRepository rollupRepository;
    private final ProductCategoryMemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final ProdCatalogCategoryRepository prodCatalogCategoryRepository;
    private final ProdCatalogRepository prodCatalogRepository;

    public CategoryAssociationServiceImpl(ProductCategoryRepository categoryRepository,
                                          ProductCategoryRollupRepository rollupRepository,
                                          ProductCategoryMemberRepository memberRepository,
                                          ProductRepository productRepository,
                                          ProdCatalogCategoryRepository prodCatalogCategoryRepository,
                                          ProdCatalogRepository prodCatalogRepository) {
        this.categoryRepository = categoryRepository;
        this.rollupRepository = rollupRepository;
        this.memberRepository = memberRepository;
        this.productRepository = productRepository;
        this.prodCatalogCategoryRepository = prodCatalogCategoryRepository;
        this.prodCatalogRepository = prodCatalogRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryRollupDto> listParentRollups(String categoryId) {
        ensureCategoryExists(categoryId);
        return rollupRepository.findByIdProductCategoryIdOrderBySequenceNumAsc(categoryId).stream()
                .map(r -> toRollupDto(r, true))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryRollupDto> listChildRollups(String categoryId) {
        ensureCategoryExists(categoryId);
        return rollupRepository.findByIdParentProductCategoryIdOrderBySequenceNumAsc(categoryId).stream()
                .map(r -> toRollupDto(r, false))
                .collect(Collectors.toList());
    }

    @Override
    public CategoryRollupDto addParentRollup(String categoryId, CategoryRollupDto dto) {
        ensureCategoryExists(categoryId);
        ensureCategoryExists(dto.getParentProductCategoryId());
        if (categoryId.equals(dto.getParentProductCategoryId())) {
            throw new IllegalArgumentException("Category cannot be its own parent");
        }
        LocalDateTime fromDate = dto.getFromDate() != null ? dto.getFromDate() : LocalDateTime.now();
        ProductCategoryRollupId id = new ProductCategoryRollupId(categoryId, dto.getParentProductCategoryId(), fromDate);
        if (rollupRepository.existsById(id)) {
            throw new IllegalArgumentException("Parent rollup already exists");
        }
        ProductCategoryRollup rollup = new ProductCategoryRollup();
        rollup.setId(id);
        rollup.setThruDate(dto.getThruDate());
        rollup.setSequenceNum(dto.getSequenceNum() != null ? dto.getSequenceNum() : BigDecimal.ONE);
        rollupRepository.save(rollup);
        return toRollupDto(rollup, true);
    }

    @Override
    public CategoryRollupDto addChildRollup(String categoryId, CategoryRollupDto dto) {
        ensureCategoryExists(categoryId);
        String childId = dto.getProductCategoryId();
        ensureCategoryExists(childId);
        if (categoryId.equals(childId)) {
            throw new IllegalArgumentException("Category cannot be its own child");
        }
        LocalDateTime fromDate = dto.getFromDate() != null ? dto.getFromDate() : LocalDateTime.now();
        ProductCategoryRollupId id = new ProductCategoryRollupId(childId, categoryId, fromDate);
        if (rollupRepository.existsById(id)) {
            throw new IllegalArgumentException("Child rollup already exists");
        }
        ProductCategoryRollup rollup = new ProductCategoryRollup();
        rollup.setId(id);
        rollup.setThruDate(dto.getThruDate());
        rollup.setSequenceNum(dto.getSequenceNum() != null ? dto.getSequenceNum() : BigDecimal.ONE);
        rollupRepository.save(rollup);
        return toRollupDto(rollup, false);
    }

    @Override
    public CategoryRollupDto updateRollup(CategoryRollupDto dto) {
        ProductCategoryRollupId id = new ProductCategoryRollupId(
                dto.getProductCategoryId(), dto.getParentProductCategoryId(), dto.getFromDate());
        ProductCategoryRollup rollup = rollupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category rollup not found"));
        rollup.setThruDate(dto.getThruDate());
        if (dto.getSequenceNum() != null) {
            rollup.setSequenceNum(dto.getSequenceNum());
        }
        rollupRepository.save(rollup);
        return toRollupDto(rollup, true);
    }

    @Override
    public void removeRollup(CategoryRollupDto dto) {
        ProductCategoryRollupId id = new ProductCategoryRollupId(
                dto.getProductCategoryId(), dto.getParentProductCategoryId(), dto.getFromDate());
        if (!rollupRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category rollup not found");
        }
        rollupRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryProductMemberDto> listProductMembers(String categoryId) {
        ensureCategoryExists(categoryId);
        return memberRepository.findByIdProductCategoryId(categoryId).stream()
                .map(this::toMemberDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryProductMemberDto addProductMember(String categoryId, CategoryProductMemberDto dto) {
        ensureCategoryExists(categoryId);
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + dto.getProductId()));
        LocalDateTime fromDate = dto.getFromDate() != null ? dto.getFromDate() : LocalDateTime.now();
        ProductCategoryMemberId id = new ProductCategoryMemberId(categoryId, dto.getProductId(), fromDate);
        if (memberRepository.existsById(id)) {
            throw new IllegalArgumentException("Product already associated with category");
        }
        ProductCategoryMember member = new ProductCategoryMember();
        member.setId(id);
        member.setThruDate(dto.getThruDate());
        member.setSequenceNum(dto.getSequenceNum() != null ? dto.getSequenceNum() : BigDecimal.ONE);
        member.setQuantity(dto.getQuantity());
        member.setComments(dto.getComments());
        memberRepository.save(member);
        CategoryProductMemberDto result = toMemberDto(member);
        result.setProductName(product.getProductName());
        result.setInternalName(product.getInternalName());
        return result;
    }

    @Override
    public CategoryProductMemberDto updateProductMember(CategoryProductMemberDto dto) {
        ProductCategoryMemberId id = new ProductCategoryMemberId(
                dto.getProductCategoryId(), dto.getProductId(), dto.getFromDate());
        ProductCategoryMember member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category product member not found"));
        member.setThruDate(dto.getThruDate());
        if (dto.getSequenceNum() != null) {
            member.setSequenceNum(dto.getSequenceNum());
        }
        member.setQuantity(dto.getQuantity());
        member.setComments(dto.getComments());
        memberRepository.save(member);
        return toMemberDto(member);
    }

    @Override
    public void removeProductMember(CategoryProductMemberDto dto) {
        ProductCategoryMemberId id = new ProductCategoryMemberId(
                dto.getProductCategoryId(), dto.getProductId(), dto.getFromDate());
        if (!memberRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category product member not found");
        }
        memberRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryProdCatalogDto> listProdCatalogs(String categoryId) {
        ensureCategoryExists(categoryId);
        return prodCatalogCategoryRepository.findByIdProductCategoryIdOrderBySequenceNumAsc(categoryId).stream()
                .map(this::toProdCatalogDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryProdCatalogDto addProdCatalog(String categoryId, CategoryProdCatalogDto dto) {
        ensureCategoryExists(categoryId);
        ProdCatalog catalog = prodCatalogRepository.findById(dto.getProdCatalogId())
                .orElseThrow(() -> new ResourceNotFoundException("Catalog not found: " + dto.getProdCatalogId()));
        String typeId = dto.getProdCatalogCategoryTypeId() != null
                ? dto.getProdCatalogCategoryTypeId() : "PCCT_BROWSE_ROOT";
        LocalDateTime fromDate = dto.getFromDate() != null ? dto.getFromDate() : LocalDateTime.now();
        ProdCatalogCategoryId id = new ProdCatalogCategoryId(dto.getProdCatalogId(), categoryId, typeId, fromDate);
        if (prodCatalogCategoryRepository.existsById(id)) {
            throw new IllegalArgumentException("Catalog already associated with category");
        }
        ProdCatalogCategory entity = new ProdCatalogCategory();
        entity.setId(id);
        entity.setThruDate(dto.getThruDate());
        entity.setSequenceNum(dto.getSequenceNum() != null ? dto.getSequenceNum() : BigDecimal.ONE);
        prodCatalogCategoryRepository.save(entity);
        CategoryProdCatalogDto result = toProdCatalogDto(entity);
        result.setCatalogName(catalog.getCatalogName());
        return result;
    }

    @Override
    public CategoryProdCatalogDto updateProdCatalog(CategoryProdCatalogDto dto) {
        ProdCatalogCategoryId id = new ProdCatalogCategoryId(
                dto.getProdCatalogId(), dto.getProductCategoryId(),
                dto.getProdCatalogCategoryTypeId(), dto.getFromDate());
        ProdCatalogCategory entity = prodCatalogCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category catalog association not found"));
        entity.setThruDate(dto.getThruDate());
        if (dto.getSequenceNum() != null) {
            entity.setSequenceNum(dto.getSequenceNum());
        }
        prodCatalogCategoryRepository.save(entity);
        return toProdCatalogDto(entity);
    }

    @Override
    public void removeProdCatalog(CategoryProdCatalogDto dto) {
        ProdCatalogCategoryId id = new ProdCatalogCategoryId(
                dto.getProdCatalogId(), dto.getProductCategoryId(),
                dto.getProdCatalogCategoryTypeId(), dto.getFromDate());
        if (!prodCatalogCategoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category catalog association not found");
        }
        prodCatalogCategoryRepository.deleteById(id);
    }

    private CategoryRollupDto toRollupDto(ProductCategoryRollup rollup, boolean parentView) {
        CategoryRollupDto dto = new CategoryRollupDto();
        dto.setProductCategoryId(rollup.getId().getProductCategoryId());
        dto.setParentProductCategoryId(rollup.getId().getParentProductCategoryId());
        dto.setFromDate(rollup.getId().getFromDate());
        dto.setThruDate(rollup.getThruDate());
        dto.setSequenceNum(rollup.getSequenceNum());
        categoryRepository.findById(rollup.getId().getProductCategoryId())
                .ifPresent(c -> dto.setCategoryName(c.getCategoryName()));
        categoryRepository.findById(rollup.getId().getParentProductCategoryId())
                .ifPresent(c -> dto.setParentCategoryName(c.getCategoryName()));
        if (!parentView) {
            dto.setCategoryName(dto.getCategoryName());
        }
        return dto;
    }

    private CategoryProductMemberDto toMemberDto(ProductCategoryMember member) {
        CategoryProductMemberDto dto = new CategoryProductMemberDto();
        dto.setProductCategoryId(member.getId().getProductCategoryId());
        dto.setProductId(member.getId().getProductId());
        dto.setFromDate(member.getId().getFromDate());
        dto.setThruDate(member.getThruDate());
        dto.setSequenceNum(member.getSequenceNum());
        dto.setQuantity(member.getQuantity());
        dto.setComments(member.getComments());
        productRepository.findById(member.getId().getProductId()).ifPresent(p -> {
            dto.setProductName(p.getProductName());
            dto.setInternalName(p.getInternalName());
        });
        return dto;
    }

    private CategoryProdCatalogDto toProdCatalogDto(ProdCatalogCategory entity) {
        CategoryProdCatalogDto dto = new CategoryProdCatalogDto();
        dto.setProdCatalogId(entity.getId().getProdCatalogId());
        dto.setProductCategoryId(entity.getId().getProductCategoryId());
        dto.setProdCatalogCategoryTypeId(entity.getId().getProdCatalogCategoryTypeId());
        dto.setFromDate(entity.getId().getFromDate());
        dto.setThruDate(entity.getThruDate());
        dto.setSequenceNum(entity.getSequenceNum());
        dto.setProdCatalogCategoryTypeDescription(
                CATALOG_CATEGORY_TYPES.getOrDefault(entity.getId().getProdCatalogCategoryTypeId(),
                        entity.getId().getProdCatalogCategoryTypeId()));
        prodCatalogRepository.findById(entity.getId().getProdCatalogId())
                .ifPresent(c -> dto.setCatalogName(c.getCatalogName()));
        return dto;
    }

    private void ensureCategoryExists(String categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category not found: " + categoryId);
        }
    }
}
