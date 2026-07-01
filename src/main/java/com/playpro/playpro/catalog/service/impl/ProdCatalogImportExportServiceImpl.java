package com.playpro.playpro.catalog.service.impl;

import com.playpro.playpro.catalog.dto.CategoryProdCatalogDto;
import com.playpro.playpro.catalog.dto.ProdCatalogDto;
import com.playpro.playpro.catalog.dto.ProductImportResultDto;
import com.playpro.playpro.catalog.dto.ProductImportRowErrorDto;
import com.playpro.playpro.catalog.entity.catalog.ProdCatalog;
import com.playpro.playpro.catalog.entity.catalog.ProdCatalogCategoryId;
import com.playpro.playpro.catalog.importexport.CatalogCategoryImportRow;
import com.playpro.playpro.catalog.importexport.ProdCatalogImportRow;
import com.playpro.playpro.catalog.importexport.ProdCatalogSpreadsheetSupport;
import com.playpro.playpro.catalog.repository.ProdCatalogCategoryRepository;
import com.playpro.playpro.catalog.repository.ProdCatalogRepository;
import com.playpro.playpro.catalog.service.CatalogAssociationService;
import com.playpro.playpro.catalog.service.ProdCatalogImportExportService;
import com.playpro.playpro.catalog.service.ProdCatalogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ProdCatalogImportExportServiceImpl implements ProdCatalogImportExportService {

    private final ProdCatalogService prodCatalogService;
    private final ProdCatalogRepository prodCatalogRepository;
    private final ProdCatalogCategoryRepository prodCatalogCategoryRepository;
    private final CatalogAssociationService catalogAssociationService;

    public ProdCatalogImportExportServiceImpl(ProdCatalogService prodCatalogService,
                                              ProdCatalogRepository prodCatalogRepository,
                                              ProdCatalogCategoryRepository prodCatalogCategoryRepository,
                                              CatalogAssociationService catalogAssociationService) {
        this.prodCatalogService = prodCatalogService;
        this.prodCatalogRepository = prodCatalogRepository;
        this.prodCatalogCategoryRepository = prodCatalogCategoryRepository;
        this.catalogAssociationService = catalogAssociationService;
    }

    @Override
    public byte[] generateTemplate() throws IOException {
        return ProdCatalogSpreadsheetSupport.writeWorkbook(new ArrayList<>(), new ArrayList<>(), true);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportCatalogs() throws IOException {
        List<Map<String, String>> catalogRows = new ArrayList<>();
        for (ProdCatalog catalog : prodCatalogRepository.findAll()) {
            ProdCatalogDto dto = prodCatalogService.getCatalog(catalog.getProdCatalogId());
            catalogRows.add(ProdCatalogImportRow.toCellMap(dto));
        }

        List<Map<String, String>> linkRows = new ArrayList<>();
        prodCatalogCategoryRepository.findAll().forEach(entity -> {
            CategoryProdCatalogDto dto = new CategoryProdCatalogDto();
            dto.setProdCatalogId(entity.getId().getProdCatalogId());
            dto.setProductCategoryId(entity.getId().getProductCategoryId());
            dto.setProdCatalogCategoryTypeId(entity.getId().getProdCatalogCategoryTypeId());
            dto.setFromDate(entity.getId().getFromDate());
            dto.setThruDate(entity.getThruDate());
            dto.setSequenceNum(entity.getSequenceNum());
            linkRows.add(CatalogCategoryImportRow.toCellMap(dto));
        });

        return ProdCatalogSpreadsheetSupport.writeWorkbook(catalogRows, linkRows, false);
    }

    @Override
    public ProductImportResultDto importCatalogs(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Import file is required");
        }

        ProdCatalogSpreadsheetSupport.WorkbookData workbookData =
                ProdCatalogSpreadsheetSupport.readRows(file.getInputStream());
        if (workbookData.getCatalogs().isEmpty() && workbookData.getCategoryLinks().isEmpty()) {
            throw new IllegalArgumentException(
                    "No catalog data rows found. Use the Catalogs and/or CatalogCategories sheets.");
        }

        ProductImportResultDto result = new ProductImportResultDto();
        result.setTotalRows(workbookData.getCatalogs().size() + workbookData.getCategoryLinks().size());

        for (ProdCatalogImportRow row : workbookData.getCatalogs()) {
            try {
                boolean created = importCatalogRow(row);
                if (created) {
                    result.setCreated(result.getCreated() + 1);
                } else {
                    result.setUpdated(result.getUpdated() + 1);
                }
            } catch (Exception ex) {
                result.setFailed(result.getFailed() + 1);
                result.getErrors().add(new ProductImportRowErrorDto(
                        row.getRowNumber(),
                        row.getCatalog().getProdCatalogId(),
                        row.getCatalog().getCatalogName(),
                        ex.getMessage() == null ? "Import failed" : ex.getMessage()));
            }
        }

        for (CatalogCategoryImportRow row : workbookData.getCategoryLinks()) {
            try {
                boolean created = importCategoryLinkRow(row);
                if (created) {
                    result.setCreated(result.getCreated() + 1);
                } else {
                    result.setUpdated(result.getUpdated() + 1);
                }
            } catch (Exception ex) {
                result.setFailed(result.getFailed() + 1);
                CategoryProdCatalogDto association = row.getAssociation();
                result.getErrors().add(new ProductImportRowErrorDto(
                        row.getRowNumber(),
                        association.getProdCatalogId(),
                        association.getProductCategoryId(),
                        ex.getMessage() == null ? "Import failed" : ex.getMessage()));
            }
        }

        return result;
    }

    private boolean importCatalogRow(ProdCatalogImportRow row) {
        ProdCatalogDto catalog = row.getCatalog();
        if (!StringUtils.hasText(catalog.getCatalogName())) {
            throw new IllegalArgumentException("catalog_name is required");
        }

        String catalogId = catalog.getProdCatalogId();
        if (StringUtils.hasText(catalogId) && prodCatalogRepository.existsById(catalogId.trim())) {
            prodCatalogService.updateCatalog(catalogId.trim(), catalog);
            return false;
        }

        prodCatalogService.createCatalog(catalog);
        return true;
    }

    private boolean importCategoryLinkRow(CatalogCategoryImportRow row) {
        CategoryProdCatalogDto association = row.getAssociation();
        if (!StringUtils.hasText(association.getProdCatalogId())) {
            throw new IllegalArgumentException("prod_catalog_id is required");
        }
        if (!StringUtils.hasText(association.getProductCategoryId())) {
            throw new IllegalArgumentException("product_category_id is required");
        }

        association.setFromDate(row.resolveFromDate());
        association.setSequenceNum(row.resolveSequenceNum());

        ProdCatalogCategoryId id = new ProdCatalogCategoryId(
                association.getProdCatalogId().trim(),
                association.getProductCategoryId().trim(),
                association.getProdCatalogCategoryTypeId(),
                association.getFromDate());

        if (prodCatalogCategoryRepository.existsById(id)) {
            catalogAssociationService.updateCategory(association);
            return false;
        }

        catalogAssociationService.addCategory(association.getProdCatalogId().trim(), association);
        return true;
    }
}
