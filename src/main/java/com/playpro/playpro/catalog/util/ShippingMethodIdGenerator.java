package com.playpro.playpro.catalog.util;

import java.util.UUID;

public final class ShippingMethodIdGenerator {

    private ShippingMethodIdGenerator() {
    }

    public static String nextId() {
        return "SSM-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }
}
