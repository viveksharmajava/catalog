package com.playpro.playpro.catalog.mapper;

import com.playpro.playpro.catalog.dto.CategoryDto;
import com.playpro.playpro.catalog.entity.Category;

public class CategoryMapper {
    public static CategoryDto toDto(Category c) {
        if (c == null) return null;
        CategoryDto d = new CategoryDto();
        d.setId(c.getId());
        d.setName(c.getName());
        d.setSlug(c.getSlug());
        d.setDescription(c.getDescription());
        d.setParentId(c.getParent() != null ? c.getParent().getId() : null);
        d.setActive(c.getActive());
        return d;
    }

    public static Category toEntity(CategoryDto d) {
        if (d == null) return null;
        Category c = new Category();
        c.setId(d.getId());
        c.setName(d.getName());
        c.setSlug(d.getSlug());
        c.setDescription(d.getDescription());
        c.setActive(d.getActive() == null ? true : d.getActive());
        return c;
    }
}
