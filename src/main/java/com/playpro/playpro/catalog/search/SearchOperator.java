package com.playpro.playpro.catalog.search;

/**
 * OFBiz text-find operators (ProductForms FindProduct).
 * Maps to HTML select values: equals, like, contains, empty, notEqual.
 */
public enum SearchOperator {
    EQUALS("equals"),
    BEGINS_WITH("like"),
    CONTAINS("contains"),
    IS_EMPTY("empty"),
    NOT_EQUAL("notEqual"),
    NOT_EMPTY("notEmpty");

    private final String ofbizCode;

    SearchOperator(String ofbizCode) {
        this.ofbizCode = ofbizCode;
    }

    public String getOfbizCode() {
        return ofbizCode;
    }

    public static SearchOperator fromCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return CONTAINS;
        }
        String normalized = code.trim();
        for (SearchOperator op : values()) {
            if (op.name().equalsIgnoreCase(normalized)
                    || op.ofbizCode.equalsIgnoreCase(normalized)
                    || op.ofbizCode.equalsIgnoreCase(normalized.replace("_", ""))) {
                return op;
            }
        }
        if ("begins".equalsIgnoreCase(normalized) || "starts".equalsIgnoreCase(normalized)
                || "startswith".equalsIgnoreCase(normalized)) {
            return BEGINS_WITH;
        }
        if ("not_empty".equalsIgnoreCase(normalized) || "notempty".equalsIgnoreCase(normalized)) {
            return NOT_EMPTY;
        }
        throw new IllegalArgumentException("Unknown search operator: " + code);
    }
}
