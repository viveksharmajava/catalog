package com.playpro.playpro.catalog.dto;

public class CategoryDto {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private Long parentId;
    private Boolean active;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
