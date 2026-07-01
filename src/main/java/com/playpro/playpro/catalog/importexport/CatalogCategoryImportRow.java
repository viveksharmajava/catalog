package com.playpro.playpro.catalog.importexport;

import com.playpro.playpro.catalog.dto.CategoryProdCatalogDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public class CatalogCategoryImportRow {

    private int rowNumber;
    private CategoryProdCatalogDto association = new CategoryProdCatalogDto();

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public CategoryProdCatalogDto getAssociation() {
        return association;
    }

    public void setAssociation(CategoryProdCatalogDto association) {
        this.association = association;
    }

    public boolean isBlank() {
        return isBlank(association.getProdCatalogId()) && isBlank(association.getProductCategoryId());
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static CatalogCategoryImportRow fromSpreadsheetRow(SpreadsheetIO.SpreadsheetDataRow row) {
        return fromCellMap(row.getRowNumber(), row.getCells());
    }

    public static CatalogCategoryImportRow fromCellMap(int rowNumber, Map<String, String> cells) {
        CatalogCategoryImportRow row = new CatalogCategoryImportRow();
        row.setRowNumber(rowNumber);

        CategoryProdCatalogDto dto = row.getAssociation();
        dto.setProdCatalogId(trim(cells.get("prod_catalog_id")));
        dto.setProductCategoryId(trim(cells.get("product_category_id")));
        dto.setProdCatalogCategoryTypeId(defaultIfBlank(trim(cells.get("prod_catalog_category_type_id")), "PCCT_BROWSE_ROOT"));
        dto.setSequenceNum(SpreadsheetIO.parseDecimal(cells.get("sequence_num")));
        dto.setFromDate(SpreadsheetIO.parseDateTime(cells.get("from_date")));
        dto.setThruDate(SpreadsheetIO.parseDateTime(cells.get("thru_date")));
        return row;
    }

    public static Map<String, String> toCellMap(CategoryProdCatalogDto dto) {
        Map<String, String> cells = new java.util.LinkedHashMap<>();
        SpreadsheetIO.put(cells, "prod_catalog_id", dto.getProdCatalogId());
        SpreadsheetIO.put(cells, "product_category_id", dto.getProductCategoryId());
        SpreadsheetIO.put(cells, "prod_catalog_category_type_id", dto.getProdCatalogCategoryTypeId());
        SpreadsheetIO.put(cells, "sequence_num", SpreadsheetIO.formatDecimal(dto.getSequenceNum()));
        SpreadsheetIO.put(cells, "from_date", SpreadsheetIO.formatDateTime(dto.getFromDate()));
        SpreadsheetIO.put(cells, "thru_date", SpreadsheetIO.formatDateTime(dto.getThruDate()));
        return cells;
    }

    public LocalDateTime resolveFromDate() {
        return association.getFromDate() != null ? association.getFromDate() : LocalDateTime.now();
    }

    public BigDecimal resolveSequenceNum() {
        return association.getSequenceNum() != null ? association.getSequenceNum() : BigDecimal.ONE;
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
}
