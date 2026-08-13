package com.playpro.playpro.catalog.service.impl;

import com.playpro.playpro.catalog.dto.ProductStorePaymentMethodDto;
import com.playpro.playpro.catalog.dto.StorefrontPaymentMethodDto;
import com.playpro.playpro.catalog.entity.store.ProductStorePaymentMethod;
import com.playpro.playpro.catalog.entity.store.ProductStoreSetting;
import com.playpro.playpro.catalog.exception.ResourceNotFoundException;
import com.playpro.playpro.catalog.repository.ProductStorePaymentMethodRepository;
import com.playpro.playpro.catalog.repository.ProductStoreRepository;
import com.playpro.playpro.catalog.repository.ProductStoreSettingRepository;
import com.playpro.playpro.catalog.service.ProductStorePaymentMethodService;
import com.playpro.playpro.catalog.util.PaymentMethodIdGenerator;
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
public class ProductStorePaymentMethodServiceImpl implements ProductStorePaymentMethodService {

    private static final Set<String> ALLOWED_TYPES = new HashSet<String>(Arrays.asList(
            "COD", "CARD", "UPI", "NET_BANKING", "WALLET", "CUSTOM"));

    private final ProductStoreRepository productStoreRepository;
    private final ProductStoreSettingRepository productStoreSettingRepository;
    private final ProductStorePaymentMethodRepository paymentMethodRepository;

