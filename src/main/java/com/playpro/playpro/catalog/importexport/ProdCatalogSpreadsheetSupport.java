package com.playpro.playpro.catalog.importexport;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ProdCatalogSpreadsheetSupport {

    private static final Set<String> CATALOG_HEADER_MARKERS = new HashSet<>(Arrays.asList("catalog_name", "prod_catalog_id"));
    private static final Set<String> LINK_HEADER_MARKERS = new HashSet<>(Arrays.asList("prod_catalog_id", "product_category_id"));

    private static final String[] INSTRUCTIONS = {
            "Product Catalog Import Template",
            "",
            "1. Use the 'Catalogs' sheet for catalog master data.",
            "2. Use the 'CatalogCategories' sheet to link categories to catalogs.",
            "3. Do NOT delete or change row 1 on either sheet.",
            "4. catalog_name is required on the Catalogs sheet.",
            "5. prod_catalog_id + product_category_id are required on CatalogCategories.",
            "6. prod_catalog_category_type_id defaults to PCCT_BROWSE_ROOT when blank.",
            "7. from_date defaults to import time when blank.",
    };

    public static final class WorkbookData {
        private final List<ProdCatalogImportRow> catalogs;
        private final List<CatalogCategoryImportRow> categoryLinks;

        public WorkbookData(List<ProdCatalogImportRow> catalogs, List<CatalogCategoryImportRow> categoryLinks) {
            this.catalogs = catalogs;
            this.categoryLinks = categoryLinks;
        }

        public List<ProdCatalogImportRow> getCatalogs() {
            return catalogs;
        }

        public List<CatalogCategoryImportRow> getCategoryLinks() {
            return categoryLinks;
        }
    }

    private ProdCatalogSpreadsheetSupport() {
    }

    public static WorkbookData readRows(InputStream inputStream) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            DataFormatter formatter = new DataFormatter();
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

            List<ProdCatalogImportRow> catalogs = new ArrayList<>();
            Sheet catalogSheet = SpreadsheetIO.findSheetIgnoreCase(workbook, ProdCatalogSpreadsheetColumns.CATALOG_SHEET);
            if (catalogSheet == null && workbook.getNumberOfSheets() > 0) {
                catalogSheet = workbook.getSheetAt(0);
            }
            if (catalogSheet != null && !SpreadsheetIO.isInstructionsSheet(catalogSheet)) {
                for (SpreadsheetIO.SpreadsheetDataRow dataRow : SpreadsheetIO.readDataRows(
                        catalogSheet,
                        formatter,
                        evaluator,
                        CATALOG_HEADER_MARKERS,
                        "Header row not found on Catalogs sheet. Keep catalog_name and prod_catalog_id headers.")) {
                    ProdCatalogImportRow importRow = ProdCatalogImportRow.fromSpreadsheetRow(dataRow);
                    if (!importRow.isBlank()) {
                        catalogs.add(importRow);
                    }
                }
            }

            List<CatalogCategoryImportRow> links = new ArrayList<>();
            Sheet linkSheet = SpreadsheetIO.findSheetIgnoreCase(workbook, ProdCatalogSpreadsheetColumns.CATEGORY_LINK_SHEET);
            if (linkSheet != null) {
                for (SpreadsheetIO.SpreadsheetDataRow dataRow : SpreadsheetIO.readDataRows(
                        linkSheet,
                        formatter,
                        evaluator,
                        LINK_HEADER_MARKERS,
                        "Header row not found on CatalogCategories sheet.")) {
                    CatalogCategoryImportRow importRow = CatalogCategoryImportRow.fromSpreadsheetRow(dataRow);
                    if (!importRow.isBlank()) {
                        links.add(importRow);
                    }
                }
            }

            return new WorkbookData(catalogs, links);
        }
    }

    public static byte[] writeWorkbook(List<Map<String, String>> catalogRows,
                                       List<Map<String, String>> categoryLinkRows,
                                       boolean includeSampleRow) throws IOException {
        Map<String, String> catalogSample = includeSampleRow ? catalogSampleRow() : null;
        Map<String, String> linkSample = includeSampleRow ? categoryLinkSampleRow() : null;

        List<SpreadsheetIO.SheetDefinition> sheets = List.of(
                new SpreadsheetIO.SheetDefinition(
                        ProdCatalogSpreadsheetColumns.CATALOG_SHEET,
                        ProdCatalogSpreadsheetColumns.catalogHeaders(),
                        catalogRows,
                        catalogSample),
                new SpreadsheetIO.SheetDefinition(
                        ProdCatalogSpreadsheetColumns.CATEGORY_LINK_SHEET,
                        ProdCatalogSpreadsheetColumns.categoryLinkHeaders(),
                        categoryLinkRows,
                        linkSample));

        return SpreadsheetIO.writeWorkbook(sheets, includeSampleRow, includeSampleRow ? INSTRUCTIONS : null);
    }

    private static Map<String, String> catalogSampleRow() {
        Map<String, String> sample = new LinkedHashMap<>();
        sample.put("prod_catalog_id", "");
        sample.put("catalog_name", "Sample Catalog");
        sample.put("use_quick_add", "Y");
        sample.put("style_sheet", "");
        sample.put("header_logo", "");
        sample.put("content_path_prefix", "");
        sample.put("template_path_prefix", "");
        sample.put("view_allow_perm_reqd", "N");
        sample.put("purchase_allow_perm_reqd", "N");
        sample.put("is_cart_enabled", "true");
        return sample;
    }

    private static Map<String, String> categoryLinkSampleRow() {
        Map<String, String> sample = new LinkedHashMap<>();
        sample.put("prod_catalog_id", "PCAT-SAMPLE");
        sample.put("product_category_id", "CAT-ROOT");
        sample.put("prod_catalog_category_type_id", "PCCT_BROWSE_ROOT");
        sample.put("sequence_num", "1");
        sample.put("from_date", "");
        sample.put("thru_date", "");
        return sample;
    }
}
