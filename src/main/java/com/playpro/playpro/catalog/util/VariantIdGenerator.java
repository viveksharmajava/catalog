package com.playpro.playpro.catalog.util;

import java.util.UUID;

public final class VariantIdGenerator {

    private VariantIdGenerator() {
    }

    public static String nextTypeId() {
        return "VRT-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }

    public static String nextValueId() {
        return "VRV-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }
}
