package com.playpro.playpro.catalog.mapper;

import com.playpro.playpro.catalog.dto.ProductDto;
import com.playpro.playpro.catalog.entity.Product;

public class ProductMapper {
    public static ProductDto toDto(Product p) {
        if (p == null) return null;
        ProductDto d = new ProductDto();
        d.setId(p.getId());
        d.setSku(p.getSku());
        d.setName(p.getName());
        d.setDescription(p.getDescription());
        d.setStatus(p.getStatus());
        d.setVersion(p.getVersion());
        return d;
    }

    public static Product toEntity(ProductDto d) {
        if (d == null) return null;
        Product p = new Product();
        p.setId(d.getId());
        p.setSku(d.getSku());
        p.setName(d.getName());
        p.setDescription(d.getDescription());
        p.setStatus(d.getStatus());
        p.setVersion(d.getVersion());
        return p;
    }
}
