package com.playpro.playpro.catalog.service.impl;

import com.playpro.playpro.catalog.dto.ProductStoreSettingDto;
import com.playpro.playpro.catalog.dto.StorefrontSettingsDto;
import com.playpro.playpro.catalog.entity.store.ProductStore;
import com.playpro.playpro.catalog.entity.store.ProductStoreCatalog;
import com.playpro.playpro.catalog.entity.store.ProductStoreSetting;
import com.playpro.playpro.catalog.exception.ResourceNotFoundException;
import com.playpro.playpro.catalog.repository.ProductStoreCatalogRepository;
import com.playpro.playpro.catalog.repository.ProductStoreRepository;
import com.playpro.playpro.catalog.repository.ProductStoreSettingRepository;
import com.playpro.playpro.catalog.service.ProductStoreSettingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductStoreSettingServiceImpl implements ProductStoreSettingService {

    private final ProductStoreRepository productStoreRepository;
    private final ProductStoreSettingRepository productStoreSettingRepository;
    private final ProductStoreCatalogRepository productStoreCatalogRepository;

    public ProductStoreSettingServiceImpl(ProductStoreRepository productStoreRepository,
                                          ProductStoreSettingRepository productStoreSettingRepository,
                                          ProductStoreCatalogRepository productStoreCatalogRepository) {
        this.productStoreRepository = productStoreRepository;
        this.productStoreSettingRepository = productStoreSettingRepository;
        this.productStoreCatalogRepository = productStoreCatalogRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductStoreSettingDto getSettings(String productStoreId) {
        ProductStore store = loadStore(productStoreId);
        ProductStoreSetting setting = productStoreSettingRepository.findById(productStoreId)
                .orElseGet(() -> newDefaultSetting(productStoreId, false));
        return toDto(store, setting);
    }

    @Override
    @Transactional
    public ProductStoreSettingDto saveSettings(String productStoreId, ProductStoreSettingDto dto) {
        ProductStore store = loadStore(productStoreId);
        ProductStoreSetting setting = productStoreSettingRepository.findById(productStoreId)
                .orElseGet(() -> newDefaultSetting(productStoreId, false));

        applyDto(setting, dto);
        setting.setLastModifiedDate(LocalDateTime.now());

        if (dto.isDefaultStore()) {
            productStoreSettingRepository.clearDefaultExcept(productStoreId);
            setting.setIsDefaultStore("Y");
        } else if (setting.getIsDefaultStore() == null) {
            setting.setIsDefaultStore("N");
        }

        ensureAtLeastOneDefaultStore(productStoreId, setting);

        ProductStoreSetting saved = productStoreSettingRepository.save(setting);
        return toDto(store, saved);
    }

    @Override
    @Transactional(readOnly = true)
    public StorefrontSettingsDto getDefaultStorefrontSettings() {
        ProductStoreSetting setting = productStoreSettingRepository.findByIsDefaultStore("Y")
                .orElseGet(() -> productStoreSettingRepository.findAll().stream().findFirst()
                        .orElse(null));

        if (setting == null) {
            ProductStore fallbackStore = productStoreRepository.findAll().stream().findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("No product store configured"));
            setting = newDefaultSetting(fallbackStore.getProductStoreId(), true);
        }

        ProductStore store = loadStore(setting.getProductStoreId());
        StorefrontSettingsDto dto = new StorefrontSettingsDto();
        dto.setProductStoreId(store.getProductStoreId());
        dto.setStoreName(store.getStoreName());
        dto.setDefaultCurrencyUomId(store.getDefaultCurrencyUomId());
        dto.setContactUsContent(setting.getContactUsContent());
        dto.setAboutUsContent(setting.getAboutUsContent());
        dto.setShippingPolicyContent(setting.getShippingPolicyContent());
        dto.setReturnsContent(setting.getReturnsContent());
        dto.setPrivacyPolicyContent(setting.getPrivacyPolicyContent());
        dto.setTermsAndConditionsContent(setting.getTermsAndConditionsContent());
        dto.setCatalogIds(listActiveCatalogIds(store.getProductStoreId()));
        return dto;
    }

    @Override
    @Transactional
    public void initializeSettingsForNewStore(String productStoreId) {
        if (productStoreSettingRepository.existsById(productStoreId)) {
            return;
        }
        boolean makeDefault = !productStoreSettingRepository.findByIsDefaultStore("Y").isPresent()
                && productStoreSettingRepository.findAll().isEmpty();
        ProductStoreSetting setting = newDefaultSetting(productStoreId, makeDefault);
        setting.setLastModifiedDate(LocalDateTime.now());
        productStoreSettingRepository.save(setting);
    }

    private void ensureAtLeastOneDefaultStore(String productStoreId, ProductStoreSetting setting) {
        if ("Y".equals(setting.getIsDefaultStore())) {
            return;
        }
        boolean anyDefault = productStoreSettingRepository.findByIsDefaultStore("Y").stream()
                .anyMatch(s -> !productStoreId.equals(s.getProductStoreId()));
        if (!anyDefault) {
            setting.setIsDefaultStore("Y");
        }
    }

    private List<String> listActiveCatalogIds(String productStoreId) {
        LocalDateTime now = LocalDateTime.now();
        return productStoreCatalogRepository.findByIdProductStoreId(productStoreId).stream()
                .filter(m -> m.getThruDate() == null || m.getThruDate().isAfter(now))
                .sorted(Comparator.comparing(
                        m -> m.getSequenceNum() != null ? m.getSequenceNum() : java.math.BigDecimal.valueOf(999),
                        Comparator.naturalOrder()))
                .map(ProductStoreCatalog::getId)
                .map(id -> id.getProdCatalogId())
                .collect(Collectors.toList());
    }

    private ProductStore loadStore(String productStoreId) {
        return productStoreRepository.findById(productStoreId)
                .orElseThrow(() -> new ResourceNotFoundException("Product store not found: " + productStoreId));
    }

    private ProductStoreSetting newDefaultSetting(String productStoreId, boolean asDefault) {
        ProductStoreSetting setting = new ProductStoreSetting();
        setting.setProductStoreId(productStoreId);
        setting.setIsDefaultStore(asDefault ? "Y" : "N");
        return setting;
    }

    private void applyDto(ProductStoreSetting setting, ProductStoreSettingDto dto) {
        setting.setContactUsContent(trimContent(dto.getContactUsContent()));
        setting.setAboutUsContent(trimContent(dto.getAboutUsContent()));
        setting.setShippingPolicyContent(trimContent(dto.getShippingPolicyContent()));
        setting.setReturnsContent(trimContent(dto.getReturnsContent()));
        setting.setPrivacyPolicyContent(trimContent(dto.getPrivacyPolicyContent()));
        setting.setTermsAndConditionsContent(trimContent(dto.getTermsAndConditionsContent()));
        if (!dto.isDefaultStore()) {
            setting.setIsDefaultStore("N");
        }
    }

    private ProductStoreSettingDto toDto(ProductStore store, ProductStoreSetting setting) {
        ProductStoreSettingDto dto = new ProductStoreSettingDto();
        dto.setProductStoreId(store.getProductStoreId());
        dto.setStoreName(store.getStoreName());
        dto.setDefaultStore("Y".equalsIgnoreCase(setting.getIsDefaultStore()));
        dto.setContactUsContent(setting.getContactUsContent());
        dto.setAboutUsContent(setting.getAboutUsContent());
        dto.setShippingPolicyContent(setting.getShippingPolicyContent());
        dto.setReturnsContent(setting.getReturnsContent());
        dto.setPrivacyPolicyContent(setting.getPrivacyPolicyContent());
        dto.setTermsAndConditionsContent(setting.getTermsAndConditionsContent());
        return dto;
    }

    private String trimContent(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}
