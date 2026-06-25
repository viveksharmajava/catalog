package com.playpro.playpro.catalog.importexport;

import java.math.BigDecimal;

/**
 * Derives tax flags from spreadsheet price and tax rate.
 * Sale prices are tax-inclusive; average cost (purchase) is tax-exclusive.
 */
public final class ProductPriceTaxSupport {

    private ProductPriceTaxSupport() {
    }

    public static String resolveTaxInPrice(BigDecimal price, BigDecimal taxRate, boolean averageCostPrice) {
        if (price == null || taxRate == null || taxRate.compareTo(BigDecimal.ZERO) <= 0) {
            return "N";
        }
        return averageCostPrice ? "N" : "Y";
    }
}
