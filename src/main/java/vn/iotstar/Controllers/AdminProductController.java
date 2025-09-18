package vn.iotstar.Controllers;

import vn.iotstar.Entities.Account;
import vn.iotstar.Entities.Product;
import vn.iotstar.Service.CategoryService;
import vn.iotstar.Service.ProductService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {
    private final ProductService service;
    private final CategoryService categoryService;

    public AdminProductController(ProductService service, CategoryService categoryService) {
        this.service = service;
        this.categoryService = categoryService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("products", service.findAll());
        return "admin/products/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.findAll());
        return "admin/products/addOrEdit";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Product product,
                       @RequestParam("file") MultipartFile file) throws IOException {
        if (product.getCategory() != null && product.getCategory().getId() != null) {
            product.setCategory(categoryService.get(product.getCategory().getId()));
        }

        if (!file.isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path path = Paths.get("uploads/" + fileName);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            product.setImage(fileName);
        }

        service.save(product);
        return "redirect:/admin/products";
    }


    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("product", service.get(id));
        model.addAttribute("categories", categoryService.findAll());
        return "admin/products/addOrEdit";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("product", service.get(id));
        return "admin/products/detail";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/admin/products";
    }
    @GetMapping("/search")
    public String search(@RequestParam(required = false) String keyword, Model model) {
        List<Product> results;
        if (keyword != null && !keyword.isEmpty()) {
            results = service.searchNoPaging(keyword); 
        } else {
            results = service.findAll();
        }

        model.addAttribute("results", results);
        model.addAttribute("keyword", keyword);

        return "admin/products/search";
    }

    // Search paginated
    @GetMapping("/searchpaginated")
    public String searchPaginated(@RequestParam(required = false) String keyword,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "5") int size,
                                  Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> result = service.searchPaging(keyword, pageable);

        model.addAttribute("page", result);
        model.addAttribute("keyword", keyword);

        return "admin/products/searchpaginated";
    }
}