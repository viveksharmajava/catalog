package com.playpro.playpro.catalog.service.impl;

import com.playpro.playpro.catalog.dto.ProductStoreShippingMethodDto;
import com.playpro.playpro.catalog.dto.StorefrontShippingMethodDto;
import com.playpro.playpro.catalog.entity.store.ProductStoreSetting;
import com.playpro.playpro.catalog.entity.store.ProductStoreShippingMethod;
import com.playpro.playpro.catalog.exception.ResourceNotFoundException;
import com.playpro.playpro.catalog.repository.ProductStoreRepository;
import com.playpro.playpro.catalog.repository.ProductStoreSettingRepository;
import com.playpro.playpro.catalog.repository.ProductStoreShippingMethodRepository;
import com.playpro.playpro.catalog.service.ProductStoreShippingMethodService;
import com.playpro.playpro.catalog.util.ShippingMethodIdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductStoreShippingMethodServiceImpl implements ProductStoreShippingMethodService {

    private static final Set<String> ALLOWED_TYPES = new HashSet<String>(Arrays.asList(
            "FLAT_RATE", "FREE_SHIPPING", "CARRIER", "PICKUP", "CUSTOM"));

    private static final Set<String> LOCAL_TYPES = new HashSet<String>(Arrays.asList(
            "FLAT_RATE", "FREE_SHIPPING", "PICKUP"));

    private final ProductStoreRepository productStoreRepository;
    private final ProductStoreSettingRepository productStoreSettingRepository;
    private final ProductStoreShippingMethodRepository shippingMethodRepository;

    public ProductStoreShippingMethodServiceImpl(ProductStoreRepository productStoreRepository,
                                                 ProductStoreSettingRepository productStoreSettingRepository,
                                                 ProductStoreShippingMethodRepository shippingMethodRepository) {
        this.productStoreRepository = productStoreRepository;
        this.productStoreSettingRepository = productStoreSettingRepository;
        this.shippingMethodRepository = shippingMethodRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductStoreShippingMethodDto> listForStore(String productStoreId) {
        requireStore(productStoreId);
        return shippingMethodRepository.findByProductStoreIdOrderBySequenceNumAscDisplayNameAsc(productStoreId)
                .stream()
                .map(this::toAdminDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductStoreShippingMethodDto get(String productStoreId, String shippingMethodId) {
        return toAdminDto(requireMethod(productStoreId, shippingMethodId));
    }

    @Override
    @Transactional
    public ProductStoreShippingMethodDto create(String productStoreId, ProductStoreShippingMethodDto dto) {
        requireStore(productStoreId);
        validate(dto);

        ProductStoreShippingMethod entity = new ProductStoreShippingMethod();
        entity.setShippingMethodId(ShippingMethodIdGenerator.nextId());
        entity.setProductStoreId(productStoreId);
        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedDate(now);
        entity.setLastModifiedDate(now);
        applyDto(entity, dto);
        return toAdminDto(shippingMethodRepository.save(entity));
    }

    @Override
    @Transactional
    public ProductStoreShippingMethodDto update(String productStoreId, String shippingMethodId,
                                                ProductStoreShippingMethodDto dto) {
        ProductStoreShippingMethod entity = requireMethod(productStoreId, shippingMethodId);
        validate(dto);
        applyDto(entity, dto);
        entity.setLastModifiedDate(LocalDateTime.now());
        return toAdminDto(shippingMethodRepository.save(entity));
    }

    @Override
    @Transactional
    public void delete(String productStoreId, String shippingMethodId) {
        ProductStoreShippingMethod entity = requireMethod(productStoreId, shippingMethodId);
        shippingMethodRepository.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StorefrontShippingMethodDto> listEnabledForDefaultStore() {
        String storeId = resolveDefaultStoreId();
        return shippingMethodRepository
                .findByProductStoreIdAndEnabledOrderBySequenceNumAscDisplayNameAsc(storeId, "Y")
                .stream()
                .map(this::toPublicDto)
                .collect(Collectors.toList());
    }

    private String resolveDefaultStoreId() {
        ProductStoreSetting setting = productStoreSettingRepository.findByIsDefaultStore("Y")
                .orElseGet(() -> productStoreSettingRepository.findAll().stream().findFirst().orElse(null));
        if (setting != null && StringUtils.hasText(setting.getProductStoreId())) {
            return setting.getProductStoreId();
        }
        return productStoreRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No product store configured"))
                .getProductStoreId();
    }

    private void requireStore(String productStoreId) {
        if (!productStoreRepository.existsById(productStoreId)) {
            throw new ResourceNotFoundException("Product store not found: " + productStoreId);
        }
    }

    private ProductStoreShippingMethod requireMethod(String productStoreId, String shippingMethodId) {
        requireStore(productStoreId);
        return shippingMethodRepository.findByShippingMethodIdAndProductStoreId(shippingMethodId, productStoreId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Shipping method not found: " + shippingMethodId + " for store " + productStoreId));
    }

    private void validate(ProductStoreShippingMethodDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Shipping method payload is required");
        }
        if (!StringUtils.hasText(dto.getShippingType())) {
            throw new IllegalArgumentException("Shipping type is required");
        }
        String type = dto.getShippingType().trim().toUpperCase(Locale.ROOT);
        if (!ALLOWED_TYPES.contains(type)) {
            throw new IllegalArgumentException(
                    "Shipping type must be one of: FLAT_RATE, FREE_SHIPPING, CARRIER, PICKUP, CUSTOM");
        }
        if (!StringUtils.hasText(dto.getDisplayName())) {
            throw new IllegalArgumentException("Display name is required");
        }
        if (!LOCAL_TYPES.contains(type) && !StringUtils.hasText(dto.getCarrierProvider())) {
            throw new IllegalArgumentException("Carrier provider is required for carrier/custom shipping methods");
        }
        if ("FLAT_RATE".equals(type) && dto.getFlatRateAmount() != null
                && dto.getFlatRateAmount().signum() < 0) {
            throw new IllegalArgumentException("Flat rate amount cannot be negative");
        }
    }

    private void applyDto(ProductStoreShippingMethod entity, ProductStoreShippingMethodDto dto) {
        String type = dto.getShippingType().trim().toUpperCase(Locale.ROOT);
        entity.setShippingType(type);
        entity.setDisplayName(dto.getDisplayName().trim());
        entity.setEnabled(dto.isEnabled() ? "Y" : "N");
        entity.setSequenceNum(dto.getSequenceNum() != null ? dto.getSequenceNum() : 0);
        entity.setFlatRateAmount(dto.getFlatRateAmount());
        entity.setTrackOrderUrl(trimToNull(dto.getTrackOrderUrl()));

        if (LOCAL_TYPES.contains(type)) {
            entity.setCarrierProvider(null);
            entity.setApiKey(null);
            entity.setApiSecret(null);
            entity.setAccountId(null);
            entity.setApiBaseUrl(null);
            entity.setCreateShipmentUrl(null);
            entity.setWebhookUrl(null);
            entity.setAccessToken(null);
            entity.setDefaultServiceCode(null);
            entity.setExtraConfig(null);
            if ("FREE_SHIPPING".equals(type) || "PICKUP".equals(type)) {
                entity.setFlatRateAmount(null);
            }
            return;
        }

        entity.setCarrierProvider(trimToNull(dto.getCarrierProvider()) == null
                ? null
                : dto.getCarrierProvider().trim().toUpperCase(Locale.ROOT));
        entity.setApiKey(trimToNull(dto.getApiKey()));
        entity.setApiSecret(trimToNull(dto.getApiSecret()));
        entity.setAccountId(trimToNull(dto.getAccountId()));
        entity.setApiBaseUrl(trimToNull(dto.getApiBaseUrl()));
        entity.setCreateShipmentUrl(trimToNull(dto.getCreateShipmentUrl()));
        entity.setWebhookUrl(trimToNull(dto.getWebhookUrl()));
        entity.setAccessToken(trimToNull(dto.getAccessToken()));
        entity.setDefaultServiceCode(trimToNull(dto.getDefaultServiceCode()));
        entity.setExtraConfig(trimToNull(dto.getExtraConfig()));
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private ProductStoreShippingMethodDto toAdminDto(ProductStoreShippingMethod entity) {
        ProductStoreShippingMethodDto dto = new ProductStoreShippingMethodDto();
        dto.setShippingMethodId(entity.getShippingMethodId());
        dto.setProductStoreId(entity.getProductStoreId());
        dto.setShippingType(entity.getShippingType());
        dto.setDisplayName(entity.getDisplayName());
        dto.setEnabled("Y".equalsIgnoreCase(entity.getEnabled()));
        dto.setSequenceNum(entity.getSequenceNum());
        dto.setCarrierProvider(entity.getCarrierProvider());
        dto.setApiKey(entity.getApiKey());
        dto.setApiSecret(entity.getApiSecret());
        dto.setAccountId(entity.getAccountId());
        dto.setApiBaseUrl(entity.getApiBaseUrl());
        dto.setTrackOrderUrl(entity.getTrackOrderUrl());
        dto.setCreateShipmentUrl(entity.getCreateShipmentUrl());
        dto.setWebhookUrl(entity.getWebhookUrl());
        dto.setAccessToken(entity.getAccessToken());
        dto.setDefaultServiceCode(entity.getDefaultServiceCode());
        dto.setFlatRateAmount(entity.getFlatRateAmount());
        dto.setExtraConfig(entity.getExtraConfig());
        return dto;
    }

    private StorefrontShippingMethodDto toPublicDto(ProductStoreShippingMethod entity) {
        StorefrontShippingMethodDto dto = new StorefrontShippingMethodDto();
        dto.setShippingMethodId(entity.getShippingMethodId());
        dto.setShippingType(entity.getShippingType());
        dto.setDisplayName(entity.getDisplayName());
        dto.setCarrierProvider(entity.getCarrierProvider());
        dto.setTrackOrderUrl(entity.getTrackOrderUrl());
        dto.setFlatRateAmount(entity.getFlatRateAmount());
        dto.setSequenceNum(entity.getSequenceNum());
        return dto;
    }
}
