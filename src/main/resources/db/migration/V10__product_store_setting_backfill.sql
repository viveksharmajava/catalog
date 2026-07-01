-- Backfill storefront settings for product stores created before product_store_setting existed

INSERT INTO product_store_setting (
    product_store_id,
    is_default_store,
    contact_us_link,
    about_us_link,
    shipping_policy_link,
    returns_link,
    privacy_policy_link,
    terms_and_conditions_link,
    last_modified_date
)
SELECT
    ps.product_store_id,
    'N',
    '/contact',
    '/pages/about-us',
    '/pages/shipping-policy',
    '/pages/refund-policy',
    '/pages/privacy-policy',
    '/pages/terms-and-conditions',
    CURRENT_TIMESTAMP
FROM product_store ps
LEFT JOIN product_store_setting pss ON ps.product_store_id = pss.product_store_id
WHERE pss.product_store_id IS NULL;
