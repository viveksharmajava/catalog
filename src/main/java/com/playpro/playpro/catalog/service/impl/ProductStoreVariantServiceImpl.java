package com.playpro.playpro.catalog.service.impl;

import com.playpro.playpro.catalog.dto.ProductStoreVariantTypeDto;
import com.playpro.playpro.catalog.dto.ProductStoreVariantValueDto;
import com.playpro.playpro.catalog.entity.store.ProductStoreVariantType;
import com.playpro.playpro.catalog.entity.store.ProductStoreVariantValue;
import com.playpro.playpro.catalog.exception.ResourceNotFoundException;
import com.playpro.playpro.catalog.repository.ProductStoreRepository;
import com.playpro.playpro.catalog.repository.ProductStoreVariantTypeRepository;
import com.playpro.playpro.catalog.repository.ProductStoreVariantValueRepository;
import com.playpro.playpro.catalog.service.ProductStoreVariantService;
import com.playpro.playpro.catalog.util.VariantIdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class ProductStoreVariantServiceImpl implements ProductStoreVariantService {

    private final ProductStoreRepository productStoreRepository;
    private final ProductStoreVariantTypeRepository variantTypeRepository;
    private final ProductStoreVariantValueRepository variantValueRepository;

    public ProductStoreVariantServiceImpl(ProductStoreRepository productStoreRepository,
                                           ProductStoreVariantTypeRepository variantTypeRepository,
                                           ProductStoreVariantValueRepository variantValueRepository) {
        this.productStoreRepository = productStoreRepository;
        this.variantTypeRepository = variantTypeRepository;
        this.variantValueRepository = variantValueRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductStoreVariantTypeDto> listTypes(String productStoreId) {
        requireStore(productStoreId);
        return variantTypeRepository.findByProductStoreIdOrderBySequenceNumAscNameAsc(productStoreId)
                .stream()
                .map(type -> toTypeDto(type, true))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductStoreVariantTypeDto getType(String productStoreId, String variantTypeId) {
        return toTypeDto(requireType(productStoreId, variantTypeId), true);
    }

    @Override
    @Transactional
    public ProductStoreVariantTypeDto createType(String productStoreId, ProductStoreVariantTypeDto dto) {
        requireStore(productStoreId);
        validateType(dto);
        if (variantTypeRepository.existsByProductStoreIdAndNameIgnoreCase(productStoreId, dto.getName().trim())) {
            throw new IllegalArgumentException("Variant type name already exists for this store: " + dto.getName());
        }

        ProductStoreVariantType entity = new ProductStoreVariantType();
        entity.setVariantTypeId(VariantIdGenerator.nextTypeId());
        entity.setProductStoreId(productStoreId);
        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedDate(now);
        entity.setLastModifiedDate(now);
        applyType(entity, dto);
        return toTypeDto(variantTypeRepository.save(entity), false);
    }

    @Override
    @Transactional
    public ProductStoreVariantTypeDto updateType(String productStoreId, String variantTypeId,
                                                  ProductStoreVariantTypeDto dto) {
        ProductStoreVariantType entity = requireType(productStoreId, variantTypeId);
        validateType(dto);
        if (variantTypeRepository.existsByProductStoreIdAndNameIgnoreCaseAndVariantTypeIdNot(
                productStoreId, dto.getName().trim(), variantTypeId)) {
            throw new IllegalArgumentException("Variant type name already exists for this store: " + dto.getName());
        }
        applyType(entity, dto);
        entity.setLastModifiedDate(LocalDateTime.now());
        return toTypeDto(variantTypeRepository.save(entity), true);
    }

    @Override
    @Transactional
    public void deleteType(String productStoreId, String variantTypeId) {
        ProductStoreVariantType entity = requireType(productStoreId, variantTypeId);
        variantValueRepository.deleteByVariantTypeId(variantTypeId);
        variantTypeRepository.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductStoreVariantValueDto> listValues(String productStoreId, String variantTypeId) {
        requireType(productStoreId, variantTypeId);
        return variantValueRepository.findByVariantTypeIdOrderBySequenceNumAscValueAsc(variantTypeId)
                .stream()
                .map(this::toValueDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductStoreVariantValueDto createValue(String productStoreId, String variantTypeId,
                                                    ProductStoreVariantValueDto dto) {
        requireType(productStoreId, variantTypeId);
        validateValue(dto);
        if (variantValueRepository.existsByVariantTypeIdAndValueIgnoreCase(variantTypeId, dto.getValue().trim())) {
            throw new IllegalArgumentException("Variant value already exists for this type: " + dto.getValue());
        }

        ProductStoreVariantValue entity = new ProductStoreVariantValue();
        entity.setVariantValueId(VariantIdGenerator.nextValueId());
        entity.setVariantTypeId(variantTypeId);
        entity.setProductStoreId(productStoreId);
        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedDate(now);
        entity.setLastModifiedDate(now);
        applyValue(entity, dto);
        return toValueDto(variantValueRepository.save(entity));
    }

    @Override
    @Transactional
    public ProductStoreVariantValueDto updateValue(String productStoreId, String variantTypeId,
                                                     String variantValueId, ProductStoreVariantValueDto dto) {
        ProductStoreVariantValue entity = requireValue(productStoreId, variantTypeId, variantValueId);
        validateValue(dto);
        if (variantValueRepository.existsByVariantTypeIdAndValueIgnoreCaseAndVariantValueIdNot(
                variantTypeId, dto.getValue().trim(), variantValueId)) {
            throw new IllegalArgumentException("Variant value already exists for this type: " + dto.getValue());
        }
        applyValue(entity, dto);
        entity.setLastModifiedDate(LocalDateTime.now());
        return toValueDto(variantValueRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteValue(String productStoreId, String variantTypeId, String variantValueId) {
        ProductStoreVariantValue entity = requireValue(productStoreId, variantTypeId, variantValueId);
        variantValueRepository.delete(entity);
    }

    private void requireStore(String productStoreId) {
        if (!productStoreRepository.existsById(productStoreId)) {
            throw new ResourceNotFoundException("Product store not found: " + productStoreId);
        }
    }

    private ProductStoreVariantType requireType(String productStoreId, String variantTypeId) {
        requireStore(productStoreId);
        return variantTypeRepository.findByVariantTypeIdAndProductStoreId(variantTypeId, productStoreId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Variant type not found: " + variantTypeId + " for store " + productStoreId));
    }

    private ProductStoreVariantValue requireValue(String productStoreId, String variantTypeId, String variantValueId) {
        requireType(productStoreId, variantTypeId);
        return variantValueRepository
                .findByVariantValueIdAndVariantTypeIdAndProductStoreId(variantValueId, variantTypeId, productStoreId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Variant value not found: " + variantValueId));
    }

    private void validateType(ProductStoreVariantTypeDto dto) {
        if (dto == null || !StringUtils.hasText(dto.getName())) {
            throw new IllegalArgumentException("Variant type name is required");
        }
    }

    private void validateValue(ProductStoreVariantValueDto dto) {
        if (dto == null || !StringUtils.hasText(dto.getValue())) {
            throw new IllegalArgumentException("Variant value is required");
        }
    }

    private void applyType(ProductStoreVariantType entity, ProductStoreVariantTypeDto dto) {
        entity.setName(dto.getName().trim());
        entity.setCode(trimToNull(dto.getCode()) == null
                ? null
                : dto.getCode().trim().toUpperCase(Locale.ROOT).replace(' ', '_'));
        entity.setDescription(trimToNull(dto.getDescription()));
        entity.setSequenceNum(dto.getSequenceNum() != null ? dto.getSequenceNum() : 0);
        entity.setEnabled(dto.isEnabled() ? "Y" : "N");
    }

    private void applyValue(ProductStoreVariantValue entity, ProductStoreVariantValueDto dto) {
        entity.setValue(dto.getValue().trim());
        entity.setAbbreviation(trimToNull(dto.getAbbreviation()));
        entity.setSequenceNum(dto.getSequenceNum() != null ? dto.getSequenceNum() : 0);
        entity.setEnabled(dto.isEnabled() ? "Y" : "N");
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private ProductStoreVariantTypeDto toTypeDto(ProductStoreVariantType entity, boolean includeValues) {
        ProductStoreVariantTypeDto dto = new ProductStoreVariantTypeDto();
        dto.setVariantTypeId(entity.getVariantTypeId());
        dto.setProductStoreId(entity.getProductStoreId());
        dto.setName(entity.getName());
        dto.setCode(entity.getCode());
        dto.setDescription(entity.getDescription());
        dto.setSequenceNum(entity.getSequenceNum());
        dto.setEnabled("Y".equalsIgnoreCase(entity.getEnabled()));
        if (includeValues) {
            dto.setValues(variantValueRepository
                    .findByVariantTypeIdOrderBySequenceNumAscValueAsc(entity.getVariantTypeId())
                    .stream()
                    .map(this::toValueDto)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    private ProductStoreVariantValueDto toValueDto(ProductStoreVariantValue entity) {
        ProductStoreVariantValueDto dto = new ProductStoreVariantValueDto();
        dto.setVariantValueId(entity.getVariantValueId());
        dto.setVariantTypeId(entity.getVariantTypeId());
        dto.setProductStoreId(entity.getProductStoreId());
        dto.setValue(entity.getValue());
        dto.setAbbreviation(entity.getAbbreviation());
        dto.setSequenceNum(entity.getSequenceNum());
        dto.setEnabled("Y".equalsIgnoreCase(entity.getEnabled()));
        return dto;
    }
}
