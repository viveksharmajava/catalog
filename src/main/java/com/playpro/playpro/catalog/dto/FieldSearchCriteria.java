package com.playpro.playpro.catalog.dto;

public class FieldSearchCriteria {

    private String value;
    private String operator = "contains";
    private boolean ignoreCase = true;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public boolean isIgnoreCase() {
        return ignoreCase;
    }

    public void setIgnoreCase(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
    }

    public boolean hasCondition() {
        if (operator == null) {
            return value != null && !value.trim().isEmpty();
        }
        String op = operator.trim().toLowerCase();
        if ("empty".equals(op) || "notempty".equals(op) || "not_empty".equals(op)) {
            return true;
        }
        return value != null && !value.trim().isEmpty();
    }
}
