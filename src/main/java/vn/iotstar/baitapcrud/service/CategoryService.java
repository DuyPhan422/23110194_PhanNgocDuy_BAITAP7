package vn.iotstar.baitapcrud.service;

import vn.iotstar.baitapcrud.entity.Category;
import vn.iotstar.baitapcrud.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    // Lấy tất cả danh mục
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
    
    // Lấy danh mục theo ID
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }
    
    // Lưu hoặc cập nhật danh mục
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }
    
    // Xóa danh mục
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
    
    // Tìm kiếm với phân trang
    public Page<Category> searchCategories(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        if (keyword == null || keyword.isEmpty()) {
            return categoryRepository.findAll(pageable);
        }
        return categoryRepository.findByNameContainingIgnoreCase(keyword, pageable);
    }
}