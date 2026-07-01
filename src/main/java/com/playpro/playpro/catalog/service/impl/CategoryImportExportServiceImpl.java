package com.playpro.playpro.catalog.service.impl;

import com.playpro.playpro.catalog.dto.ProductCategoryDto;
import com.playpro.playpro.catalog.dto.ProductImportResultDto;
import com.playpro.playpro.catalog.dto.ProductImportRowErrorDto;
import com.playpro.playpro.catalog.entity.category.ProductCategory;
import com.playpro.playpro.catalog.importexport.CategoryImportRow;
import com.playpro.playpro.catalog.importexport.CategorySpreadsheetSupport;
import com.playpro.playpro.catalog.repository.ProductCategoryRepository;
import com.playpro.playpro.catalog.service.CategoryImportExportService;
import com.playpro.playpro.catalog.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CategoryImportExportServiceImpl implements CategoryImportExportService {

    private final CategoryService categoryService;
    private final ProductCategoryRepository categoryRepository;

    public CategoryImportExportServiceImpl(CategoryService categoryService,
                                           ProductCategoryRepository categoryRepository) {
        this.categoryService = categoryService;
        this.categoryRepository = categoryRepository;
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
            rows.add(CategoryImportRow.toCellMap(dto));
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
        if (categoryId != null && !categoryId.trim().isEmpty()
                && categoryRepository.existsById(categoryId.trim())) {
            categoryService.updateCategory(categoryId.trim(), category, principal);
            return false;
        }

        categoryService.createCategory(category, principal);
        return true;
    }
}
