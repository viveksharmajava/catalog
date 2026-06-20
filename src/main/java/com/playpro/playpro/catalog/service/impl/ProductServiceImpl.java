package com.playpro.playpro.catalog.service.impl;

import com.playpro.playpro.catalog.dto.ProductAttributeDto;
import com.playpro.playpro.catalog.dto.ProductCategoryDto;
import com.playpro.playpro.catalog.dto.ProductDto;
import com.playpro.playpro.catalog.dto.ProductFindRequest;
import com.playpro.playpro.catalog.dto.ProductFindResponse;
import com.playpro.playpro.catalog.dto.ProductSummaryDto;
import com.playpro.playpro.catalog.entity.category.ProductCategory;
import com.playpro.playpro.catalog.entity.category.ProductCategoryMember;
import com.playpro.playpro.catalog.entity.category.ProductCategoryMemberId;
import com.playpro.playpro.catalog.entity.product.GoodIdentification;
import com.playpro.playpro.catalog.entity.product.GoodIdentificationId;
import com.playpro.playpro.catalog.entity.product.Product;
import com.playpro.playpro.catalog.entity.product.ProductAssoc;
import com.playpro.playpro.catalog.entity.product.ProductAssocId;
import com.playpro.playpro.catalog.entity.product.ProductAttribute;
import com.playpro.playpro.catalog.entity.product.ProductAttributeId;
import com.playpro.playpro.catalog.entity.product.ProductKeyword;
import com.playpro.playpro.catalog.entity.product.ProductKeywordId;
import com.playpro.playpro.catalog.exception.ResourceNotFoundException;
import com.playpro.playpro.catalog.helper.ProductSearchSpecifications;
import com.playpro.playpro.catalog.helper.ProductWorker;
import com.playpro.playpro.catalog.mapper.ProductMapper;
import com.playpro.playpro.catalog.repository.GoodIdentificationRepository;
import com.playpro.playpro.catalog.repository.ProductAssocRepository;
import com.playpro.playpro.catalog.repository.ProductAttributeRepository;
import com.playpro.playpro.catalog.repository.ProductCategoryMemberRepository;
import com.playpro.playpro.catalog.repository.ProductCategoryRepository;
import com.playpro.playpro.catalog.repository.ProductKeywordRepository;
import com.playpro.playpro.catalog.repository.ProductRepository;
import com.playpro.playpro.catalog.repository.ProductTypeRepository;
import com.playpro.playpro.catalog.service.ProductService;
import com.playpro.playpro.catalog.util.ProductIdGenerator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private static final Set<String> SORTABLE_FIELDS = new HashSet<>(Arrays.asList(
            "productId", "productTypeId", "internalName", "brandName", "productName", "description", "statusId"
    ));

    private final ProductRepository productRepository;
    private final ProductTypeRepository productTypeRepository;
    private final ProductCategoryRepository categoryRepository;
    private final ProductCategoryMemberRepository memberRepository;
    private final GoodIdentificationRepository goodIdentificationRepository;
    private final ProductAttributeRepository attributeRepository;
    private final ProductKeywordRepository keywordRepository;
    private final ProductAssocRepository assocRepository;
    private final ProductWorker productWorker;

    public ProductServiceImpl(ProductRepository productRepository,
                              ProductTypeRepository productTypeRepository,
                              ProductCategoryRepository categoryRepository,
                              ProductCategoryMemberRepository memberRepository,
                              GoodIdentificationRepository goodIdentificationRepository,
                              ProductAttributeRepository attributeRepository,
                              ProductKeywordRepository keywordRepository,
                              ProductAssocRepository assocRepository,
                              ProductWorker productWorker) {
        this.productRepository = productRepository;
        this.productTypeRepository = productTypeRepository;
        this.categoryRepository = categoryRepository;
        this.memberRepository = memberRepository;
        this.goodIdentificationRepository = goodIdentificationRepository;
        this.attributeRepository = attributeRepository;
        this.keywordRepository = keywordRepository;
        this.assocRepository = assocRepository;
        this.productWorker = productWorker;
    }

    @Override
    public ProductDto createProduct(ProductDto dto, String principal) {
        validateProductType(dto.getProductTypeId());
        if (dto.getPrimaryProductCategoryId() != null) {
            ensureCategoryExists(dto.getPrimaryProductCategoryId());
        }

        Product product = new Product();
        product.setProductId(dto.getProductId() != null ? dto.getProductId() : ProductIdGenerator.nextProductId());
        product.setProductTypeId(dto.getProductTypeId() != null ? dto.getProductTypeId() : "FINISHED_GOOD");
        product.setStatusId(dto.getStatusId() != null ? dto.getStatusId() : ProductWorker.STATUS_DRAFT);
        ProductMapper.applyDtoToEntity(dto, product);
        if (product.getProductName() == null || product.getProductName().trim().isEmpty()) {
            throw new IllegalArgumentException("productName is required");
        }
        if (product.getInternalName() == null) {
            product.setInternalName(product.getProductName());
        }
        product.applyAuditOnCreate(principal);
        productRepository.save(product);

        if (dto.getSku() != null && !dto.getSku().trim().isEmpty()) {
            saveSku(product.getProductId(), dto.getSku());
        }

        if (dto.getPrimaryProductCategoryId() != null) {
            addCategoryToProduct(product.getProductId(), dto.getPrimaryProductCategoryId(), principal);
        }

        if (dto.getKeywords() != null) {
            for (String keyword : dto.getKeywords()) {
                addKeyword(product.getProductId(), keyword, principal);
            }
        }

        return getProduct(product.getProductId());
    }

    @Override
    public ProductDto updateProduct(String productId, ProductDto dto, String principal) {
        Product product = loadProductWithType(productId);
        ProductMapper.applyDtoToEntity(dto, product);
        product.applyAuditOnUpdate(principal);
        productRepository.save(product);

        if (dto.getSku() != null) {
            saveSku(productId, dto.getSku());
        }
        return getProduct(productId);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto getProduct(String productId) {
        Product product = loadProductWithType(productId);
        return buildProductDto(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto getProductBySku(String sku) {
        GoodIdentification gid = goodIdentificationRepository
                .findByIdGoodIdentificationTypeIdAndIdValue(ProductWorker.ID_TYPE_SKU, sku)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found for SKU: " + sku));
        return getProduct(gid.getId().getProductId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> searchByKeyword(String keyword) {
        return keywordRepository.findByIdKeywordContainingIgnoreCase(keyword).stream()
                .map(k -> getProduct(k.getId().getProductId()))
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> getVariants(String virtualProductId) {
        Product virtual = loadProductWithType(virtualProductId);
        if (!productWorker.isVirtual(virtual)) {
            throw new IllegalArgumentException("Product is not a virtual product: " + virtualProductId);
        }
        LocalDateTime now = LocalDateTime.now();
        return assocRepository.findActiveAssocsFrom(virtualProductId, ProductWorker.ASSOC_VARIANT, now).stream()
                .map(a -> getProduct(a.getId().getProductIdTo()))
                .collect(Collectors.toList());
    }

    @Override
    public void addCategoryToProduct(String productId, String categoryId, String principal) {
        loadProductWithType(productId);
        ensureCategoryExists(categoryId);
        LocalDateTime now = LocalDateTime.now();
        ProductCategoryMemberId memberId = new ProductCategoryMemberId(categoryId, productId, now);
        if (memberRepository.findById(memberId).isPresent()) {
            return;
        }
        ProductCategoryMember member = new ProductCategoryMember();
        member.setId(memberId);
        member.setSequenceNum(BigDecimal.ONE);
        memberRepository.save(member);
    }

    @Override
    public void removeCategoryFromProduct(String productId, String categoryId, String principal) {
        List<ProductCategoryMember> members = memberRepository.findByIdProductId(productId);
        for (ProductCategoryMember member : members) {
            if (categoryId.equals(member.getId().getProductCategoryId())) {
                member.setThruDate(LocalDateTime.now());
                memberRepository.save(member);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductCategoryDto> getCategoriesForProduct(String productId) {
        loadProductWithType(productId);
        LocalDateTime now = LocalDateTime.now();
        return memberRepository.findByIdProductId(productId).stream()
                .filter(m -> m.getThruDate() == null || m.getThruDate().isAfter(now))
                .map(m -> categoryRepository.findById(m.getId().getProductCategoryId()).orElse(null))
                .filter(c -> c != null)
                .map(ProductMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProductAttributeDto addAttribute(String productId, ProductAttributeDto dto, String principal) {
        loadProductWithType(productId);
        if (dto.getAttrName() == null || dto.getAttrName().trim().isEmpty()) {
            throw new IllegalArgumentException("attrName is required");
        }
        ProductAttribute attribute = new ProductAttribute();
        attribute.setId(new ProductAttributeId(productId, dto.getAttrName()));
        attribute.setAttrValue(dto.getAttrValue());
        attribute.setAttrType(dto.getAttrType());
        attribute.setAttrDescription(dto.getAttrDescription());
        attributeRepository.save(attribute);
        return ProductMapper.toAttributeDto(attribute);
    }

    @Override
    public void addKeyword(String productId, String keyword, String principal) {
        loadProductWithType(productId);
        if (keyword == null || keyword.trim().isEmpty()) {
            return;
        }
        ProductKeywordId id = new ProductKeywordId(productId, keyword.trim().toLowerCase(), "KWT_TAG");
        if (keywordRepository.findById(id).isPresent()) {
            return;
        }
        ProductKeyword productKeyword = new ProductKeyword();
        productKeyword.setId(id);
        productKeyword.setRelevancyWeight(BigDecimal.TEN);
        productKeyword.setStatusId("KW_APPROVED");
        keywordRepository.save(productKeyword);
    }

    @Override
    public void associateVariant(String virtualProductId, String variantProductId, String principal) {
        Product virtual = loadProductWithType(virtualProductId);
        Product variant = loadProductWithType(variantProductId);
        if (!productWorker.isVirtual(virtual)) {
            throw new IllegalArgumentException("Source product must be virtual");
        }
        if (!productWorker.isVariant(variant)) {
            throw new IllegalArgumentException("Target product must be a variant");
        }
        LocalDateTime now = LocalDateTime.now();
        ProductAssocId assocId = new ProductAssocId(virtualProductId, variantProductId, ProductWorker.ASSOC_VARIANT, now);
        if (assocRepository.findById(assocId).isPresent()) {
            return;
        }
        ProductAssoc assoc = new ProductAssoc();
        assoc.setId(assocId);
        assoc.setSequenceNum(BigDecimal.ONE);
        assocRepository.save(assoc);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductFindResponse findProducts(ProductFindRequest request) {
        if (!request.hasFieldConditions() && !request.isNoConditionFind()) {
            ProductFindResponse empty = new ProductFindResponse();
            empty.setPage(Math.max(request.getPage(), 0));
            empty.setSize(Math.max(request.getSize(), 1));
            return empty;
        }

        Specification<Product> spec = ProductSearchSpecifications.combine(
                ProductSearchSpecifications.fieldCriteria("productId", request.getProductId()),
                ProductSearchSpecifications.fieldCriteria("productName", request.getProductName()),
                ProductSearchSpecifications.fieldCriteria("internalName", request.getInternalName())
        );

        int page = Math.max(request.getPage(), 0);
        int size = Math.min(Math.max(request.getSize(), 1), 100);
        Sort sort = buildSort(request.getSortField(), request.getSortDirection());
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Product> result = spec == null
                ? productRepository.findAll(pageable)
                : productRepository.findAll(spec, pageable);

        ProductFindResponse response = new ProductFindResponse();
        response.setPage(result.getNumber());
        response.setSize(result.getSize());
        response.setTotalElements(result.getTotalElements());
        response.setTotalPages(result.getTotalPages());
        response.setContent(result.getContent().stream()
                .map(ProductMapper::toSummaryDto)
                .collect(Collectors.toList()));
        return response;
    }

    private Sort buildSort(String sortField, String sortDirection) {
        String field = SORTABLE_FIELDS.contains(sortField) ? sortField : "productId";
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDirection)
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(direction, field);
    }

    private Product loadProductWithType(String productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
        productTypeRepository.findById(product.getProductTypeId()).ifPresent(product::setProductType);
        return product;
    }

    private ProductDto buildProductDto(Product product) {
        List<GoodIdentification> ids = goodIdentificationRepository.findByIdProductId(product.getProductId());
        List<ProductKeyword> keywords = keywordRepository.findByIdProductId(product.getProductId());
        List<ProductAttribute> attributes = attributeRepository.findByIdProductId(product.getProductId());
        return ProductMapper.toDto(product, productWorker, ProductMapper.extractSku(ids), keywords, attributes);
    }

    private void validateProductType(String productTypeId) {
        String typeId = productTypeId != null ? productTypeId : "FINISHED_GOOD";
        if (!productTypeRepository.existsById(typeId)) {
            throw new IllegalArgumentException("Unknown productTypeId: " + typeId);
        }
    }

    private void ensureCategoryExists(String categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category not found: " + categoryId);
        }
    }

    private void saveSku(String productId, String sku) {
        GoodIdentificationId id = new GoodIdentificationId(ProductWorker.ID_TYPE_SKU, productId);
        GoodIdentification gid = goodIdentificationRepository.findById(id).orElse(new GoodIdentification());
        gid.setId(id);
        gid.setIdValue(sku);
        goodIdentificationRepository.save(gid);
    }
}
