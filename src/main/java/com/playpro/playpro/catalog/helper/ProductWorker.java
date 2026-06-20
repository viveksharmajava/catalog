package com.playpro.playpro.catalog.helper;

import com.playpro.playpro.catalog.entity.product.Product;
import com.playpro.playpro.catalog.entity.product.ProductType;
import com.playpro.playpro.catalog.util.IndicatorUtil;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Product domain helper inspired by Apache OFBiz {@code ProductWorker}.
 * Pure product-level logic; inventory, pricing, and promotions live in other services.
 */
@Component
public class ProductWorker {

    public static final String STATUS_DRAFT = "DRAFT";
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_INACTIVE = "INACTIVE";
    public static final String STATUS_DISCONTINUED = "DISCONTINUED";

    public static final String ASSOC_VARIANT = "PRODUCT_VARIANT";
    public static final String ASSOC_ACCESSORY = "PRODUCT_ACCESSORY";
    public static final String ID_TYPE_SKU = "SKU";

    public boolean isVirtual(Product product) {
        return product != null && IndicatorUtil.isYes(product.getIsVirtual());
    }

    public boolean isVariant(Product product) {
        return product != null && IndicatorUtil.isYes(product.getIsVariant());
    }

    public boolean isDigital(Product product) {
        if (product == null || product.getProductType() == null) {
            return false;
        }
        return IndicatorUtil.isYes(product.getProductType().getIsDigital());
    }

    public boolean isPhysical(Product product) {
        if (product == null || product.getProductType() == null) {
            return true;
        }
        return IndicatorUtil.isYes(product.getProductType().getIsPhysical());
    }

    public boolean shippingApplies(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product is required");
        }
        ProductType type = product.getProductType();
        if (type != null && IndicatorUtil.isYes(type.getIsDigital()) && !IndicatorUtil.isYes(type.getIsPhysical())) {
            return false;
        }
        if ("SERVICE".equals(product.getProductTypeId())) {
            return false;
        }
        return product.getChargeShipping() == null || IndicatorUtil.isYes(product.getChargeShipping());
    }

    public boolean taxApplies(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product is required");
        }
        Boolean taxable = IndicatorUtil.fromIndicator(product.getTaxable());
        return taxable == null || taxable;
    }

    public boolean isReturnable(Product product) {
        if (product == null) {
            return false;
        }
        Boolean returnable = IndicatorUtil.fromIndicator(product.getReturnable());
        return returnable == null || returnable;
    }

    public boolean isSalesDiscontinued(Product product) {
        if (product == null) {
            return true;
        }
        if (STATUS_DISCONTINUED.equals(product.getStatusId())) {
            return true;
        }
        LocalDateTime discDate = product.getSalesDiscontinuationDate();
        return discDate != null && !discDate.isAfter(LocalDateTime.now());
    }

    public boolean isSellable(Product product) {
        return product != null
                && STATUS_ACTIVE.equals(product.getStatusId())
                && !isSalesDiscontinued(product);
    }

    public boolean isIntroduced(Product product) {
        if (product == null) {
            return false;
        }
        LocalDateTime intro = product.getIntroductionDate();
        return intro == null || !intro.isAfter(LocalDateTime.now());
    }

    public boolean isReleased(Product product) {
        if (product == null) {
            return false;
        }
        LocalDateTime release = product.getReleaseDate();
        return release == null || !release.isAfter(LocalDateTime.now());
    }

    public boolean isAvailableForSale(Product product) {
        return isSellable(product) && isIntroduced(product) && isReleased(product);
    }
}
