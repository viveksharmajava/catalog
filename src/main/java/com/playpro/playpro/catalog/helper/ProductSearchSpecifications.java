package com.playpro.playpro.catalog.helper;

import com.playpro.playpro.catalog.dto.FieldSearchCriteria;
import com.playpro.playpro.catalog.search.SearchOperator;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Builds JPA Specifications from OFBiz-style text-find criteria.
 */
public final class ProductSearchSpecifications {

    private ProductSearchSpecifications() {
    }

    public static <T> Specification<T> fieldCriteria(String attribute, FieldSearchCriteria criteria) {
        if (criteria == null || !criteria.hasCondition()) {
            return null;
        }
        return (root, query, cb) -> buildPredicate(root, cb, attribute, criteria);
    }

    public static Specification<com.playpro.playpro.catalog.entity.product.Product> combine(
            Specification<com.playpro.playpro.catalog.entity.product.Product> first,
            Specification<com.playpro.playpro.catalog.entity.product.Product> second,
            Specification<com.playpro.playpro.catalog.entity.product.Product> third) {
        Specification<com.playpro.playpro.catalog.entity.product.Product> combined = first;
        if (second != null) {
            combined = combined == null ? second : combined.and(second);
        }
        if (third != null) {
            combined = combined == null ? third : combined.and(third);
        }
        return combined;
    }

    private static <T> Predicate buildPredicate(Root<T> root, CriteriaBuilder cb,
                                                String attribute, FieldSearchCriteria criteria) {
        SearchOperator operator = SearchOperator.fromCode(criteria.getOperator());
        Expression<String> field = root.get(attribute);
        Expression<String> expr = criteria.isIgnoreCase() ? cb.lower(field) : field;

        switch (operator) {
            case IS_EMPTY:
                return cb.or(cb.isNull(field), cb.equal(field, ""));
            case NOT_EMPTY:
                return cb.and(cb.isNotNull(field), cb.notEqual(field, ""));
            case EQUALS:
                return cb.equal(expr, normalizeValue(criteria.getValue(), criteria.isIgnoreCase()));
            case NOT_EQUAL:
                return cb.notEqual(expr, normalizeValue(criteria.getValue(), criteria.isIgnoreCase()));
            case BEGINS_WITH:
                return cb.like(expr, normalizeValue(criteria.getValue(), criteria.isIgnoreCase()) + "%");
            case CONTAINS:
            default:
                return cb.like(expr, "%" + normalizeValue(criteria.getValue(), criteria.isIgnoreCase()) + "%");
        }
    }

    private static String normalizeValue(String value, boolean ignoreCase) {
        String v = value == null ? "" : value.trim();
        return ignoreCase ? v.toLowerCase() : v;
    }
}
