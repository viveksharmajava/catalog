package com.playpro.playpro.catalog.service.impl;

import com.playpro.playpro.catalog.dto.ProductAttributeDto;
import com.playpro.playpro.catalog.dto.ProductDto;
import com.playpro.playpro.catalog.dto.ProductVariantConfigDto;
import com.playpro.playpro.catalog.dto.ProductVariantConfigDto.ProductVariantConfigTypeDto;
import com.playpro.playpro.catalog.dto.ProductVariantConfigDto.ProductVariantConfigValueDto;
import com.playpro.playpro.catalog.dto.ProductVariantConfigDto.ProductVariantGeneratedDto;
import com.playpro.playpro.catalog.dto.ProductVariantConfigRequest;
import com.playpro.playpro.catalog.entity.product.Product;
import com.playpro.playpro.catalog.entity.product.ProductAssoc;
import com.playpro.playpro.catalog.entity.product.ProductVariantOption;
import com.playpro.playpro.catalog.entity.product.ProductVariantOptionValue;
import com.playpro.playpro.catalog.entity.store.ProductStoreVariantType;
import com.playpro.playpro.catalog.entity.store.ProductStoreVariantValue;
import com.playpro.playpro.catalog.exception.ResourceNotFoundException;
import com.playpro.playpro.catalog.helper.ProductWorker;
import com.playpro.playpro.catalog.repository.ProductAssocRepository;
import com.playpro.playpro.catalog.repository.ProductRepository;
import com.playpro.playpro.catalog.repository.ProductStoreRepository;
import com.playpro.playpro.catalog.repository.ProductStoreVariantTypeRepository;
import com.playpro.playpro.catalog.repository.ProductStoreVariantValueRepository;
import com.playpro.playpro.catalog.repository.ProductVariantOptionRepository;
import com.playpro.playpro.catalog.repository.ProductVariantOptionValueRepository;
import com.playpro.playpro.catalog.service.ProductService;
import com.playpro.playpro.catalog.service.ProductVariantConfigService;
import com.playpro.playpro.catalog.util.IndicatorUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductVariantConfigServiceImpl implements ProductVariantConfigService {

    private static final int MAX_PRODUCT_ID_LENGTH = 20;

    private final ProductRepository productRepository;
    private final ProductStoreRepository productStoreRepository;
    private final ProductVariantOptionRepository optionRepository;
    private final ProductVariantOptionValueRepository optionValueRepository;
    private final ProductStoreVariantTypeRepository variantTypeRepository;
    private final ProductStoreVariantValueRepository variantValueRepository;
    private final ProductAssocRepository assocRepository;
    private final ProductService productService;
    private final ProductWorker productWorker;

    public ProductVariantConfigServiceImpl(ProductRepository productRepository,
                                            ProductStoreRepository productStoreRepository,
                                            ProductVariantOptionRepository optionRepository,
                                            ProductVariantOptionValueRepository optionValueRepository,
                                            ProductStoreVariantTypeRepository variantTypeRepository,
                                            ProductStoreVariantValueRepository variantValueRepository,
                                            ProductAssocRepository assocRepository,
                                            ProductService productService,
                                            ProductWorker productWorker) {
        this.productRepository = productRepository;
        this.productStoreRepository = productStoreRepository;
        this.optionRepository = optionRepository;
        this.optionValueRepository = optionValueRepository;
        this.variantTypeRepository = variantTypeRepository;
        this.variantValueRepository = variantValueRepository;
        this.assocRepository = assocRepository;
        this.productService = productService;
        this.productWorker = productWorker;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductVariantConfigDto getConfig(String productId, String productStoreId) {
        Product product = requireProduct(productId);
        String storeId = resolveStoreId(productId, productStoreId);
        return buildConfig(product, storeId);
    }

    @Override
    @Transactional
    public ProductVariantConfigDto saveConfig(String productId, ProductVariantConfigRequest request, String principal) {
        Product product = requireProduct(productId);
        if (request == null || !StringUtils.hasText(request.getProductStoreId())) {
            throw new IllegalArgumentException("productStoreId is required");
        }
        String storeId = request.getProductStoreId().trim();
        if (!productStoreRepository.existsById(storeId)) {
            throw new ResourceNotFoundException("Product store not found: " + storeId);
        }
        if (request.getTypes() == null || request.getTypes().isEmpty()) {
            throw new IllegalArgumentException("Select at least one variant type with values");
        }

        optionValueRepository.deleteByProductId(productId);
        optionRepository.deleteByProductId(productId);

        LocalDateTime now = LocalDateTime.now();
        int typeIndex = 0;
        for (ProductVariantConfigRequest.TypeSelection typeSel : request.getTypes()) {
            if (typeSel == null || !StringUtils.hasText(typeSel.getVariantTypeId())) {
                continue;
            }
            if (typeSel.getValueIds() == null || typeSel.getValueIds().isEmpty()) {
                throw new IllegalArgumentException("Each selected variant type must include at least one value");
            }
            ProductStoreVariantType type = variantTypeRepository
                    .findByVariantTypeIdAndProductStoreId(typeSel.getVariantTypeId(), storeId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Variant type not found for store: " + typeSel.getVariantTypeId()));

            ProductVariantOption option = new ProductVariantOption();
            option.setProductId(productId);
            option.setVariantTypeId(type.getVariantTypeId());
            option.setProductStoreId(storeId);
            option.setSequenceNum(typeSel.getSequenceNum() != null ? typeSel.getSequenceNum() : typeIndex * 10);
            option.setCreatedDate(now);
            optionRepository.save(option);

            for (String valueId : typeSel.getValueIds()) {
                if (!StringUtils.hasText(valueId)) {
                    continue;
                }
                ProductStoreVariantValue value = variantValueRepository
                        .findByVariantValueIdAndVariantTypeIdAndProductStoreId(
                                valueId.trim(), type.getVariantTypeId(), storeId)
                        .orElseThrow(() -> new ResourceNotFoundException("Variant value not found: " + valueId));

                ProductVariantOptionValue ov = new ProductVariantOptionValue();
                ov.setProductId(productId);
                ov.setVariantTypeId(type.getVariantTypeId());
                ov.setVariantValueId(value.getVariantValueId());
                optionValueRepository.save(ov);
            }
            typeIndex++;
        }

        // Persist options then immediately create child products for each combination.
        return createChildVariants(product, storeId, principal);
    }

    @Override
    @Transactional
    public ProductVariantConfigDto generateVariants(String productId, String principal) {
        Product parent = requireProduct(productId);
        List<ProductVariantOption> options = optionRepository.findByProductIdOrderBySequenceNumAsc(productId);
        if (options.isEmpty()) {
            throw new IllegalArgumentException("Save variant type selections before generating variants");
        }
        String storeId = options.get(0).getProductStoreId();
        return createChildVariants(parent, storeId, principal);
    }

    private ProductVariantConfigDto createChildVariants(Product parent, String storeId, String principal) {
        String productId = parent.getProductId();
        List<ProductVariantOption> options = optionRepository.findByProductIdOrderBySequenceNumAsc(productId);
        if (options.isEmpty()) {
            throw new IllegalArgumentException("Select at least one variant type with values");
        }

        List<List<ValueChoice>> axes = new ArrayList<>();
        for (ProductVariantOption option : options) {
            ProductStoreVariantType type = variantTypeRepository.findById(option.getVariantTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Variant type not found: " + option.getVariantTypeId()));
            List<ProductVariantOptionValue> selected =
                    optionValueRepository.findByProductIdAndVariantTypeId(productId, option.getVariantTypeId());
            if (selected.isEmpty()) {
                throw new IllegalArgumentException("No values selected for type: " + type.getName());
            }
            List<ValueChoice> choices = new ArrayList<>();
            for (ProductVariantOptionValue ov : selected) {
                ProductStoreVariantValue value = variantValueRepository.findById(ov.getVariantValueId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Variant value not found: " + ov.getVariantValueId()));
                choices.add(new ValueChoice(type, value));
            }
            axes.add(choices);
        }

        List<List<ValueChoice>> combinations = cartesian(axes);
        for (List<ValueChoice> combo : combinations) {
            String childId = buildChildProductId(parent.getProductId(), combo);

            if (productRepository.existsById(childId)) {
                Product existing = productRepository.findById(childId).orElseThrow();
                if (!productWorker.isVariant(existing)) {
                    existing.setIsVariant("Y");
                    productRepository.save(existing);
                }
                productService.associateVariant(productId, childId, principal);
                ensureVariantAttributes(childId, combo, principal);
                continue;
            }

            ProductDto childDto = copyParentToChildDto(parent, childId, combo);
            productService.createProduct(childDto, principal);
            productService.associateVariant(productId, childId, principal);
            ensureVariantAttributes(childId, combo, principal);

            if (parent.getPrimaryProductCategoryId() != null) {
                try {
                    productService.addCategoryToProduct(childId, parent.getPrimaryProductCategoryId(), principal);
                } catch (Exception ignored) {
                    // already linked via create
                }
            }
        }

        return buildConfig(parent, storeId);
    }

    private ProductDto copyParentToChildDto(Product parent, String childId, List<ValueChoice> combo) {
        ProductDto childDto = new ProductDto();
        childDto.setProductId(childId);
        childDto.setProductTypeId(parent.getProductTypeId());
        childDto.setStatusId(parent.getStatusId() != null ? parent.getStatusId() : ProductWorker.STATUS_ACTIVE);
        childDto.setPrimaryProductCategoryId(parent.getPrimaryProductCategoryId());
        childDto.setInternalName(parent.getInternalName());
        childDto.setBrandName(parent.getBrandName());
        childDto.setProductName(buildChildName(parent.getProductName(), combo));
        childDto.setDescription(parent.getDescription());
        childDto.setLongDescription(parent.getLongDescription());
        childDto.setComments(parent.getComments());
        childDto.setSmallImageUrl(parent.getSmallImageUrl());
        childDto.setMediumImageUrl(parent.getMediumImageUrl());
        childDto.setLargeImageUrl(parent.getLargeImageUrl());
        childDto.setDetailImageUrl(parent.getDetailImageUrl());
        childDto.setIntroductionDate(parent.getIntroductionDate());
        childDto.setReleaseDate(parent.getReleaseDate());
        childDto.setSalesDiscontinuationDate(parent.getSalesDiscontinuationDate());
        childDto.setVirtualProduct(false);
        childDto.setVariant(true);
        childDto.setReturnable(IndicatorUtil.fromIndicator(parent.getReturnable()));
        childDto.setTaxable(IndicatorUtil.fromIndicator(parent.getTaxable()));
        childDto.setChargeShipping(IndicatorUtil.fromIndicator(parent.getChargeShipping()));
        childDto.setRequireInventory(
                parent.getRequireInventory() != null
                        ? IndicatorUtil.fromIndicator(parent.getRequireInventory())
                        : Boolean.TRUE);
        childDto.setShippingWeight(parent.getShippingWeight());
        childDto.setProductWeight(parent.getProductWeight());
        childDto.setProductHeight(parent.getProductHeight());
        childDto.setProductWidth(parent.getProductWidth());
        childDto.setProductDepth(parent.getProductDepth());
        childDto.setSku(childId);
        return childDto;
    }

    private void ensureVariantAttributes(String childId, List<ValueChoice> combo, String principal) {
        for (ValueChoice choice : combo) {
            ProductAttributeDto attr = new ProductAttributeDto();
            attr.setAttrName(choice.type.getName());
            attr.setAttrValue(choice.value.getValue());
            attr.setAttrType("VARIANT");
            attr.setAttrDescription(choice.type.getCode());
            try {
                productService.addAttribute(childId, attr, principal);
            } catch (Exception ignored) {
                // attribute may already exist
            }
        }
    }

    private ProductVariantConfigDto buildConfig(Product product, String storeId) {
        ProductVariantConfigDto dto = new ProductVariantConfigDto();
        dto.setProductId(product.getProductId());
        dto.setProductStoreId(storeId);
        dto.setVirtualProduct(productWorker.isVirtual(product));

        List<ProductVariantOption> savedOptions = optionRepository.findByProductIdOrderBySequenceNumAsc(productId(product));
        Map<String, Set<String>> selectedByType = optionValueRepository.findByProductId(product.getProductId()).stream()
                .collect(Collectors.groupingBy(
                        ProductVariantOptionValue::getVariantTypeId,
                        Collectors.mapping(ProductVariantOptionValue::getVariantValueId, Collectors.toSet())));

        List<ProductStoreVariantType> storeTypes = storeId == null
                ? List.of()
                : variantTypeRepository.findByProductStoreIdOrderBySequenceNumAscNameAsc(storeId);

        List<ProductVariantConfigTypeDto> typeDtos = new ArrayList<>();
        for (ProductStoreVariantType type : storeTypes) {
            ProductVariantConfigTypeDto typeDto = new ProductVariantConfigTypeDto();
            typeDto.setVariantTypeId(type.getVariantTypeId());
            typeDto.setName(type.getName());
            typeDto.setCode(type.getCode());
            typeDto.setSequenceNum(type.getSequenceNum());

            Set<String> selected = selectedByType.getOrDefault(type.getVariantTypeId(), Set.of());
            List<ProductVariantConfigValueDto> values = variantValueRepository
                    .findByVariantTypeIdOrderBySequenceNumAscValueAsc(type.getVariantTypeId())
                    .stream()
                    .map(v -> {
                        ProductVariantConfigValueDto vd = new ProductVariantConfigValueDto();
                        vd.setVariantValueId(v.getVariantValueId());
                        vd.setValue(v.getValue());
                        vd.setAbbreviation(v.getAbbreviation());
                        vd.setSequenceNum(v.getSequenceNum());
                        vd.setEnabled("Y".equalsIgnoreCase(v.getEnabled()));
                        vd.setSelected(selected.contains(v.getVariantValueId()));
                        return vd;
                    })
                    .collect(Collectors.toList());
            typeDto.setValues(values);
            typeDto.setSelectedValueIds(values.stream()
                    .filter(ProductVariantConfigValueDto::isSelected)
                    .map(ProductVariantConfigValueDto::getVariantValueId)
                    .collect(Collectors.toList()));
            typeDtos.add(typeDto);
        }
        dto.setTypes(typeDtos);

        // Prefer saved option order when building generated list labels
        LocalDateTime now = LocalDateTime.now();
        List<ProductAssoc> assocs = assocRepository.findActiveAssocsFrom(
                product.getProductId(), ProductWorker.ASSOC_VARIANT, now);
        List<ProductVariantGeneratedDto> generated = new ArrayList<>();
        for (ProductAssoc assoc : assocs) {
            String childId = assoc.getId().getProductIdTo();
            ProductDto child = productService.getProduct(childId);
            ProductVariantGeneratedDto g = new ProductVariantGeneratedDto();
            g.setProductId(child.getProductId());
            g.setProductName(child.getProductName());
            g.setExisting(true);
            Map<String, String> selections = new LinkedHashMap<>();
            if (child.getAttributes() != null) {
                for (ProductAttributeDto attr : child.getAttributes()) {
                    if ("VARIANT".equalsIgnoreCase(attr.getAttrType()) && attr.getAttrName() != null) {
                        selections.put(attr.getAttrName(), attr.getAttrValue());
                    }
                }
            }
            g.setSelections(selections);
            generated.add(g);
        }
        dto.setGeneratedVariants(generated);

        // Keep storeId from saved options if request omitted
        if (!savedOptions.isEmpty() && dto.getProductStoreId() == null) {
            dto.setProductStoreId(savedOptions.get(0).getProductStoreId());
        }
        return dto;
    }

    private String resolveStoreId(String productId, String productStoreId) {
        // Prefer the store already used for this product's variant options.
        List<ProductVariantOption> options = optionRepository.findByProductIdOrderBySequenceNumAsc(productId);
        if (!options.isEmpty()) {
            return options.get(0).getProductStoreId();
        }
        if (StringUtils.hasText(productStoreId)) {
            return productStoreId.trim();
        }
        return null;
    }

    private Product requireProduct(String productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
    }

    private static String productId(Product product) {
        return product.getProductId();
    }

    static String buildChildProductId(String parentId, List<ValueChoice> combo) {
        StringBuilder sb = new StringBuilder(parentId);
        for (ValueChoice choice : combo) {
            sb.append('-').append(idSegment(choice.value));
        }
        String id = sb.toString();
        if (id.length() > MAX_PRODUCT_ID_LENGTH) {
            throw new IllegalArgumentException(
                    "Generated variant product id exceeds " + MAX_PRODUCT_ID_LENGTH
                            + " characters: " + id
                            + ". Use shorter parent product id or value abbreviations.");
        }
        return id;
    }

    static String idSegment(ProductStoreVariantValue value) {
        String raw = StringUtils.hasText(value.getAbbreviation()) ? value.getAbbreviation() : value.getValue();
        String slug = raw.trim().toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-+$", "");
        if (!StringUtils.hasText(slug)) {
            throw new IllegalArgumentException("Variant value produces empty id segment: " + value.getValue());
        }
        return slug;
    }

    private static String buildChildName(String parentName, List<ValueChoice> combo) {
        String base = StringUtils.hasText(parentName) ? parentName.trim() : "Product";
        String suffix = combo.stream()
                .map(c -> c.value.getValue())
                .collect(Collectors.joining(" / "));
        return base + " - " + suffix;
    }

    private static List<List<ValueChoice>> cartesian(List<List<ValueChoice>> axes) {
        List<List<ValueChoice>> result = new ArrayList<>();
        result.add(new ArrayList<>());
        for (List<ValueChoice> axis : axes) {
            List<List<ValueChoice>> next = new ArrayList<>();
            for (List<ValueChoice> prefix : result) {
                for (ValueChoice choice : axis) {
                    List<ValueChoice> copy = new ArrayList<>(prefix);
                    copy.add(choice);
                    next.add(copy);
                }
            }
            result = next;
        }
        return result;
    }

    private static final class ValueChoice {
        private final ProductStoreVariantType type;
        private final ProductStoreVariantValue value;

        private ValueChoice(ProductStoreVariantType type, ProductStoreVariantValue value) {
            this.type = Objects.requireNonNull(type);
            this.value = Objects.requireNonNull(value);
        }
    }
}
