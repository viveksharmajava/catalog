package com.playpro.playpro.catalog.importexport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Column layout for product import/export Excel (single sheet).
 * Purchase: {@code AVERAGE_COST} + {@code average_cost_tax}. Sale prices use {@code tax_rate};
 * {@code tax_in_price} is calculated on import (not a spreadsheet column).
 */
public final class ProductSpreadsheetColumns {

    public static final int ATTR_SLOTS = 3;

    public static final String DEFAULT_PRICE_TYPE = "DEFAULT_PRICE";

    public static final String AVERAGE_COST_TYPE = "AVERAGE_COST";

    public static final String PURCHASE_PRICE_PURPOSE = "PURCHASE";

    /**
     * All product price types from admin reference (ordered by description).
     */
    public static final List<String> ALL_PRICE_TYPE_COLUMNS = Collections.unmodifiableList(Arrays.asList(
            "AVERAGE_COST",
            "BOX_PRICE",
            "COMPETITIVE_PRICE",
            "DEFAULT_PRICE",
            "LIST_PRICE",
            "MAXIMUM_PRICE",
            "MINIMUM_ORDER_PRICE",
            "MINIMUM_PRICE",
            "PROMO_PRICE",
            "SHIPPING_ALLOWANCE",
            "SPECIAL_PROMO_PRICE",
            "WHOLESALE_PRICE"
    ));

    /** Sale price types after {@code tax_rate} (excludes AVERAGE_COST and DEFAULT_PRICE). */
    private static final List<String> SALE_PRICE_TYPE_COLUMNS;

    private static final List<String> BASE_COLUMNS = new ArrayList<>();

    private static final List<String> PRICING_META_COLUMNS = Arrays.asList(
            "currency",
            "tax_rate",
            "average_cost_tax"
    );

    private static final Set<String> BASE_COLUMN_SET = new HashSet<>();
    private static final Set<String> PRICE_TYPE_SET = new HashSet<>();
    private static final Set<String> PRICING_META_SET = new HashSet<>();

    static {
        SALE_PRICE_TYPE_COLUMNS = Collections.unmodifiableList(Arrays.asList(
                "BOX_PRICE",
                "COMPETITIVE_PRICE",
                "LIST_PRICE",
                "MAXIMUM_PRICE",
                "MINIMUM_ORDER_PRICE",
                "MINIMUM_PRICE",
                "PROMO_PRICE",
                "SHIPPING_ALLOWANCE",
                "SPECIAL_PROMO_PRICE",
                "WHOLESALE_PRICE"
        ));

        Collections.addAll(BASE_COLUMNS,
                "product_id",
                "sku",
                "product_type_id",
                "status_id",
                "category_ids",
                "internal_name",
                "brand_name",
                "product_name",
                "description",
                "long_description",
                "comments",
                "keywords",
                "is_virtual",
                "is_variant",
                "returnable",
                "taxable",
                "charge_shipping",
                "require_inventory",
                "introduction_date",
                "release_date",
                "sales_discontinuation_date",
                "product_weight",
                "shipping_weight",
                "product_height",
                "product_width",
                "product_depth",
                "small_image",
                "medium_image",
                "large_image",
                "original_image");

        for (int i = 1; i <= ATTR_SLOTS; i++) {
            BASE_COLUMNS.add("attr_" + i + "_name");
            BASE_COLUMNS.add("attr_" + i + "_value");
        }

        BASE_COLUMN_SET.addAll(BASE_COLUMNS);
        PRICE_TYPE_SET.addAll(ALL_PRICE_TYPE_COLUMNS);
        for (String meta : PRICING_META_COLUMNS) {
            PRICING_META_SET.add(meta);
        }
        PRICING_META_SET.add("gst_percent");
        PRICING_META_SET.add("average_gst");
        PRICING_META_SET.add("tax_in_price");
    }

    private ProductSpreadsheetColumns() {
    }

    public static List<String> headers() {
        List<String> all = new ArrayList<>(BASE_COLUMNS);
        all.add("currency");
        all.add(AVERAGE_COST_TYPE);
        all.add("average_cost_tax");
        all.add(DEFAULT_PRICE_TYPE);
        all.add("tax_rate");
        all.addAll(SALE_PRICE_TYPE_COLUMNS);
        return Collections.unmodifiableList(all);
    }

    public static List<String> baseColumns() {
        return Collections.unmodifiableList(BASE_COLUMNS);
    }

    public static List<String> salePriceTypeColumns() {
        return SALE_PRICE_TYPE_COLUMNS;
    }

    public static boolean isBaseColumn(String normalizedHeader) {
        return normalizedHeader != null && BASE_COLUMN_SET.contains(normalizedHeader);
    }

    public static boolean isPricingMetaColumn(String normalizedHeader) {
        if (normalizedHeader == null) {
            return false;
        }
        return PRICING_META_SET.contains(normalizedHeader.toLowerCase(Locale.ROOT));
    }

    public static boolean isPriceTypeColumn(String normalizedHeader) {
        if (normalizedHeader == null || isPricingMetaColumn(normalizedHeader)) {
            return false;
        }
        return PRICE_TYPE_SET.contains(normalizedHeader.toUpperCase(Locale.ROOT));
    }

    public static String normalizePriceTypeHeader(String header) {
        return header == null ? null : header.trim().toUpperCase(Locale.ROOT);
    }
}
