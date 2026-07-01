package com.playpro.playpro.catalog.importexport;

import com.playpro.playpro.catalog.dto.CategoryProdCatalogDto;
import com.playpro.playpro.catalog.dto.ProdCatalogDto;

import java.math.BigDecimal;
import java.util.Map;

public class ProdCatalogImportRow {

    private int rowNumber;
    private ProdCatalogDto catalog = new ProdCatalogDto();

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public ProdCatalogDto getCatalog() {
        return catalog;
    }

    public void setCatalog(ProdCatalogDto catalog) {
        this.catalog = catalog;
    }

    public boolean isBlank() {
        return isBlank(catalog.getProdCatalogId()) && isBlank(catalog.getCatalogName());
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static ProdCatalogImportRow fromSpreadsheetRow(SpreadsheetIO.SpreadsheetDataRow row) {
        return fromCellMap(row.getRowNumber(), row.getCells());
    }

    public static ProdCatalogImportRow fromCellMap(int rowNumber, Map<String, String> cells) {
        ProdCatalogImportRow row = new ProdCatalogImportRow();
        row.setRowNumber(rowNumber);

        ProdCatalogDto dto = row.getCatalog();
        dto.setProdCatalogId(trim(cells.get("prod_catalog_id")));
        dto.setCatalogName(trim(cells.get("catalog_name")));
        dto.setUseQuickAdd(defaultIfBlank(trim(cells.get("use_quick_add")), "Y"));
        dto.setStyleSheet(trim(cells.get("style_sheet")));
        dto.setHeaderLogo(trim(cells.get("header_logo")));
        dto.setContentPathPrefix(trim(cells.get("content_path_prefix")));
        dto.setTemplatePathPrefix(trim(cells.get("template_path_prefix")));
        dto.setViewAllowPermReqd(defaultIfBlank(trim(cells.get("view_allow_perm_reqd")), "N"));
        dto.setPurchaseAllowPermReqd(defaultIfBlank(trim(cells.get("purchase_allow_perm_reqd")), "N"));
        dto.setIsCartEnabled(parseBooleanCell(cells.get("is_cart_enabled"), Boolean.TRUE));
        return row;
    }

    public static Map<String, String> toCellMap(ProdCatalogDto dto) {
        Map<String, String> cells = new java.util.LinkedHashMap<>();
        SpreadsheetIO.put(cells, "prod_catalog_id", dto.getProdCatalogId());
        SpreadsheetIO.put(cells, "catalog_name", dto.getCatalogName());
        SpreadsheetIO.put(cells, "use_quick_add", dto.getUseQuickAdd());
        SpreadsheetIO.put(cells, "style_sheet", dto.getStyleSheet());
        SpreadsheetIO.put(cells, "header_logo", dto.getHeaderLogo());
        SpreadsheetIO.put(cells, "content_path_prefix", dto.getContentPathPrefix());
        SpreadsheetIO.put(cells, "template_path_prefix", dto.getTemplatePathPrefix());
        SpreadsheetIO.put(cells, "view_allow_perm_reqd", dto.getViewAllowPermReqd());
        SpreadsheetIO.put(cells, "purchase_allow_perm_reqd", dto.getPurchaseAllowPermReqd());
        SpreadsheetIO.put(cells, "is_cart_enabled", dto.getIsCartEnabled() == null ? "true" : dto.getIsCartEnabled().toString());
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

    private static Boolean parseBooleanCell(String value, Boolean defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        String normalized = value.trim().toLowerCase();
        if ("true".equals(normalized) || "y".equals(normalized) || "yes".equals(normalized) || "1".equals(normalized)) {
            return Boolean.TRUE;
        }
        if ("false".equals(normalized) || "n".equals(normalized) || "no".equals(normalized) || "0".equals(normalized)) {
            return Boolean.FALSE;
        }
        return defaultValue;
    }
}
