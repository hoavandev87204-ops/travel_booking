package com.example.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.entity.Category;
import com.example.backend.repository.CategoryRepository;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    // deatail
    public List<Category> getAllCategory() {
        return categoryRepository.findAll();
    }

    // find id
    public Optional<Category> geOptional(Long id) {
        return categoryRepository.findById(id);
    }

    // add
    public Category creatCategory(String name) {
        Category category = new Category();
        category.SetName(name);
        return categoryRepository.save(category);
    }

    // update
    public Category upCategory(Long id, String name) {
        return categoryRepository.findById(id).map(cat -> {
            cat.SetName(name);
            return categoryRepository.save(cat);
        }).orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với id" + id));
    }

    // delete
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}
