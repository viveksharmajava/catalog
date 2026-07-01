package com.playpro.playpro.catalog.mapper;

import com.playpro.playpro.catalog.dto.ProdCatalogDto;
import com.playpro.playpro.catalog.dto.ProdCatalogSummaryDto;
import com.playpro.playpro.catalog.entity.catalog.ProdCatalog;
import com.playpro.playpro.catalog.util.IndicatorUtil;

public final class ProdCatalogMapper {

    private ProdCatalogMapper() {
    }

    public static ProdCatalogDto toDto(ProdCatalog entity) {
        ProdCatalogDto dto = new ProdCatalogDto();
        dto.setProdCatalogId(entity.getProdCatalogId());
        dto.setCatalogName(entity.getCatalogName());
        dto.setUseQuickAdd(entity.getUseQuickAdd());
        dto.setStyleSheet(entity.getStyleSheet());
        dto.setHeaderLogo(entity.getHeaderLogo());
        dto.setContentPathPrefix(entity.getContentPathPrefix());
        dto.setTemplatePathPrefix(entity.getTemplatePathPrefix());
        dto.setViewAllowPermReqd(entity.getViewAllowPermReqd());
        dto.setPurchaseAllowPermReqd(entity.getPurchaseAllowPermReqd());
        dto.setIsCartEnabled(IndicatorUtil.fromIndicator(entity.getIsCartEnabled()));
        return dto;
    }

    public static ProdCatalogSummaryDto toSummaryDto(ProdCatalog entity) {
        ProdCatalogSummaryDto dto = new ProdCatalogSummaryDto();
        dto.setProdCatalogId(entity.getProdCatalogId());
        dto.setCatalogName(entity.getCatalogName());
        dto.setUseQuickAdd(entity.getUseQuickAdd());
        dto.setIsCartEnabled(IndicatorUtil.fromIndicator(entity.getIsCartEnabled()));
        return dto;
    }

    public static void applyDtoToEntity(ProdCatalogDto dto, ProdCatalog entity) {
        if (dto.getCatalogName() != null) {
            entity.setCatalogName(dto.getCatalogName());
        }
        if (dto.getUseQuickAdd() != null) {
            entity.setUseQuickAdd(normalizeIndicator(dto.getUseQuickAdd()));
        }
        entity.setStyleSheet(dto.getStyleSheet());
        entity.setHeaderLogo(dto.getHeaderLogo());
        entity.setContentPathPrefix(dto.getContentPathPrefix());
        entity.setTemplatePathPrefix(dto.getTemplatePathPrefix());
        if (dto.getViewAllowPermReqd() != null) {
            entity.setViewAllowPermReqd(normalizeIndicator(dto.getViewAllowPermReqd()));
        }
        if (dto.getPurchaseAllowPermReqd() != null) {
            entity.setPurchaseAllowPermReqd(normalizeIndicator(dto.getPurchaseAllowPermReqd()));
        }
        if (dto.getIsCartEnabled() != null) {
            entity.setIsCartEnabled(IndicatorUtil.toIndicator(dto.getIsCartEnabled()));
        } else if (entity.getIsCartEnabled() == null) {
            entity.setIsCartEnabled(IndicatorUtil.YES);
        }
    }

    private static String normalizeIndicator(String value) {
        return IndicatorUtil.isYes(value) ? IndicatorUtil.YES : IndicatorUtil.NO;
    }
}
