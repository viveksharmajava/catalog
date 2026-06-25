package com.playpro.playpro.catalog.importexport;

import com.playpro.playpro.catalog.client.dto.ProductPriceClientDto;
import com.playpro.playpro.catalog.dto.ProductAttributeDto;
import com.playpro.playpro.catalog.dto.ProductDto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProductImportRow {

    private int rowNumber;
    private ProductDto product = new ProductDto();
    private List<String> categoryIds = new ArrayList<>();
    private List<ProductAttributeDto> attributes = new ArrayList<>();
    private List<ProductPriceClientDto> prices = new ArrayList<>();
    private String currency = "INR";
    private BigDecimal taxRate;
    private BigDecimal averageCostTax;

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public ProductDto getProduct() {
        return product;
    }

    public void setProduct(ProductDto product) {
        this.product = product;
    }

    public List<String> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<String> categoryIds) {
        this.categoryIds = categoryIds;
    }

    public List<ProductAttributeDto> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<ProductAttributeDto> attributes) {
        this.attributes = attributes;
    }

    public List<ProductPriceClientDto> getPrices() {
        return prices;
    }

    public void setPrices(List<ProductPriceClientDto> prices) {
        this.prices = prices;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getAverageCostTax() {
        return averageCostTax;
    }

    public void setAverageCostTax(BigDecimal averageCostTax) {
        this.averageCostTax = averageCostTax;
    }

    public boolean isBlank() {
        return isBlank(product.getProductId())
                && isBlank(product.getSku())
                && isBlank(product.getProductName())
                && isBlank(product.getInternalName());
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static ProductImportRow fromCellMap(int rowNumber, Map<String, String> cells) {
        ProductImportRow row = new ProductImportRow();
        row.setRowNumber(rowNumber);

        ProductDto dto = row.getProduct();
        dto.setProductId(trim(cells.get("product_id")));
        dto.setSku(trim(cells.get("sku")));
        dto.setProductTypeId(defaultIfBlank(trim(cells.get("product_type_id")), "FINISHED_GOOD"));
        dto.setStatusId(defaultIfBlank(trim(cells.get("status_id")), "DRAFT"));
        dto.setInternalName(trim(cells.get("internal_name")));
        dto.setBrandName(trim(cells.get("brand_name")));
        dto.setProductName(trim(cells.get("product_name")));
        dto.setDescription(trim(cells.get("description")));
        dto.setLongDescription(trim(cells.get("long_description")));
        dto.setComments(trim(cells.get("comments")));
        dto.setSmallImageUrl(trim(cells.get("small_image")));
        dto.setMediumImageUrl(trim(cells.get("medium_image")));
        dto.setLargeImageUrl(trim(cells.get("large_image")));
        dto.setDetailImageUrl(trim(cells.get("original_image")));
        dto.setVirtualProduct(parseYn(cells.get("is_virtual")));
        dto.setVariant(parseYn(cells.get("is_variant")));
        dto.setReturnable(parseYn(cells.get("returnable")));
        dto.setTaxable(parseYn(cells.get("taxable")));
        dto.setChargeShipping(parseYn(cells.get("charge_shipping")));
        dto.setRequireInventory(parseYn(cells.get("require_inventory")));
        dto.setIntroductionDate(ProductSpreadsheetSupport.parseDateTime(cells.get("introduction_date")));
        dto.setReleaseDate(ProductSpreadsheetSupport.parseDateTime(cells.get("release_date")));
        dto.setSalesDiscontinuationDate(ProductSpreadsheetSupport.parseDateTime(cells.get("sales_discontinuation_date")));
        dto.setProductWeight(ProductSpreadsheetSupport.parseDecimal(cells.get("product_weight")));
        dto.setShippingWeight(ProductSpreadsheetSupport.parseDecimal(cells.get("shipping_weight")));
        dto.setProductHeight(ProductSpreadsheetSupport.parseDecimal(cells.get("product_height")));
        dto.setProductWidth(ProductSpreadsheetSupport.parseDecimal(cells.get("product_width")));
        dto.setProductDepth(ProductSpreadsheetSupport.parseDecimal(cells.get("product_depth")));

        row.setCurrency(defaultIfBlank(trim(cells.get("currency")), "INR"));

        BigDecimal taxRate = ProductSpreadsheetSupport.parseDecimal(trim(cells.get("tax_rate")));
        if (taxRate == null) {
            taxRate = ProductSpreadsheetSupport.parseDecimal(trim(cells.get("gst_percent")));
        }
        row.setTaxRate(taxRate);
        BigDecimal averageCostTax = ProductSpreadsheetSupport.parseDecimal(trim(cells.get("average_cost_tax")));
        if (averageCostTax == null) {
            averageCostTax = ProductSpreadsheetSupport.parseDecimal(trim(cells.get("average_gst")));
        }
        row.setAverageCostTax(averageCostTax);

        String keywords = trim(cells.get("keywords"));
        if (keywords != null) {
            List<String> keywordList = new ArrayList<>();
            for (String part : keywords.split(",")) {
                String keyword = part.trim();
                if (!keyword.isEmpty()) {
                    keywordList.add(keyword);
                }
            }
            dto.setKeywords(keywordList);
        }

        String categories = trim(cells.get("category_ids"));
        if (categories != null) {
            for (String part : categories.split(",")) {
                String categoryId = part.trim();
                if (!categoryId.isEmpty()) {
                    row.getCategoryIds().add(categoryId);
                }
            }
        }

        for (int i = 1; i <= ProductSpreadsheetColumns.ATTR_SLOTS; i++) {
            String name = trim(cells.get("attr_" + i + "_name"));
            String value = trim(cells.get("attr_" + i + "_value"));
            if (name != null) {
                ProductAttributeDto attribute = new ProductAttributeDto();
                attribute.setAttrName(name);
                attribute.setAttrValue(value);
                row.getAttributes().add(attribute);
            }
        }

        BigDecimal saleTaxRate = row.getTaxRate() != null ? row.getTaxRate() : new BigDecimal("18");
        BigDecimal costTaxRate = row.getAverageCostTax() != null ? row.getAverageCostTax() : saleTaxRate;
        String currency = row.getCurrency();

        for (Map.Entry<String, String> entry : cells.entrySet()) {
            String header = entry.getKey();
            if (!ProductSpreadsheetColumns.isPriceTypeColumn(header)) {
                continue;
            }
            String amountText = trim(entry.getValue());
            if (amountText == null) {
                continue;
            }
            String priceTypeId = ProductSpreadsheetColumns.normalizePriceTypeHeader(header);
            boolean averageCost = ProductSpreadsheetColumns.AVERAGE_COST_TYPE.equals(priceTypeId);
            BigDecimal amount = ProductSpreadsheetSupport.parseDecimal(amountText);
            BigDecimal taxRateForPrice = averageCost ? costTaxRate : saleTaxRate;
            String taxInPrice = ProductPriceTaxSupport.resolveTaxInPrice(amount, taxRateForPrice, averageCost);

            ProductPriceClientDto price = new ProductPriceClientDto();
            price.setProductPriceTypeId(priceTypeId);
            price.setProductPricePurposeId(ProductSpreadsheetColumns.PURCHASE_PRICE_PURPOSE);
            price.setCurrencyUomId(currency);
            price.setProductStoreGroupId("_NA_");
            price.setPrice(amount);
            price.setTaxPercentage(taxRateForPrice);
            price.setTaxInPrice(taxInPrice);
            row.getPrices().add(price);
        }

        if (!row.getCategoryIds().isEmpty()) {
            dto.setPrimaryProductCategoryId(row.getCategoryIds().get(0));
        }

        return row;
    }

    public void applyImagePaths(String productId) {
        ProductDto dto = product;
        dto.setSmallImageUrl(ProductImagePathSupport.normalizeImportPath(productId, "small", dto.getSmallImageUrl()));
        dto.setMediumImageUrl(ProductImagePathSupport.normalizeImportPath(productId, "medium", dto.getMediumImageUrl()));
        dto.setLargeImageUrl(ProductImagePathSupport.normalizeImportPath(productId, "large", dto.getLargeImageUrl()));
        dto.setDetailImageUrl(ProductImagePathSupport.normalizeImportPath(productId, "original", dto.getDetailImageUrl()));
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

    private static Boolean parseYn(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return "Y".equalsIgnoreCase(value.trim()) || "YES".equalsIgnoreCase(value.trim())
                || "TRUE".equalsIgnoreCase(value.trim());
    }
}
