package vn.iotstar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping({"/admin","/"})
public class AdminPageController {

    
    @GetMapping({"", "/"})
    public String defaultPage() {
        return "redirect:/admin/categories"; 
    }

    @GetMapping("/categories")
    public String categoriesPage() {
        return "admin/categories"; 
    }

    @GetMapping("/products")
    public String productsPage() {
        return "admin/products";   
    }
}
