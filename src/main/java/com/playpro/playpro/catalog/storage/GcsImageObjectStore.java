package com.playpro.playpro.catalog.storage;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.playpro.playpro.catalog.exception.ResourceNotFoundException;
import com.playpro.playpro.catalog.media.ImageFileSupport;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.Locale;
import java.util.Optional;

/**
 * Stores catalog media in a GCS bucket. Public URLs use {@code catalog.storage.gcs.public-base-url}
 * (CDN) or {@code https://storage.googleapis.com/<bucket>/...}.
 * <p>
 * Uses Application Default Credentials (Cloud Run service account). Ensure the SA can
 * {@code storage.objects.create/delete/get/list} on the bucket, and that objects are publicly
 * readable (or served via CDN).
 */
@Component
@ConditionalOnProperty(name = "catalog.storage.type", havingValue = "gcs")
public class GcsImageObjectStore implements ImageObjectStore {

    private final CatalogStorageProperties properties;
    private Storage storage;
    private String bucket;
    private String publicBaseUrl;

    public GcsImageObjectStore(CatalogStorageProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void init() {
        CatalogStorageProperties.Gcs gcs = properties.getGcs();
        if (!StringUtils.hasText(gcs.getBucket())) {
            throw new IllegalStateException(
                    "catalog.storage.gcs.bucket is required when catalog.storage.type=gcs");
        }
        this.bucket = gcs.getBucket().trim();
        this.publicBaseUrl = gcs.resolvePublicBaseUrl();
        this.storage = StorageOptions.getDefaultInstance().getService();
    }

    @Override
    public StoredImageObject store(String folder,
                                   String entityId,
                                   String fileName,
                                   String fileNamePrefix,
                                   byte[] content,
                                   String contentType) {
        ImageFileSupport.validateFileName(fileName);
        String safeId = ImageFileSupport.sanitizeEntityId(entityId);
        deleteMatching(folder, safeId, fileNamePrefix);

        String objectKey = objectKey(folder, safeId, fileName);
        BlobInfo.Builder builder = BlobInfo.newBuilder(BlobId.of(bucket, objectKey))
                .setCacheControl("public, max-age=31536000");
        if (StringUtils.hasText(contentType)) {
            builder.setContentType(contentType);
        }
        storage.create(builder.build(), content);

        String url = publicUrl(folder, entityId, fileName);
        return new StoredImageObject(objectKey, fileName, url);
    }

    @Override
    public Optional<String> findFileName(String folder, String entityId, String fileNamePrefix) {
        String safeId = ImageFileSupport.sanitizeEntityId(entityId);
        String prefix = folder + "/" + safeId + "/";
        String namePrefix = fileNamePrefix == null ? "" : fileNamePrefix.toLowerCase(Locale.ROOT);
        for (Blob blob : storage.list(bucket, Storage.BlobListOption.prefix(prefix)).iterateAll()) {
            String name = blob.getName();
            if (name == null || name.endsWith("/")) {
                continue;
            }
            String fileName = name.substring(name.lastIndexOf('/') + 1);
            if (fileName.toLowerCase(Locale.ROOT).startsWith(namePrefix)) {
                return Optional.of(fileName);
            }
        }
        return Optional.empty();
    }

    @Override
    public Resource load(String folder, String entityId, String fileName) {
        ImageFileSupport.validateFileName(fileName);
        String objectKey = objectKey(folder, ImageFileSupport.sanitizeEntityId(entityId), fileName);
        Blob blob = storage.get(BlobId.of(bucket, objectKey));
        if (blob == null || !blob.exists()) {
            throw new ResourceNotFoundException("Image not found: " + fileName);
        }
        byte[] content = blob.getContent();
        return new ByteArrayResource(content) {
            @Override
            public String getFilename() {
                return fileName;
            }
        };
    }

    @Override
    public String publicUrl(String folder, String entityId, String fileName) {
        String safeId = ImageFileSupport.sanitizeEntityId(entityId);
        return publicBaseUrl + "/" + objectKey(folder, safeId, fileName);
    }

    @Override
    public boolean servesViaCatalogApi() {
        return false;
    }

    private void deleteMatching(String folder, String safeEntityId, String fileNamePrefix) {
        if (fileNamePrefix == null || fileNamePrefix.isEmpty()) {
            return;
        }
        String prefix = folder + "/" + safeEntityId + "/";
        String namePrefix = fileNamePrefix.toLowerCase(Locale.ROOT);
        for (Blob blob : storage.list(bucket, Storage.BlobListOption.prefix(prefix)).iterateAll()) {
            String name = blob.getName();
            if (name == null || name.endsWith("/")) {
                continue;
            }
            String fileName = name.substring(name.lastIndexOf('/') + 1);
            if (fileName.toLowerCase(Locale.ROOT).startsWith(namePrefix)) {
                storage.delete(blob.getBlobId());
            }
        }
    }

    private static String objectKey(String folder, String entityId, String fileName) {
        return folder + "/" + entityId + "/" + fileName;
    }
}
