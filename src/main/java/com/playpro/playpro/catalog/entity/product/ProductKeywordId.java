package com.playpro.playpro.catalog.entity.product;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ProductKeywordId implements Serializable {

    @Column(name = "product_id", length = 20)
    private String productId;

    @Column(length = 60)
    private String keyword;

    @Column(name = "keyword_type_id", length = 20)
    private String keywordTypeId;

    public ProductKeywordId() {
    }

    public ProductKeywordId(String productId, String keyword, String keywordTypeId) {
        this.productId = productId;
        this.keyword = keyword;
        this.keywordTypeId = keywordTypeId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getKeywordTypeId() {
        return keywordTypeId;
    }

    public void setKeywordTypeId(String keywordTypeId) {
        this.keywordTypeId = keywordTypeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProductKeywordId that = (ProductKeywordId) o;
        return Objects.equals(productId, that.productId)
                && Objects.equals(keyword, that.keyword)
                && Objects.equals(keywordTypeId, that.keywordTypeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, keyword, keywordTypeId);
    }
}
