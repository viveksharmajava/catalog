package com.playpro.playpro.catalog.service.impl;

import com.playpro.playpro.catalog.dto.ProductStoreCatalogDto;
import com.playpro.playpro.catalog.dto.ProductStoreDto;
import com.playpro.playpro.catalog.dto.ProductStoreSummaryDto;
import com.playpro.playpro.catalog.entity.catalog.ProdCatalog;
import com.playpro.playpro.catalog.entity.store.ProductStore;
import com.playpro.playpro.catalog.entity.store.ProductStoreCatalog;
import com.playpro.playpro.catalog.entity.store.ProductStoreCatalogId;
import com.playpro.playpro.catalog.exception.ResourceNotFoundException;
import com.playpro.playpro.catalog.mapper.ProductStoreMapper;
import com.playpro.playpro.catalog.repository.ProdCatalogRepository;
import com.playpro.playpro.catalog.repository.ProductStoreCatalogRepository;
import com.playpro.playpro.catalog.repository.ProductStoreRepository;
import com.playpro.playpro.catalog.repository.ProductStoreSettingRepository;
import com.playpro.playpro.catalog.service.ProductStoreService;
import com.playpro.playpro.catalog.service.ProductStoreSettingService;
import com.playpro.playpro.catalog.util.ProductIdGenerator;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductStoreServiceImpl implements ProductStoreService {

    private final ProductStoreRepository productStoreRepository;
    private final ProductStoreCatalogRepository productStoreCatalogRepository;
    private final ProdCatalogRepository prodCatalogRepository;
    private final ProductStoreSettingRepository productStoreSettingRepository;
    private final ProductStoreSettingService productStoreSettingService;

    public ProductStoreServiceImpl(ProductStoreRepository productStoreRepository,
                                   ProductStoreCatalogRepository productStoreCatalogRepository,
                                   ProdCatalogRepository prodCatalogRepository,
                                   ProductStoreSettingRepository productStoreSettingRepository,
                                   ProductStoreSettingService productStoreSettingService) {
        this.productStoreRepository = productStoreRepository;
        this.productStoreCatalogRepository = productStoreCatalogRepository;
        this.prodCatalogRepository = prodCatalogRepository;
        this.productStoreSettingRepository = productStoreSettingRepository;
        this.productStoreSettingService = productStoreSettingService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductStoreSummaryDto> listStores() {
        return productStoreRepository.findAll(Sort.by(Sort.Direction.ASC, "storeName")).stream()
                .map(store -> {
                    ProductStoreSummaryDto dto = ProductStoreMapper.toSummaryDto(store);
                    productStoreSettingRepository.findById(store.getProductStoreId())
                            .ifPresent(setting -> dto.setDefaultStore("Y".equalsIgnoreCase(setting.getIsDefaultStore())));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductStoreDto getStore(String productStoreId) {
        return ProductStoreMapper.toDto(loadStore(productStoreId));
    }

    @Override
    public ProductStoreDto createStore(ProductStoreDto dto) {
        if (!StringUtils.hasText(dto.getStoreName())) {
            throw new IllegalArgumentException("Store name is required");
        }

        String storeId = StringUtils.hasText(dto.getProductStoreId())
                ? dto.getProductStoreId().trim()
                : ProductIdGenerator.nextProductStoreId();
        if (productStoreRepository.existsById(storeId)) {
            throw new IllegalArgumentException("Product store already exists: " + storeId);
        }

        ProductStore entity = new ProductStore();
        entity.setProductStoreId(storeId);
        ProductStoreMapper.applyDtoToEntity(dto, entity);
        ProductStore saved = productStoreRepository.save(entity);
        productStoreSettingService.initializeSettingsForNewStore(saved.getProductStoreId());
        return ProductStoreMapper.toDto(saved);
    }

    @Override
    public ProductStoreDto updateStore(String productStoreId, ProductStoreDto dto) {
        if (!StringUtils.hasText(dto.getStoreName())) {
            throw new IllegalArgumentException("Store name is required");
        }
        ProductStore entity = loadStore(productStoreId);
        ProductStoreMapper.applyDtoToEntity(dto, entity);
        return ProductStoreMapper.toDto(productStoreRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductStoreCatalogDto> listStoreCatalogs(String productStoreId) {
        loadStore(productStoreId);
        LocalDateTime now = LocalDateTime.now();
        return productStoreCatalogRepository.findByIdProductStoreId(productStoreId).stream()
                .filter(m -> m.getThruDate() == null || m.getThruDate().isAfter(now))
                .map(m -> {
                    ProdCatalog catalog = prodCatalogRepository.findById(m.getId().getProdCatalogId()).orElse(null);
                    return ProductStoreMapper.toCatalogDto(m, catalog);
                })
                .collect(Collectors.toList());
    }

    @Override
    public ProductStoreCatalogDto addStoreCatalog(String productStoreId, ProductStoreCatalogDto dto) {
        loadStore(productStoreId);
        if (!StringUtils.hasText(dto.getProdCatalogId())) {
            throw new IllegalArgumentException("prodCatalogId is required");
        }
        ProdCatalog catalog = prodCatalogRepository.findById(dto.getProdCatalogId())
                .orElseThrow(() -> new ResourceNotFoundException("Product catalog not found: " + dto.getProdCatalogId()));

        LocalDateTime fromDate = dto.getFromDate() != null ? dto.getFromDate() : LocalDateTime.now();
        ProductStoreCatalogId id = new ProductStoreCatalogId(productStoreId, dto.getProdCatalogId(), fromDate);
        if (productStoreCatalogRepository.findById(id).isPresent()) {
            throw new IllegalArgumentException("Catalog already linked to store for this from date");
        }

        ProductStoreCatalog member = new ProductStoreCatalog();
        member.setId(id);
        member.setSequenceNum(dto.getSequenceNum() != null ? dto.getSequenceNum() : BigDecimal.ONE);
        productStoreCatalogRepository.save(member);
        return ProductStoreMapper.toCatalogDto(member, catalog);
    }

    @Override
    public void removeStoreCatalog(String productStoreId, String prodCatalogId, String fromDate) {
        loadStore(productStoreId);
        LocalDateTime parsedFromDate = parseFromDate(fromDate);
        ProductStoreCatalogId id = new ProductStoreCatalogId(productStoreId, prodCatalogId, parsedFromDate);
        ProductStoreCatalog member = productStoreCatalogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Store catalog association not found"));
        member.setThruDate(LocalDateTime.now());
        productStoreCatalogRepository.save(member);
    }

    private ProductStore loadStore(String productStoreId) {
        return productStoreRepository.findById(productStoreId)
                .orElseThrow(() -> new ResourceNotFoundException("Product store not found: " + productStoreId));
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
