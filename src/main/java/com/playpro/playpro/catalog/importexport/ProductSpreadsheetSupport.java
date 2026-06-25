package com.playpro.playpro.catalog.importexport;

import com.playpro.playpro.catalog.dto.ProductAttributeDto;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ProductSpreadsheetSupport {

    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_ONLY = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private ProductSpreadsheetSupport() {
    }

    public static List<ProductImportRow> readRows(InputStream inputStream) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheet("Products");
            if (sheet == null) {
                sheet = workbook.getSheetAt(0);
            }
            if (sheet == null) {
                return new ArrayList<>();
            }

            DataFormatter formatter = new DataFormatter();
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

            int headerRowIndex = findHeaderRowIndex(sheet, formatter, evaluator);
            if (headerRowIndex < 0) {
                throw new IllegalArgumentException(
                        "Header row not found. Do not delete the first row of the template — it must contain "
                                + "column names such as product_id, sku, and product_name. "
                                + "Download a fresh template from Bulk Import and add your products from row 2 onward.");
            }

            Map<Integer, String> indexToHeader = buildHeaderMap(sheet.getRow(headerRowIndex), formatter, evaluator);

            List<ProductImportRow> rows = new ArrayList<>();
            int lastRow = sheet.getLastRowNum();
            int blankStreak = 0;
            for (int rowIndex = headerRowIndex + 1; rowIndex <= lastRow; rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null || isEmptyRow(row, formatter, evaluator)) {
                    blankStreak++;
                    if (blankStreak > 20) {
                        break;
                    }
                    continue;
                }
                blankStreak = 0;
                Map<String, String> cells = new LinkedHashMap<>();
                for (Map.Entry<Integer, String> entry : indexToHeader.entrySet()) {
                    cells.put(entry.getValue(), readCellAsString(row.getCell(entry.getKey()), formatter, evaluator));
                }
                ProductImportRow importRow = ProductImportRow.fromCellMap(rowIndex + 1, cells);
                if (!importRow.isBlank()) {
                    rows.add(importRow);
                }
            }
            return rows;
        }
    }

    private static int findHeaderRowIndex(Sheet sheet, DataFormatter formatter, FormulaEvaluator evaluator) {
        int scanLimit = Math.min(15, sheet.getLastRowNum());
        for (int rowIndex = 0; rowIndex <= scanLimit; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                continue;
            }
            boolean hasProductName = false;
            boolean hasSkuOrProductId = false;
            for (Cell cell : row) {
                String header = normalizeHeader(readCellAsString(cell, formatter, evaluator));
                if ("product_name".equals(header)) {
                    hasProductName = true;
                }
                if ("product_id".equals(header) || "sku".equals(header)) {
                    hasSkuOrProductId = true;
                }
            }
            if (hasProductName && hasSkuOrProductId) {
                return rowIndex;
            }
        }
        return -1;
    }

    private static Map<Integer, String> buildHeaderMap(Row headerRow, DataFormatter formatter, FormulaEvaluator evaluator) {
        Map<Integer, String> indexToHeader = new HashMap<>();
        for (Cell cell : headerRow) {
            String header = normalizeHeader(readCellAsString(cell, formatter, evaluator));
            if (header != null) {
                indexToHeader.put(cell.getColumnIndex(), header);
            }
        }
        return indexToHeader;
    }

    private static boolean isEmptyRow(Row row, DataFormatter formatter, FormulaEvaluator evaluator) {
        for (Cell cell : row) {
            String value = readCellAsString(cell, formatter, evaluator);
            if (value != null && !value.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public static byte[] writeWorkbook(List<Map<String, String>> dataRows, boolean includeSampleRow) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            if (includeSampleRow) {
                Sheet instructions = workbook.createSheet("Instructions");
                String[] lines = {
                        "Product Import Template",
                        "",
                        "1. Go to the 'Products' sheet.",
                        "2. Do NOT delete or change row 1 (column headers).",
                        "3. Enter each product on its own row starting from row 2.",
                        "4. product_name is required. Use sku or product_id to update existing products.",
                        "5. category_ids: comma-separated category IDs (first = primary category).",
                        "6. Image columns (small_image, medium_image, large_image, original_image):",
                        "   relative path format: product_id/image_type/file_name (e.g. PROD-001/small/hero.jpg)",
                        "7. Pricing: currency, AVERAGE_COST (purchase price), average_cost_tax,",
                        "   DEFAULT_PRICE, tax_rate (sale prices), then other price type columns.",
                        "8. tax_in_price is calculated on import (sale = tax-inclusive, cost = exclusive).",
                        "9. Price type column header = type ID; cell = amount.",
                };
                for (int i = 0; i < lines.length; i++) {
                    instructions.createRow(i).createCell(0).setCellValue(lines[i]);
                }
                instructions.autoSizeColumn(0);
            }

            Sheet sheet = workbook.createSheet("Products");
            List<String> headers = ProductSpreadsheetColumns.headers();

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.size(); i++) {
                headerRow.createCell(i).setCellValue(headers.get(i));
            }
            sheet.createFreezePane(0, 1);

            int rowIndex = 1;
            if (includeSampleRow) {
                writeRow(sheet.createRow(rowIndex++), sampleRow());
            }

            for (Map<String, String> dataRow : dataRows) {
                writeRow(sheet.createRow(rowIndex++), dataRow);
            }

            for (int i = 0; i < Math.min(headers.size(), 12); i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.setSheetOrder("Products", 0);
            if (includeSampleRow) {
                workbook.setSheetOrder("Instructions", 1);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    private static Map<String, String> sampleRow() {
        Map<String, String> sample = new LinkedHashMap<>();
        sample.put("product_id", "");
        sample.put("sku", "SKU-SAMPLE-001");
        sample.put("product_type_id", "FINISHED_GOOD");
        sample.put("status_id", "ACTIVE");
        sample.put("category_ids", "CAT-ELECTRONICS,CAT-ROOT");
        sample.put("internal_name", "Sample Internal Name");
        sample.put("brand_name", "Sample Brand");
        sample.put("product_name", "Sample Product");
        sample.put("description", "Short description");
        sample.put("long_description", "Long description for sample product");
        sample.put("comments", "Import comments");
        sample.put("keywords", "sample,import,demo");
        sample.put("is_virtual", "N");
        sample.put("is_variant", "N");
        sample.put("returnable", "Y");
        sample.put("taxable", "Y");
        sample.put("charge_shipping", "Y");
        sample.put("require_inventory", "Y");
        sample.put("introduction_date", "2026-01-01");
        sample.put("release_date", "2026-01-15");
        sample.put("sales_discontinuation_date", "");
        sample.put("product_weight", "0.5");
        sample.put("shipping_weight", "0.6");
        sample.put("product_height", "10");
        sample.put("product_width", "5");
        sample.put("product_depth", "2");
        sample.put("small_image", "PROD-SAMPLE/small/hero.jpg");
        sample.put("medium_image", "PROD-SAMPLE/medium/hero.jpg");
        sample.put("large_image", "PROD-SAMPLE/large/hero.jpg");
        sample.put("original_image", "PROD-SAMPLE/original/hero.jpg");
        sample.put("attr_1_name", "color");
        sample.put("attr_1_value", "red");
        sample.put("attr_2_name", "size");
        sample.put("attr_2_value", "M");
        sample.put("currency", "INR");
        sample.put("AVERAGE_COST", "750.00");
        sample.put("average_cost_tax", "5");
        sample.put("DEFAULT_PRICE", "999.00");
        sample.put("tax_rate", "18");
        sample.put("BOX_PRICE", "950.00");
        sample.put("COMPETITIVE_PRICE", "899.00");
        sample.put("LIST_PRICE", "1299.00");
        sample.put("MAXIMUM_PRICE", "1499.00");
        sample.put("MINIMUM_ORDER_PRICE", "500.00");
        sample.put("MINIMUM_PRICE", "799.00");
        sample.put("PROMO_PRICE", "899.00");
        sample.put("SHIPPING_ALLOWANCE", "50.00");
        sample.put("SPECIAL_PROMO_PRICE", "849.00");
        sample.put("WHOLESALE_PRICE", "850.00");
        return sample;
    }

    public static Map<String, String> toCellMap(ProductImportRow row,
                                                List<com.playpro.playpro.catalog.client.dto.ProductPriceClientDto> prices) {
        Map<String, String> cells = new LinkedHashMap<>();
        com.playpro.playpro.catalog.dto.ProductDto product = row.getProduct();
        String productId = product.getProductId();
        put(cells, "product_id", productId);
        put(cells, "sku", product.getSku());
        put(cells, "product_type_id", product.getProductTypeId());
        put(cells, "status_id", product.getStatusId());
        put(cells, "category_ids", joinComma(row.getCategoryIds()));
        put(cells, "internal_name", product.getInternalName());
        put(cells, "brand_name", product.getBrandName());
        put(cells, "product_name", product.getProductName());
        put(cells, "description", product.getDescription());
        put(cells, "long_description", product.getLongDescription());
        put(cells, "comments", product.getComments());
        put(cells, "keywords", product.getKeywords() == null ? null : String.join(",", product.getKeywords()));
        put(cells, "is_virtual", toYn(product.getVirtualProduct()));
        put(cells, "is_variant", toYn(product.getVariant()));
        put(cells, "returnable", toYn(product.getReturnable()));
        put(cells, "taxable", toYn(product.getTaxable()));
        put(cells, "charge_shipping", toYn(product.getChargeShipping()));
        put(cells, "require_inventory", toYn(product.getRequireInventory()));
        put(cells, "introduction_date", formatDateTime(product.getIntroductionDate()));
        put(cells, "release_date", formatDateTime(product.getReleaseDate()));
        put(cells, "sales_discontinuation_date", formatDateTime(product.getSalesDiscontinuationDate()));
        put(cells, "product_weight", formatDecimal(product.getProductWeight()));
        put(cells, "shipping_weight", formatDecimal(product.getShippingWeight()));
        put(cells, "product_height", formatDecimal(product.getProductHeight()));
        put(cells, "product_width", formatDecimal(product.getProductWidth()));
        put(cells, "product_depth", formatDecimal(product.getProductDepth()));
        put(cells, "small_image", ProductImagePathSupport.toExportPath(productId, "small", product.getSmallImageUrl()));
        put(cells, "medium_image", ProductImagePathSupport.toExportPath(productId, "medium", product.getMediumImageUrl()));
        put(cells, "large_image", ProductImagePathSupport.toExportPath(productId, "large", product.getLargeImageUrl()));
        put(cells, "original_image", ProductImagePathSupport.toExportPath(productId, "original", product.getDetailImageUrl()));

        BigDecimal exportTax = row.getTaxRate();
        BigDecimal exportAverageCostTax = row.getAverageCostTax();
        String exportCurrency = row.getCurrency();
        List<com.playpro.playpro.catalog.client.dto.ProductPriceClientDto> exportPrices =
                prices != null ? prices : row.getPrices();
        for (com.playpro.playpro.catalog.client.dto.ProductPriceClientDto price : exportPrices) {
            if (price.getProductPriceTypeId() != null && price.getPrice() != null) {
                put(cells, price.getProductPriceTypeId(), formatDecimal(price.getPrice()));
            }
            if (ProductSpreadsheetColumns.DEFAULT_PRICE_TYPE.equals(price.getProductPriceTypeId())
                    && price.getTaxPercentage() != null) {
                exportTax = price.getTaxPercentage();
            }
            if (ProductSpreadsheetColumns.AVERAGE_COST_TYPE.equals(price.getProductPriceTypeId())
                    && price.getTaxPercentage() != null) {
                exportAverageCostTax = price.getTaxPercentage();
            }
            if (exportCurrency == null && price.getCurrencyUomId() != null) {
                exportCurrency = price.getCurrencyUomId();
            }
        }
        put(cells, "currency", exportCurrency != null ? exportCurrency : "INR");
        put(cells, "average_cost_tax", formatDecimal(exportAverageCostTax));
        put(cells, "tax_rate", formatDecimal(exportTax));

        for (int i = 0; i < ProductSpreadsheetColumns.ATTR_SLOTS; i++) {
            ProductAttributeDto attribute = i < row.getAttributes().size() ? row.getAttributes().get(i) : null;
            put(cells, "attr_" + (i + 1) + "_name", attribute == null ? null : attribute.getAttrName());
            put(cells, "attr_" + (i + 1) + "_value", attribute == null ? null : attribute.getAttrValue());
        }
        return cells;
    }

    private static void writeRow(Row row, Map<String, String> cells) {
        List<String> headers = ProductSpreadsheetColumns.headers();
        for (int i = 0; i < headers.size(); i++) {
            String value = cells.get(headers.get(i));
            if (value != null) {
                row.createCell(i).setCellValue(value);
            }
        }
    }

    private static void put(Map<String, String> cells, String key, String value) {
        if (value != null) {
            cells.put(key, value);
        }
    }

    private static String joinComma(List<String> values) {
        if (values == null || values.isEmpty()) {
            return null;
        }
        return String.join(",", values);
    }

    private static String joinPipe(List<String> values) {
        if (values == null || values.isEmpty()) {
            return null;
        }
        return String.join("|", values);
    }

    private static String toYn(Boolean value) {
        if (value == null) {
            return null;
        }
        return Boolean.TRUE.equals(value) ? "Y" : "N";
    }

    private static String formatDecimal(BigDecimal value) {
        return value == null ? null : value.stripTrailingZeros().toPlainString();
    }

    private static String formatDateTime(LocalDateTime value) {
        return value == null ? null : value.format(DATE_TIME);
    }

    public static BigDecimal parseDecimal(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid number: " + value);
        }
    }

    public static LocalDateTime parseDateTime(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        String trimmed = value.trim();
        try {
            if (trimmed.length() <= 10) {
                return LocalDateTime.parse(trimmed + " 00:00:00", DATE_TIME);
            }
            return LocalDateTime.parse(trimmed, DATE_TIME);
        } catch (DateTimeParseException ex) {
            try {
                return LocalDateTime.parse(trimmed, DATE_ONLY);
            } catch (DateTimeParseException ignored) {
                throw new IllegalArgumentException("Invalid date: " + value);
            }
        }
    }

    private static String canonicalHeader(String header) {
        if (header == null) {
            return null;
        }
        String trimmed = header.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        String upper = trimmed.toUpperCase(java.util.Locale.ROOT);
        if (ProductSpreadsheetColumns.isPriceTypeColumn(upper)) {
            return upper;
        }
        return trimmed.toLowerCase(java.util.Locale.ROOT).replace(' ', '_');
    }

    private static String normalizeHeader(String header) {
        return canonicalHeader(header);
    }

    private static String readCellAsString(Cell cell, DataFormatter formatter, FormulaEvaluator evaluator) {
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getLocalDateTimeCellValue().format(DATE_TIME);
        }
        String value = formatter.formatCellValue(cell, evaluator);
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }
}
