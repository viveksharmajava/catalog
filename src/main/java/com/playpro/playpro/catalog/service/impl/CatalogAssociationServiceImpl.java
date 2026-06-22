package com.playpro.playpro.catalog.service.impl;

import com.playpro.playpro.catalog.dto.CategoryProdCatalogDto;
import com.playpro.playpro.catalog.dto.ProductStoreCatalogDto;
import com.playpro.playpro.catalog.entity.catalog.ProdCatalog;
import com.playpro.playpro.catalog.entity.catalog.ProdCatalogCategory;
import com.playpro.playpro.catalog.entity.catalog.ProdCatalogCategoryId;
import com.playpro.playpro.catalog.entity.category.ProductCategory;
import com.playpro.playpro.catalog.entity.store.ProductStore;
import com.playpro.playpro.catalog.entity.store.ProductStoreCatalog;
import com.playpro.playpro.catalog.entity.store.ProductStoreCatalogId;
import com.playpro.playpro.catalog.exception.ResourceNotFoundException;
import com.playpro.playpro.catalog.repository.ProdCatalogCategoryRepository;
import com.playpro.playpro.catalog.repository.ProdCatalogRepository;
import com.playpro.playpro.catalog.repository.ProductCategoryRepository;
import com.playpro.playpro.catalog.repository.ProductStoreCatalogRepository;
import com.playpro.playpro.catalog.repository.ProductStoreRepository;
import com.playpro.playpro.catalog.service.CatalogAssociationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class CatalogAssociationServiceImpl implements CatalogAssociationService {

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

    private final ProdCatalogRepository prodCatalogRepository;
    private final ProdCatalogCategoryRepository prodCatalogCategoryRepository;
    private final ProductCategoryRepository categoryRepository;
    private final ProductStoreCatalogRepository productStoreCatalogRepository;
    private final ProductStoreRepository productStoreRepository;

    public CatalogAssociationServiceImpl(ProdCatalogRepository prodCatalogRepository,
                                         ProdCatalogCategoryRepository prodCatalogCategoryRepository,
                                         ProductCategoryRepository categoryRepository,
                                         ProductStoreCatalogRepository productStoreCatalogRepository,
                                         ProductStoreRepository productStoreRepository) {
        this.prodCatalogRepository = prodCatalogRepository;
        this.prodCatalogCategoryRepository = prodCatalogCategoryRepository;
        this.categoryRepository = categoryRepository;
        this.productStoreCatalogRepository = productStoreCatalogRepository;
        this.productStoreRepository = productStoreRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryProdCatalogDto> listCategories(String prodCatalogId) {
        ensureCatalogExists(prodCatalogId);
        return prodCatalogCategoryRepository.findByIdProdCatalogIdOrderBySequenceNumAsc(prodCatalogId).stream()
                .map(this::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryProdCatalogDto addCategory(String prodCatalogId, CategoryProdCatalogDto dto) {
        ensureCatalogExists(prodCatalogId);
        ProductCategory category = categoryRepository.findById(dto.getProductCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + dto.getProductCategoryId()));
        String typeId = dto.getProdCatalogCategoryTypeId() != null
                ? dto.getProdCatalogCategoryTypeId() : "PCCT_BROWSE_ROOT";
        LocalDateTime fromDate = dto.getFromDate() != null ? dto.getFromDate() : LocalDateTime.now();
        ProdCatalogCategoryId id = new ProdCatalogCategoryId(prodCatalogId, dto.getProductCategoryId(), typeId, fromDate);
        if (prodCatalogCategoryRepository.existsById(id)) {
            throw new IllegalArgumentException("Category already associated with catalog");
        }
        ProdCatalogCategory entity = new ProdCatalogCategory();
        entity.setId(id);
        entity.setThruDate(dto.getThruDate());
        entity.setSequenceNum(dto.getSequenceNum() != null ? dto.getSequenceNum() : BigDecimal.ONE);
        prodCatalogCategoryRepository.save(entity);
        CategoryProdCatalogDto result = toCategoryDto(entity);
        result.setCategoryName(category.getCategoryName());
        return result;
    }

    @Override
    public CategoryProdCatalogDto updateCategory(CategoryProdCatalogDto dto) {
        ProdCatalogCategoryId id = new ProdCatalogCategoryId(
                dto.getProdCatalogId(), dto.getProductCategoryId(),
                dto.getProdCatalogCategoryTypeId(), dto.getFromDate());
        ProdCatalogCategory entity = prodCatalogCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catalog category association not found"));
        entity.setThruDate(dto.getThruDate());
        if (dto.getSequenceNum() != null) {
            entity.setSequenceNum(dto.getSequenceNum());
        }
        prodCatalogCategoryRepository.save(entity);
        return toCategoryDto(entity);
    }

    @Override
    public void removeCategory(CategoryProdCatalogDto dto) {
        ProdCatalogCategoryId id = new ProdCatalogCategoryId(
                dto.getProdCatalogId(), dto.getProductCategoryId(),
                dto.getProdCatalogCategoryTypeId(), dto.getFromDate());
        if (!prodCatalogCategoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Catalog category association not found");
        }
        prodCatalogCategoryRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductStoreCatalogDto> listStores(String prodCatalogId) {
        ensureCatalogExists(prodCatalogId);
        LocalDateTime now = LocalDateTime.now();
        return productStoreCatalogRepository.findByIdProdCatalogId(prodCatalogId).stream()
                .filter(m -> m.getThruDate() == null || m.getThruDate().isAfter(now))
                .map(this::toStoreDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProductStoreCatalogDto addStore(String prodCatalogId, ProductStoreCatalogDto dto) {
        ensureCatalogExists(prodCatalogId);
        ProductStore store = productStoreRepository.findById(dto.getProductStoreId())
                .orElseThrow(() -> new ResourceNotFoundException("Product store not found: " + dto.getProductStoreId()));
        LocalDateTime fromDate = dto.getFromDate() != null ? dto.getFromDate() : LocalDateTime.now();
        ProductStoreCatalogId id = new ProductStoreCatalogId(dto.getProductStoreId(), prodCatalogId, fromDate);
        if (productStoreCatalogRepository.findById(id).isPresent()) {
            throw new IllegalArgumentException("Store already linked to catalog for this from date");
        }
        ProductStoreCatalog member = new ProductStoreCatalog();
        member.setId(id);
        member.setSequenceNum(dto.getSequenceNum() != null ? dto.getSequenceNum() : BigDecimal.ONE);
        productStoreCatalogRepository.save(member);
        ProductStoreCatalogDto result = toStoreDto(member);
        result.setStoreName(store.getStoreName());
        return result;
    }

    @Override
    public void removeStore(String prodCatalogId, String productStoreId, String fromDate) {
        ensureCatalogExists(prodCatalogId);
        LocalDateTime parsedFromDate = parseFromDate(fromDate);
        ProductStoreCatalogId id = new ProductStoreCatalogId(productStoreId, prodCatalogId, parsedFromDate);
        ProductStoreCatalog member = productStoreCatalogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catalog store association not found"));
        member.setThruDate(LocalDateTime.now());
        productStoreCatalogRepository.save(member);
    }

    private CategoryProdCatalogDto toCategoryDto(ProdCatalogCategory entity) {
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
        categoryRepository.findById(entity.getId().getProductCategoryId())
                .ifPresent(c -> dto.setCategoryName(c.getCategoryName()));
        return dto;
    }

    private ProductStoreCatalogDto toStoreDto(ProductStoreCatalog member) {
        ProductStoreCatalogDto dto = new ProductStoreCatalogDto();
        dto.setProductStoreId(member.getId().getProductStoreId());
        dto.setProdCatalogId(member.getId().getProdCatalogId());
        dto.setFromDate(member.getId().getFromDate());
        dto.setThruDate(member.getThruDate());
        dto.setSequenceNum(member.getSequenceNum());
        prodCatalogRepository.findById(member.getId().getProdCatalogId())
                .ifPresent(c -> dto.setCatalogName(c.getCatalogName()));
        productStoreRepository.findById(member.getId().getProductStoreId())
                .ifPresent(s -> dto.setStoreName(s.getStoreName()));
        return dto;
    }

    private void ensureCatalogExists(String prodCatalogId) {
        if (!prodCatalogRepository.existsById(prodCatalogId)) {
            throw new ResourceNotFoundException("Product catalog not found: " + prodCatalogId);
        }
    }

    private LocalDateTime parseFromDate(String fromDate) {
        if (!StringUtils.hasText(fromDate)) {
            throw new IllegalArgumentException("fromDate is required");
        }
        try {
            return LocalDateTime.parse(fromDate);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Invalid fromDate format, expected ISO-8601 date-time");
        }
    }
}
