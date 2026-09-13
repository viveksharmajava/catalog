package com.playpro.playpro.catalog.importexport;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CategoryImportRowTest {

    @Test
    void parsesCatalogIdAndCreatesResolvedList() {
        Map<String, String> cells = new LinkedHashMap<>();
        cells.put("category_name", "Rackets");
        cells.put("catalog_id", "DEMO_CATALOG, SEASONAL");

        CategoryImportRow row = CategoryImportRow.fromCellMap(2, cells);

        assertEquals("Rackets", row.getCategory().getCategoryName());
        assertEquals("DEMO_CATALOG, SEASONAL", row.getCatalogId());
        assertEquals(List.of("DEMO_CATALOG", "SEASONAL"), row.resolveCatalogIds());
    }

    @Test
    void acceptsProdCatalogIdAlias() {
        Map<String, String> cells = new LinkedHashMap<>();
        cells.put("category_name", "Shoes");
        cells.put("prod_catalog_id", "DEMO_CATALOG");

        CategoryImportRow row = CategoryImportRow.fromCellMap(3, cells);

        assertEquals(List.of("DEMO_CATALOG"), row.resolveCatalogIds());
    }

    @Test
    void blankCatalogIdYieldsNoLinks() {
        Map<String, String> cells = new LinkedHashMap<>();
        cells.put("category_name", "Bags");

        CategoryImportRow row = CategoryImportRow.fromCellMap(4, cells);

        assertTrue(row.resolveCatalogIds().isEmpty());
    }
}
