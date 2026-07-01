package com.playpro.playpro.catalog.importexport;

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
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class SpreadsheetIO {

    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_ONLY = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private SpreadsheetIO() {
    }

    public static List<SpreadsheetDataRow> readDataRows(InputStream inputStream,
                                                        String preferredSheetName,
                                                        Set<String> requiredHeaderMarkers,
                                                        String missingHeaderMessage) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            DataFormatter formatter = new DataFormatter();
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            Sheet sheet = resolveDataSheet(workbook, preferredSheetName, formatter, evaluator, requiredHeaderMarkers);
            if (sheet == null) {
                throw new IllegalArgumentException(missingHeaderMessage);
            }
            return readDataRows(sheet, formatter, evaluator, requiredHeaderMarkers, missingHeaderMessage);
        }
    }

    private static Sheet resolveDataSheet(Workbook workbook,
                                          String preferredSheetName,
                                          DataFormatter formatter,
                                          FormulaEvaluator evaluator,
                                          Set<String> requiredHeaderMarkers) {
        Sheet named = findSheetIgnoreCase(workbook, preferredSheetName);
        if (named != null && findHeaderRowIndex(named, formatter, evaluator, requiredHeaderMarkers) >= 0) {
            return named;
        }
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet candidate = workbook.getSheetAt(i);
            if (isInstructionsSheet(candidate)) {
                continue;
            }
            if (findHeaderRowIndex(candidate, formatter, evaluator, requiredHeaderMarkers) >= 0) {
                return candidate;
            }
        }
        return named;
    }

    public static Sheet findSheetIgnoreCase(Workbook workbook, String preferredName) {
        if (preferredName == null) {
            return null;
        }
        Sheet sheet = workbook.getSheet(preferredName);
        if (sheet != null) {
            return sheet;
        }
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet candidate = workbook.getSheetAt(i);
            if (preferredName.equalsIgnoreCase(candidate.getSheetName())) {
                return candidate;
            }
        }
        return null;
    }

    public static boolean isInstructionsSheet(Sheet sheet) {
        return sheet != null && "instructions".equalsIgnoreCase(sheet.getSheetName().trim());
    }

    public static List<SpreadsheetDataRow> readDataRows(Sheet sheet,
                                                        DataFormatter formatter,
                                                        FormulaEvaluator evaluator,
                                                        Set<String> requiredHeaderMarkers,
                                                        String missingHeaderMessage) {
        if (sheet == null) {
            return new ArrayList<>();
        }
        int headerRowIndex = findHeaderRowIndex(sheet, formatter, evaluator, requiredHeaderMarkers);
        if (headerRowIndex < 0) {
            throw new IllegalArgumentException(missingHeaderMessage);
        }

        Map<Integer, String> indexToHeader = buildHeaderMap(sheet.getRow(headerRowIndex), formatter, evaluator);
        List<SpreadsheetDataRow> rows = new ArrayList<>();
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
            rows.add(new SpreadsheetDataRow(rowIndex + 1, cells));
        }
        return rows;
    }

    public static byte[] writeWorkbook(List<SheetDefinition> sheets,
                                       boolean includeInstructions,
                                       String[] instructionLines) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            for (SheetDefinition definition : sheets) {
                Sheet sheet = workbook.createSheet(definition.getSheetName());
                List<String> headers = definition.getHeaders();
                Row headerRow = sheet.createRow(0);
                for (int i = 0; i < headers.size(); i++) {
                    headerRow.createCell(i).setCellValue(headers.get(i));
                }
                sheet.createFreezePane(0, 1);

                int rowIndex = 1;
                if (definition.getSampleRow() != null) {
                    writeRow(sheet.createRow(rowIndex++), headers, definition.getSampleRow());
                }
                for (Map<String, String> dataRow : definition.getDataRows()) {
                    writeRow(sheet.createRow(rowIndex++), headers, dataRow);
                }
                for (int i = 0; i < Math.min(headers.size(), 12); i++) {
                    sheet.autoSizeColumn(i);
                }
            }

            if (includeInstructions && instructionLines != null && instructionLines.length > 0) {
                Sheet instructions = workbook.createSheet("Instructions");
                for (int i = 0; i < instructionLines.length; i++) {
                    instructions.createRow(i).createCell(0).setCellValue(instructionLines[i]);
                }
                instructions.autoSizeColumn(0);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    public static String normalizeHeader(String header) {
        if (header == null) {
            return null;
        }
        String trimmed = header.replace('\uFEFF', ' ').trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        return trimmed.toLowerCase(Locale.ROOT).replace(' ', '_');
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

    public static String formatDateTime(LocalDateTime value) {
        return value == null ? null : value.format(DATE_TIME);
    }

    public static String formatDecimal(BigDecimal value) {
        return value == null ? null : value.stripTrailingZeros().toPlainString();
    }

    public static void put(Map<String, String> cells, String key, String value) {
        if (value != null) {
            cells.put(key, value);
        }
    }

    private static int findHeaderRowIndex(Sheet sheet,
                                          DataFormatter formatter,
                                          FormulaEvaluator evaluator,
                                          Set<String> requiredHeaderMarkers) {
        int scanLimit = Math.min(15, sheet.getLastRowNum());
        for (int rowIndex = 0; rowIndex <= scanLimit; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                continue;
            }
            Set<String> found = new java.util.HashSet<>();
            for (int col = 0; col < row.getLastCellNum(); col++) {
                String header = normalizeHeader(readCellAsString(row.getCell(col), formatter, evaluator));
                if (header != null && requiredHeaderMarkers.contains(header)) {
                    found.add(header);
                }
            }
            if (found.containsAll(requiredHeaderMarkers)) {
                return rowIndex;
            }
        }
        return -1;
    }

    private static Map<Integer, String> buildHeaderMap(Row headerRow,
                                                       DataFormatter formatter,
                                                       FormulaEvaluator evaluator) {
        Map<Integer, String> indexToHeader = new HashMap<>();
        if (headerRow == null) {
            return indexToHeader;
        }
        for (int col = 0; col < headerRow.getLastCellNum(); col++) {
            String header = normalizeHeader(readCellAsString(headerRow.getCell(col), formatter, evaluator));
            if (header != null) {
                indexToHeader.put(col, header);
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

    private static void writeRow(Row row, List<String> headers, Map<String, String> cells) {
        for (int i = 0; i < headers.size(); i++) {
            String value = cells.get(headers.get(i));
            if (value != null) {
                row.createCell(i).setCellValue(value);
            }
        }
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

    public static final class SpreadsheetDataRow {
        private final int rowNumber;
        private final Map<String, String> cells;

        public SpreadsheetDataRow(int rowNumber, Map<String, String> cells) {
            this.rowNumber = rowNumber;
            this.cells = cells;
        }

        public int getRowNumber() {
            return rowNumber;
        }

        public Map<String, String> getCells() {
            return cells;
        }
    }

    public static final class SheetDefinition {
        private final String sheetName;
        private final List<String> headers;
        private final List<Map<String, String>> dataRows;
        private final Map<String, String> sampleRow;

        public SheetDefinition(String sheetName,
                               List<String> headers,
                               List<Map<String, String>> dataRows,
                               Map<String, String> sampleRow) {
            this.sheetName = sheetName;
            this.headers = headers;
            this.dataRows = dataRows;
            this.sampleRow = sampleRow;
        }

        public String getSheetName() {
            return sheetName;
        }

        public List<String> headers() {
            return headers;
        }

        public List<String> getHeaders() {
            return headers;
        }

        public List<Map<String, String>> getDataRows() {
            return dataRows;
        }

        public Map<String, String> getSampleRow() {
            return sampleRow;
        }
    }
}
