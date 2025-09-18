package vn.iotstar.baitapcrud.controller;

import vn.iotstar.baitapcrud.entity.Category;
import vn.iotstar.baitapcrud.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CategoryController {
    
    @Autowired
    private CategoryService categoryService;
    
    // Hiển thị danh sách danh mục với phân trang và tìm kiếm
    @GetMapping("/categories")
    public String listCategories(@RequestParam(required = false) String keyword,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "5") int size,
                                 Model model) {
        Page<Category> categoryPage = categoryService.searchCategories(keyword, page, size);
        model.addAttribute("categories", categoryPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", categoryPage.getTotalPages());
        model.addAttribute("keyword", keyword);
        return "list";  // Trả về list.html
    }
    
    // Hiển thị form thêm mới
    @GetMapping("/categories/add")
    public String addCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        return "addOrEdit";  // Trả về addOrEdit.html
    }
    
    // Lưu danh mục (thêm hoặc sửa)
    @PostMapping("/categories/save")
    public String saveCategory(@ModelAttribute Category category, Model model) {
        categoryService.saveCategory(category);
        return "redirect:/categories";  // Redirect về danh sách
    }
    
    // Hiển thị form sửa
    @GetMapping("/categories/edit/{id}")
    public String editCategoryForm(@PathVariable Long id, Model model) {
        Category category = categoryService.getCategoryById(id).orElse(null);
        model.addAttribute("category", category);
        return "addOrEdit";
    }
    
    // Xóa danh mục
    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return "redirect:/categories";
    }
}