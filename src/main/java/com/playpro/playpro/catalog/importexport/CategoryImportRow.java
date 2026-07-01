package com.playpro.playpro.catalog.importexport;

import com.playpro.playpro.catalog.dto.ProductCategoryDto;
import com.playpro.playpro.catalog.util.IndicatorUtil;

import java.util.Map;

public class CategoryImportRow {

    private int rowNumber;
    private ProductCategoryDto category = new ProductCategoryDto();

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public ProductCategoryDto getCategory() {
        return category;
    }

    public void setCategory(ProductCategoryDto category) {
        this.category = category;
    }

    public boolean isBlank() {
        return isBlank(category.getProductCategoryId()) && isBlank(category.getCategoryName());
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static CategoryImportRow fromSpreadsheetRow(SpreadsheetIO.SpreadsheetDataRow row) {
        return fromCellMap(row.getRowNumber(), row.getCells());
    }

    public static CategoryImportRow fromCellMap(int rowNumber, Map<String, String> cells) {
        CategoryImportRow row = new CategoryImportRow();
        row.setRowNumber(rowNumber);

        ProductCategoryDto dto = row.getCategory();
        dto.setProductCategoryId(trim(cells.get("category_id")));
        dto.setProductCategoryTypeId(defaultIfBlank(trim(cells.get("category_type_id")), "CATALOG_CATEGORY"));
        dto.setPrimaryParentCategoryId(trim(cells.get("parent_category_id")));
        dto.setCategoryName(trim(cells.get("category_name")));
        dto.setDescription(trim(cells.get("description")));
        dto.setLongDescription(trim(cells.get("long_description")));
        dto.setCategoryImageUrl(trim(cells.get("category_image_url")));
        dto.setShowInSelect(parseYn(cells.get("show_in_select")));
        return row;
    }

    public static Map<String, String> toCellMap(ProductCategoryDto dto) {
        Map<String, String> cells = new java.util.LinkedHashMap<>();
        SpreadsheetIO.put(cells, "category_id", dto.getProductCategoryId());
        SpreadsheetIO.put(cells, "category_type_id", dto.getProductCategoryTypeId());
        SpreadsheetIO.put(cells, "parent_category_id", dto.getPrimaryParentCategoryId());
        SpreadsheetIO.put(cells, "category_name", dto.getCategoryName());
        SpreadsheetIO.put(cells, "description", dto.getDescription());
        SpreadsheetIO.put(cells, "long_description", dto.getLongDescription());
        SpreadsheetIO.put(cells, "category_image_url", dto.getCategoryImageUrl());
        if (dto.getShowInSelect() != null) {
            SpreadsheetIO.put(cells, "show_in_select", IndicatorUtil.toIndicator(dto.getShowInSelect()));
        }
        return cells;
    }

    private static String trim(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static String defaultIfBlank(String value, String defaultValue) {
        return value == null || value.trim().isEmpty() ? defaultValue : value.trim();
    }

    private static Boolean parseYn(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return IndicatorUtil.fromIndicator(value.trim());
    }
}
