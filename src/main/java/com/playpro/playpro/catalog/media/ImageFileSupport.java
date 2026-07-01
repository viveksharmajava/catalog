package com.playpro.playpro.catalog.media;

import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public final class ImageFileSupport {

    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(Arrays.asList(
            "jpg", "jpeg", "png", "gif", "webp"
    ));

    private ImageFileSupport() {
    }

    public static String resolveExtension(MultipartFile file) {
        String original = file.getOriginalFilename();
        if (original != null && original.contains(".")) {
            String ext = original.substring(original.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
            if (ALLOWED_EXTENSIONS.contains(ext)) {
                return "jpeg".equals(ext) ? "jpg" : ext;
            }
        }

        String contentType = file.getContentType();
        if (contentType != null) {
            switch (contentType.toLowerCase(Locale.ROOT)) {
                case "image/jpeg":
                    return "jpg";
                case "image/png":
                    return "png";
                case "image/gif":
                    return "gif";
                case "image/webp":
                    return "webp";
                default:
                    break;
            }
        }

        throw new IllegalArgumentException("Unsupported image type. Allowed: jpg, png, gif, webp");
    }

    public static void validateFileName(String fileName) {
        if (fileName == null || fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
            throw new IllegalArgumentException("Invalid file name");
        }
        int dot = fileName.lastIndexOf('.');
        if (dot <= 0) {
            throw new IllegalArgumentException("Invalid file name");
        }
        String ext = fileName.substring(dot + 1).toLowerCase(Locale.ROOT);
        if (!ALLOWED_EXTENSIONS.contains(ext) && !"jpeg".equals(ext)) {
            throw new IllegalArgumentException("Unsupported image type");
        }
    }

    public static String sanitizeEntityId(String entityId) {
        if (entityId == null || entityId.trim().isEmpty()) {
            throw new IllegalArgumentException("entity id is required");
        }
        String sanitized = entityId.trim().replaceAll("[^A-Za-z0-9._-]", "_");
        if (sanitized.isEmpty()) {
            throw new IllegalArgumentException("Invalid entity id");
        }
        return sanitized;
    }

    public static String buildPublicUrl(String baseUrl, String pathSegment, String entityId, String fileName) {
        String base = baseUrl;
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        return base + "/catalog/" + pathSegment + "/" + sanitizeEntityId(entityId) + "/" + fileName;
    }

    public static MediaType resolveMediaType(String fileName) {
        String lower = fileName.toLowerCase(Locale.ROOT);
        if (lower.endsWith(".png")) {
            return MediaType.IMAGE_PNG;
        }
        if (lower.endsWith(".gif")) {
            return MediaType.IMAGE_GIF;
        }
        if (lower.endsWith(".webp")) {
            return MediaType.parseMediaType("image/webp");
        }
        return MediaType.IMAGE_JPEG;
    }
}
