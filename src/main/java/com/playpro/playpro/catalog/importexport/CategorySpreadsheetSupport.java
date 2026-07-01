package com.playpro.playpro.catalog.importexport;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class CategorySpreadsheetSupport {

    private static final Set<String> HEADER_MARKERS = new HashSet<>(Arrays.asList("category_name", "category_id"));

    private static final String[] INSTRUCTIONS = {
            "Category Import Template",
            "",
            "1. Go to the 'Categories' sheet.",
            "2. Do NOT delete or change row 1 (column headers).",
            "3. Enter each category on its own row starting from row 2.",
            "4. category_name is required. Use category_id to update an existing category.",
            "5. parent_category_id links the category under a parent in the rollup tree.",
            "6. show_in_select accepts Y or N.",
    };

    private CategorySpreadsheetSupport() {
    }

    public static List<CategoryImportRow> readRows(InputStream inputStream) throws IOException {
        List<SpreadsheetIO.SpreadsheetDataRow> dataRows = SpreadsheetIO.readDataRows(
                inputStream,
                CategorySpreadsheetColumns.SHEET_NAME,
                HEADER_MARKERS,
                "Header row not found. Keep the first row with category_id and category_name. "
                        + "Download a fresh template and add categories from row 2 onward.");
        List<CategoryImportRow> rows = new ArrayList<>();
        for (SpreadsheetIO.SpreadsheetDataRow dataRow : dataRows) {
            CategoryImportRow importRow = CategoryImportRow.fromSpreadsheetRow(dataRow);
            if (!importRow.isBlank() && !isTemplateSampleRow(dataRow.getCells())) {
                rows.add(importRow);
            }
        }
        return rows;
    }

    private static boolean isTemplateSampleRow(Map<String, String> cells) {
        return "Sample Category".equalsIgnoreCase(cells.get("category_name"));
    }

    public static byte[] writeWorkbook(List<Map<String, String>> dataRows, boolean includeSampleRow) throws IOException {
        Map<String, String> sample = includeSampleRow ? sampleRow() : null;
        SpreadsheetIO.SheetDefinition sheet = new SpreadsheetIO.SheetDefinition(
                CategorySpreadsheetColumns.SHEET_NAME,
                CategorySpreadsheetColumns.headers(),
                dataRows,
                sample);
        return SpreadsheetIO.writeWorkbook(
                List.of(sheet),
                includeSampleRow,
                includeSampleRow ? INSTRUCTIONS : null);
    }

    private static Map<String, String> sampleRow() {
        Map<String, String> sample = new LinkedHashMap<>();
        sample.put("category_id", "");
        sample.put("category_type_id", "CATALOG_CATEGORY");
        sample.put("parent_category_id", "CAT-ROOT");
        sample.put("category_name", "Sample Category");
        sample.put("description", "Short description");
        sample.put("long_description", "Long description for sample category");
        sample.put("category_image_url", "");
        sample.put("show_in_select", "Y");
        return sample;
    }
}
