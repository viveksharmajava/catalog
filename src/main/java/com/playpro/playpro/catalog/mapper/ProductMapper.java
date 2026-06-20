package com.playpro.playpro.catalog.mapper;

import com.playpro.playpro.catalog.dto.ProductAttributeDto;
import com.playpro.playpro.catalog.dto.ProductCategoryDto;
import com.playpro.playpro.catalog.dto.ProductDto;
import com.playpro.playpro.catalog.entity.category.ProductCategory;
import com.playpro.playpro.catalog.entity.product.GoodIdentification;
import com.playpro.playpro.catalog.entity.product.Product;
import com.playpro.playpro.catalog.entity.product.ProductAttribute;
import com.playpro.playpro.catalog.entity.product.ProductKeyword;
import com.playpro.playpro.catalog.helper.ProductWorker;
import com.playpro.playpro.catalog.util.IndicatorUtil;

import java.util.List;
import java.util.stream.Collectors;

public final class ProductMapper {

    private ProductMapper() {
    }

    public static ProductDto toDto(Product product,
                                   ProductWorker productWorker,
                                   String sku,
                                   List<ProductKeyword> keywords,
                                   List<ProductAttribute> attributes) {
        ProductDto dto = new ProductDto();
        dto.setProductId(product.getProductId());
        dto.setProductTypeId(product.getProductTypeId());
        dto.setPrimaryProductCategoryId(product.getPrimaryProductCategoryId());
        dto.setStatusId(product.getStatusId());
        dto.setInternalName(product.getInternalName());
        dto.setBrandName(product.getBrandName());
        dto.setProductName(product.getProductName());
        dto.setDescription(product.getDescription());
        dto.setLongDescription(product.getLongDescription());
        dto.setComments(product.getComments());
        dto.setSmallImageUrl(product.getSmallImageUrl());
        dto.setMediumImageUrl(product.getMediumImageUrl());
        dto.setLargeImageUrl(product.getLargeImageUrl());
        dto.setDetailImageUrl(product.getDetailImageUrl());
        dto.setIntroductionDate(product.getIntroductionDate());
        dto.setReleaseDate(product.getReleaseDate());
        dto.setSalesDiscontinuationDate(product.getSalesDiscontinuationDate());
        dto.setVirtualProduct(IndicatorUtil.fromIndicator(product.getIsVirtual()));
        dto.setVariant(IndicatorUtil.fromIndicator(product.getIsVariant()));
        dto.setReturnable(IndicatorUtil.fromIndicator(product.getReturnable()));
        dto.setTaxable(IndicatorUtil.fromIndicator(product.getTaxable()));
        dto.setChargeShipping(IndicatorUtil.fromIndicator(product.getChargeShipping()));
        dto.setRequireInventory(IndicatorUtil.fromIndicator(product.getRequireInventory()));
        dto.setShippingWeight(product.getShippingWeight());
        dto.setProductWeight(product.getProductWeight());
        dto.setProductHeight(product.getProductHeight());
        dto.setProductWidth(product.getProductWidth());
        dto.setProductDepth(product.getProductDepth());
        dto.setVersion(product.getVersion());
        dto.setSku(sku);
        if (keywords != null) {
            dto.setKeywords(keywords.stream().map(k -> k.getId().getKeyword()).collect(Collectors.toList()));
        }
        if (attributes != null) {
            dto.setAttributes(attributes.stream().map(ProductMapper::toAttributeDto).collect(Collectors.toList()));
        }
        if (productWorker != null) {
            dto.setAvailableForSale(productWorker.isAvailableForSale(product));
            dto.setShippingApplies(productWorker.shippingApplies(product));
        }
        return dto;
    }

    public static ProductAttributeDto toAttributeDto(ProductAttribute attribute) {
        ProductAttributeDto dto = new ProductAttributeDto();
        dto.setAttrName(attribute.getId().getAttrName());
        dto.setAttrValue(attribute.getAttrValue());
        dto.setAttrType(attribute.getAttrType());
        dto.setAttrDescription(attribute.getAttrDescription());
        return dto;
    }

