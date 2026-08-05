package com.playpro.playpro.catalog.service.impl;

import com.playpro.playpro.catalog.dto.CategoryProdCatalogDto;
import com.playpro.playpro.catalog.dto.ProductCategoryDto;
import com.playpro.playpro.catalog.dto.ProductImportResultDto;
import com.playpro.playpro.catalog.dto.ProductImportRowErrorDto;
import com.playpro.playpro.catalog.entity.catalog.ProdCatalogCategory;
import com.playpro.playpro.catalog.entity.category.ProductCategory;
import com.playpro.playpro.catalog.importexport.CategoryImportRow;
import com.playpro.playpro.catalog.importexport.CategorySpreadsheetSupport;
import com.playpro.playpro.catalog.repository.ProdCatalogCategoryRepository;
import com.playpro.playpro.catalog.repository.ProductCategoryRepository;
import com.playpro.playpro.catalog.service.CategoryAssociationService;
import com.playpro.playpro.catalog.service.CategoryImportExportService;
import com.playpro.playpro.catalog.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CategoryImportExportServiceImpl implements CategoryImportExportService {

    private final CategoryService categoryService;
    private final CategoryAssociationService categoryAssociationService;
    private final ProductCategoryRepository categoryRepository;
    private final ProdCatalogCategoryRepository prodCatalogCategoryRepository;

    public CategoryImportExportServiceImpl(CategoryService categoryService,
                                           CategoryAssociationService categoryAssociationService,
                                           ProductCategoryRepository categoryRepository,
                                           ProdCatalogCategoryRepository prodCatalogCategoryRepository) {
        this.categoryService = categoryService;
        this.categoryAssociationService = categoryAssociationService;
        this.categoryRepository = categoryRepository;
        this.prodCatalogCategoryRepository = prodCatalogCategoryRepository;
    }

    @Override
    public byte[] generateTemplate() throws IOException {
        return CategorySpreadsheetSupport.writeWorkbook(new ArrayList<>(), true);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportCategories() throws IOException {
        List<Map<String, String>> rows = new ArrayList<>();
        for (ProductCategory category : categoryRepository.findAll()) {
            ProductCategoryDto dto = categoryService.getCategory(category.getProductCategoryId());
            String catalogIds = prodCatalogCategoryRepository
                    .findByIdProductCategoryIdOrderBySequenceNumAsc(category.getProductCategoryId())
                    .stream()
                    .map(link -> link.getId().getProdCatalogId())
                    .distinct()
                    .collect(Collectors.joining(","));
            rows.add(CategoryImportRow.toCellMap(dto, catalogIds.isEmpty() ? null : catalogIds));
        }
        return CategorySpreadsheetSupport.writeWorkbook(rows, false);
    }

    @Override
    public ProductImportResultDto importCategories(MultipartFile file, String principal) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Import file is required");
        }

        List<CategoryImportRow> rows = CategorySpreadsheetSupport.readRows(file.getInputStream());
        if (rows.isEmpty()) {
            throw new IllegalArgumentException(
                    "No category data rows found. Keep the header row unchanged and enter categories from row 2. "
                            + "Each row needs at least category_name (and category_id for updates).");
        }

        ProductImportResultDto result = new ProductImportResultDto();
        result.setTotalRows(rows.size());

        for (CategoryImportRow row : rows) {
            try {
                boolean created = importRow(row, principal);
                if (created) {
                    result.setCreated(result.getCreated() + 1);
                } else {
                    result.setUpdated(result.getUpdated() + 1);
                }
            } catch (Exception ex) {
                result.setFailed(result.getFailed() + 1);
                result.getErrors().add(new ProductImportRowErrorDto(
                        row.getRowNumber(),
                        row.getCategory().getProductCategoryId(),
                        null,
                        ex.getMessage() == null ? "Import failed" : ex.getMessage()));
            }
        }
        return result;
    }

    private boolean importRow(CategoryImportRow row, String principal) {
        ProductCategoryDto category = row.getCategory();
        if (category.getCategoryName() == null || category.getCategoryName().trim().isEmpty()) {
            throw new IllegalArgumentException("category_name is required");
        }

        String categoryId = category.getProductCategoryId();
        boolean created;
        ProductCategoryDto saved;
        if (categoryId != null && !categoryId.trim().isEmpty()
                && categoryRepository.existsById(categoryId.trim())) {
            saved = categoryService.updateCategory(categoryId.trim(), category, principal);
            created = false;
        } else {
            saved = categoryService.createCategory(category, principal);
            created = true;
        }

        linkCatalogs(saved.getProductCategoryId(), row.resolveCatalogIds());
        return created;
    }

    private void linkCatalogs(String categoryId, List<String> catalogIds) {
        if (catalogIds == null || catalogIds.isEmpty()) {
            return;
        }

        List<ProdCatalogCategory> existingLinks =
                prodCatalogCategoryRepository.findByIdProductCategoryIdOrderBySequenceNumAsc(categoryId);

        for (String catalogId : catalogIds) {
            boolean alreadyLinked = existingLinks.stream()
                    .anyMatch(link -> catalogId.equalsIgnoreCase(link.getId().getProdCatalogId()));
            if (alreadyLinked) {
                continue;
            }
            CategoryProdCatalogDto association = new CategoryProdCatalogDto();
            association.setProdCatalogId(catalogId);
            association.setProductCategoryId(categoryId);
            association.setProdCatalogCategoryTypeId("PCCT_BROWSE_ROOT");
            categoryAssociationService.addProdCatalog(categoryId, association);
        }
    }
}
