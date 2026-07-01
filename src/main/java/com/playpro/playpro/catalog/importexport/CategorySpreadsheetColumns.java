package com.playpro.playpro.catalog.importexport;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class CategorySpreadsheetColumns {

    public static final String SHEET_NAME = "Categories";

    private static final List<String> HEADERS = Collections.unmodifiableList(Arrays.asList(
            "category_id",
            "category_type_id",
            "parent_category_id",
            "category_name",
            "description",
            "long_description",
            "category_image_url",
            "show_in_select"
    ));

    private CategorySpreadsheetColumns() {
    }

    public static List<String> headers() {
        return HEADERS;
    }
}