    public static ProductCategoryDto toCategoryDto(ProductCategory category) {
        ProductCategoryDto dto = new ProductCategoryDto();
        dto.setProductCategoryId(category.getProductCategoryId());
        dto.setProductCategoryTypeId(category.getProductCategoryTypeId());
        dto.setPrimaryParentCategoryId(category.getPrimaryParentCategoryId());
        dto.setCategoryName(category.getCategoryName());
        dto.setDescription(category.getDescription());
        dto.setLongDescription(category.getLongDescription());
        dto.setCategoryImageUrl(category.getCategoryImageUrl());
        dto.setShowInSelect(IndicatorUtil.fromIndicator(category.getShowInSelect()));
        return dto;
    }

    public static void applyDtoToEntity(ProductDto dto, Product product) {
        if (dto.getProductTypeId() != null) {
            product.setProductTypeId(dto.getProductTypeId());
        }
        if (dto.getPrimaryProductCategoryId() != null) {
            product.setPrimaryProductCategoryId(dto.getPrimaryProductCategoryId());
        }
        if (dto.getStatusId() != null) {
            product.setStatusId(dto.getStatusId());
        }
        if (dto.getInternalName() != null) {
            product.setInternalName(dto.getInternalName());
        }
        if (dto.getBrandName() != null) {
            product.setBrandName(dto.getBrandName());
        }
        if (dto.getProductName() != null) {
            product.setProductName(dto.getProductName());
        }
        if (dto.getDescription() != null) {
            product.setDescription(dto.getDescription());
        }
        if (dto.getLongDescription() != null) {
            product.setLongDescription(dto.getLongDescription());
        }
        if (dto.getComments() != null) {
            product.setComments(dto.getComments());
        }
        if (dto.getSmallImageUrl() != null) {
            product.setSmallImageUrl(dto.getSmallImageUrl());
        }
        if (dto.getMediumImageUrl() != null) {
            product.setMediumImageUrl(dto.getMediumImageUrl());
        }
        if (dto.getLargeImageUrl() != null) {
            product.setLargeImageUrl(dto.getLargeImageUrl());
        }
        if (dto.getDetailImageUrl() != null) {
            product.setDetailImageUrl(dto.getDetailImageUrl());
        }
        product.setIntroductionDate(dto.getIntroductionDate());
        product.setReleaseDate(dto.getReleaseDate());
        product.setSalesDiscontinuationDate(dto.getSalesDiscontinuationDate());
        if (dto.getVirtualProduct() != null) {
            product.setIsVirtual(IndicatorUtil.toIndicator(dto.getVirtualProduct()));
        }
        if (dto.getVariant() != null) {
            product.setIsVariant(IndicatorUtil.toIndicator(dto.getVariant()));
        }
        if (dto.getReturnable() != null) {
            product.setReturnable(IndicatorUtil.toIndicator(dto.getReturnable()));
        }
        if (dto.getTaxable() != null) {
            product.setTaxable(IndicatorUtil.toIndicator(dto.getTaxable()));
        }
        if (dto.getChargeShipping() != null) {
            product.setChargeShipping(IndicatorUtil.toIndicator(dto.getChargeShipping()));
        }
        if (dto.getRequireInventory() != null) {
            product.setRequireInventory(IndicatorUtil.toIndicator(dto.getRequireInventory()));
        }
        product.setShippingWeight(dto.getShippingWeight());
        product.setProductWeight(dto.getProductWeight());
        product.setProductHeight(dto.getProductHeight());
        product.setProductWidth(dto.getProductWidth());
        product.setProductDepth(dto.getProductDepth());
    }

    public static String extractSku(List<GoodIdentification> identifications) {
        if (identifications == null) {
            return null;
        }
        return identifications.stream()
                .filter(g -> "SKU".equals(g.getId().getGoodIdentificationTypeId()))
                .map(GoodIdentification::getIdValue)
                .findFirst()
                .orElse(null);
    }
}
