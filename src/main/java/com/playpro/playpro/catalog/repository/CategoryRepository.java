package com.playpro.playpro.catalog.repository;

import com.playpro.playpro.catalog.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByParent_Id(Long parentId);
    List<Category> findByActiveTrue();
}
