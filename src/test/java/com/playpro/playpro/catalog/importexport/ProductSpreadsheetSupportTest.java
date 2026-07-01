package com.playpro.playpro.catalog.importexport;

import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductSpreadsheetSupportTest {

    @Test
    void readsUserProductTemplate() throws Exception {
        Path file = Path.of("C:/Users/W520724/Downloads/withdata/product_import_template (4) (1).xlsx");
        try (FileInputStream in = new FileInputStream(file.toFile())) {
            List<ProductImportRow> rows = ProductSpreadsheetSupport.readRows(in);
            assertFalse(rows.isEmpty(), "Expected product rows from user template");
        }
    }

    @Test
    void readsGeneratedTemplate() throws Exception {
        byte[] bytes = ProductSpreadsheetSupport.writeWorkbook(List.of(), true);
        try (java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(bytes)) {
            List<ProductImportRow> rows = ProductSpreadsheetSupport.readRows(in);
            assertTrue(rows.isEmpty(), "Fresh template only contains the sample row, which is skipped on import");
        }
    }

    @Test
    void readsUserCategoryTemplate() throws Exception {
        Path file = Path.of("C:/Users/W520724/Downloads/withdata/category_import_template.xlsx");
        try (FileInputStream in = new FileInputStream(file.toFile())) {
            List<CategoryImportRow> rows = CategorySpreadsheetSupport.readRows(in);
            assertFalse(rows.isEmpty(), "Expected category rows from user template");
        }
    }

    @Test
    void readsUserCatalogTemplate() throws Exception {
        Path file = Path.of("C:/Users/W520724/Downloads/withdata/prod_catalog_import_template.xlsx");
        try (FileInputStream in = new FileInputStream(file.toFile())) {
            ProdCatalogSpreadsheetSupport.WorkbookData data = ProdCatalogSpreadsheetSupport.readRows(in);
            assertFalse(data.getCatalogs().isEmpty() || data.getCategoryLinks().isEmpty(),
                    "Expected catalog or category-link rows from user template");
        }
    }
}
