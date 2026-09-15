package com.playpro.playpro.catalog.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * catalog.storage.* — {@code local} (default) or {@code gcs} for production.
 */
@Component
@ConfigurationProperties(prefix = "catalog.storage")
public class CatalogStorageProperties {

    /**
     * {@code local} = filesystem under data/; {@code gcs} = Google Cloud Storage bucket.
     */
    private String type = "local";

    private final Gcs gcs = new Gcs();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Gcs getGcs() {
        return gcs;
    }

    public boolean isGcs() {
        return type != null && "gcs".equalsIgnoreCase(type.trim());
    }

    public static class Gcs {
        /** GCS bucket name (required when type=gcs). */
        private String bucket = "";

        /**
         * Public URL prefix for objects (no trailing slash), e.g. CDN origin or
         * {@code https://storage.googleapis.com/<bucket>}. Empty → storage.googleapis.com default.
         */
        private String publicBaseUrl = "";

        public String getBucket() {
            return bucket;
        }

        public void setBucket(String bucket) {
            this.bucket = bucket;
        }

        public String getPublicBaseUrl() {
            return publicBaseUrl;
        }

        public void setPublicBaseUrl(String publicBaseUrl) {
            this.publicBaseUrl = publicBaseUrl;
        }

        public String resolvePublicBaseUrl() {
            if (publicBaseUrl != null && !publicBaseUrl.trim().isEmpty()) {
                String base = publicBaseUrl.trim();
                while (base.endsWith("/")) {
                    base = base.substring(0, base.length() - 1);
                }
                return base;
            }
            if (bucket == null || bucket.trim().isEmpty()) {
                throw new IllegalStateException("catalog.storage.gcs.bucket is required when type=gcs");
            }
            return "https://storage.googleapis.com/" + bucket.trim();
        }
    }
}