    public ProductStorePaymentMethodServiceImpl(ProductStoreRepository productStoreRepository,
                                                ProductStoreSettingRepository productStoreSettingRepository,
                                                ProductStorePaymentMethodRepository paymentMethodRepository) {
        this.productStoreRepository = productStoreRepository;
        this.productStoreSettingRepository = productStoreSettingRepository;
        this.paymentMethodRepository = paymentMethodRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductStorePaymentMethodDto> listForStore(String productStoreId) {
        requireStore(productStoreId);
        return paymentMethodRepository.findByProductStoreIdOrderBySequenceNumAscDisplayNameAsc(productStoreId)
                .stream()
                .map(this::toAdminDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductStorePaymentMethodDto get(String productStoreId, String paymentMethodId) {
        return toAdminDto(requireMethod(productStoreId, paymentMethodId));
    }

    @Override
    @Transactional
    public ProductStorePaymentMethodDto create(String productStoreId, ProductStorePaymentMethodDto dto) {
        requireStore(productStoreId);
        validate(dto, true);

        ProductStorePaymentMethod entity = new ProductStorePaymentMethod();
        entity.setPaymentMethodId(PaymentMethodIdGenerator.nextId());
        entity.setProductStoreId(productStoreId);
        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedDate(now);
        entity.setLastModifiedDate(now);
        applyDto(entity, dto);
        return toAdminDto(paymentMethodRepository.save(entity));
    }

    @Override
    @Transactional
    public ProductStorePaymentMethodDto update(String productStoreId, String paymentMethodId,
                                               ProductStorePaymentMethodDto dto) {
        ProductStorePaymentMethod entity = requireMethod(productStoreId, paymentMethodId);
        validate(dto, false);
        applyDto(entity, dto);
        entity.setLastModifiedDate(LocalDateTime.now());
        return toAdminDto(paymentMethodRepository.save(entity));
    }

    @Override
    @Transactional
    public void delete(String productStoreId, String paymentMethodId) {
        ProductStorePaymentMethod entity = requireMethod(productStoreId, paymentMethodId);
        paymentMethodRepository.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StorefrontPaymentMethodDto> listEnabledForDefaultStore() {
        String storeId = resolveDefaultStoreId();
        return paymentMethodRepository
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

    private ProductStorePaymentMethod requireMethod(String productStoreId, String paymentMethodId) {
        requireStore(productStoreId);
        return paymentMethodRepository.findByPaymentMethodIdAndProductStoreId(paymentMethodId, productStoreId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment method not found: " + paymentMethodId + " for store " + productStoreId));
    }

    private void validate(ProductStorePaymentMethodDto dto, boolean creating) {
        if (dto == null) {
            throw new IllegalArgumentException("Payment method payload is required");
        }
        if (!StringUtils.hasText(dto.getPaymentType())) {
            throw new IllegalArgumentException("Payment type is required");
        }
        String type = dto.getPaymentType().trim().toUpperCase(Locale.ROOT);
        if (!ALLOWED_TYPES.contains(type)) {
            throw new IllegalArgumentException(
                    "Payment type must be one of: COD, CARD, UPI, NET_BANKING, WALLET, CUSTOM");
        }
        if (!StringUtils.hasText(dto.getDisplayName())) {
            throw new IllegalArgumentException("Display name is required");
        }
        if (!"COD".equals(type) && !StringUtils.hasText(dto.getGatewayProvider())) {
            throw new IllegalArgumentException("Gateway provider is required for non-COD payment methods");
        }
        if (creating) {
            // ok
        }
    }

    private void applyDto(ProductStorePaymentMethod entity, ProductStorePaymentMethodDto dto) {
        String type = dto.getPaymentType().trim().toUpperCase(Locale.ROOT);
        entity.setPaymentType(type);
        entity.setDisplayName(dto.getDisplayName().trim());
        entity.setEnabled(dto.isEnabled() ? "Y" : "N");
        entity.setSequenceNum(dto.getSequenceNum() != null ? dto.getSequenceNum() : 0);

        if ("COD".equals(type)) {
            entity.setGatewayProvider(null);
            entity.setApiKey(null);
            entity.setApiSecret(null);
            entity.setMerchantId(null);
            entity.setGatewayUrl(null);
            entity.setRedirectUrl(null);
            entity.setWebhookUrl(null);
            entity.setAccessToken(null);
            entity.setPublishableKey(null);
            entity.setExtraConfig(null);
            return;
        }

        entity.setGatewayProvider(trimToNull(dto.getGatewayProvider()) == null
                ? null
                : dto.getGatewayProvider().trim().toUpperCase(Locale.ROOT));
        entity.setApiKey(trimToNull(dto.getApiKey()));
        entity.setApiSecret(trimToNull(dto.getApiSecret()));
        entity.setMerchantId(trimToNull(dto.getMerchantId()));
        entity.setGatewayUrl(trimToNull(dto.getGatewayUrl()));
        entity.setRedirectUrl(trimToNull(dto.getRedirectUrl()));
        entity.setWebhookUrl(trimToNull(dto.getWebhookUrl()));
        entity.setAccessToken(trimToNull(dto.getAccessToken()));
        entity.setPublishableKey(trimToNull(dto.getPublishableKey()));
        entity.setExtraConfig(trimToNull(dto.getExtraConfig()));
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private ProductStorePaymentMethodDto toAdminDto(ProductStorePaymentMethod entity) {
        ProductStorePaymentMethodDto dto = new ProductStorePaymentMethodDto();
        dto.setPaymentMethodId(entity.getPaymentMethodId());
        dto.setProductStoreId(entity.getProductStoreId());
        dto.setPaymentType(entity.getPaymentType());
        dto.setDisplayName(entity.getDisplayName());
        dto.setEnabled("Y".equalsIgnoreCase(entity.getEnabled()));
        dto.setSequenceNum(entity.getSequenceNum());
        dto.setGatewayProvider(entity.getGatewayProvider());
        dto.setApiKey(entity.getApiKey());
        dto.setApiSecret(entity.getApiSecret());
        dto.setMerchantId(entity.getMerchantId());
        dto.setGatewayUrl(entity.getGatewayUrl());
        dto.setRedirectUrl(entity.getRedirectUrl());
        dto.setWebhookUrl(entity.getWebhookUrl());
        dto.setAccessToken(entity.getAccessToken());
        dto.setPublishableKey(entity.getPublishableKey());
        dto.setExtraConfig(entity.getExtraConfig());
        return dto;
    }

    private StorefrontPaymentMethodDto toPublicDto(ProductStorePaymentMethod entity) {
        StorefrontPaymentMethodDto dto = new StorefrontPaymentMethodDto();
        dto.setPaymentMethodId(entity.getPaymentMethodId());
        dto.setPaymentType(entity.getPaymentType());
        dto.setDisplayName(entity.getDisplayName());
        dto.setGatewayProvider(entity.getGatewayProvider());
        dto.setPublishableKey(entity.getPublishableKey());
        dto.setSequenceNum(entity.getSequenceNum());
        return dto;
    }
}
