package com.playpro.playpro.catalog.util;

import java.util.UUID;

public final class PaymentMethodIdGenerator {

    private PaymentMethodIdGenerator() {
    }

    public static String nextId() {
        return "PSM-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }
}
