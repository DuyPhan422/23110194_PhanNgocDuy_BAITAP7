package vn.iotstar.Controllers;

import vn.iotstar.Entities.Category;
import vn.iotstar.Service.CategoryService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/categories")
public class AdminCategoryController {
    private final CategoryService service;

    public AdminCategoryController(CategoryService service) {
        this.service = service;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "5") int size,
                       Model model) {
        Pageable pageable = PageRequest.of(page, size);
        model.addAttribute("categories", service.findAll(pageable));
        return "admin/categories/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("category", new Category());
        return "admin/categories/addOrEdit";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Category category) {
        service.save(category);
        return "redirect:/admin/categories";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("category", service.get(id));
        return "admin/categories/addOrEdit";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("category", service.get(id));
        return "admin/categories/detail";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/admin/categories";
    }
}