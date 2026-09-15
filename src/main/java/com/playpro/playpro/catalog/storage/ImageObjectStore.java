package com.playpro.playpro.catalog.storage;

import org.springframework.core.io.Resource;

import java.util.Optional;

/**
 * Abstraction over local filesystem or Google Cloud Storage for catalog media.
 */
public interface ImageObjectStore {

    /**
     * Store (replace) an image. Implementations delete prior files for the same entity that share
     * {@code fileNamePrefix} (e.g. {@code large.} or {@code image.}) before writing.
     */
    StoredImageObject store(String folder,
                            String entityId,
                            String fileName,
                            String fileNamePrefix,
                            byte[] content,
                            String contentType);

    Optional<String> findFileName(String folder, String entityId, String fileNamePrefix);

    Resource load(String folder, String entityId, String fileName);

    String publicUrl(String folder, String entityId, String fileName);

    /** Whether this backend serves bytes via the catalog HTTP API (local) or public GCS/CDN URLs. */
    boolean servesViaCatalogApi();
}
