package com.playpro.playpro.catalog.storage;

/**
 * Result of storing an image object (local disk or GCS).
 */
public final class StoredImageObject {

    private final String objectKey;
    private final String fileName;
    private final String publicUrl;

    public StoredImageObject(String objectKey, String fileName, String publicUrl) {
        this.objectKey = objectKey;
        this.fileName = fileName;
        this.publicUrl = publicUrl;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public String getFileName() {
        return fileName;
    }

    public String getPublicUrl() {
        return publicUrl;
    }
}
