-- GCS / CDN public URLs exceed the original 250-char header_logo limit.
ALTER TABLE prod_catalog MODIFY COLUMN header_logo VARCHAR(2000);
