package vn.iotstar.controller;

import vn.iotstar.entity.Product;
import vn.iotstar.service.CategoryService;
import vn.iotstar.service.FileStorageService;
import vn.iotstar.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping
    public String listProducts(Model model) {
        model.addAttribute("products", productService.findAll());
        return "products/list";
    }

    @GetMapping("/search")
    public String searchProducts(@RequestParam("keyword") String keyword, Model model) {
        model.addAttribute("products", productService.search(keyword));
        return "products/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.findAll());
        return "products/form";
    }

    @PostMapping("/add")
    public String addProduct(@Valid @ModelAttribute("product") Product product, 
                             BindingResult result, 
                             @RequestParam("imageFile") MultipartFile imageFile, 
                             Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.findAll());
            return "products/form";
        }

        if (!imageFile.isEmpty()) {
            String imageUrl = fileStorageService.storeFile(imageFile);
            product.setImages(imageUrl);
        }

        productService.save(product);
        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Product product = productService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.findAll());
        return "products/form";
    }

    @PostMapping("/edit/{id}")
    public String updateProduct(@PathVariable("id") Long id, 
                                @Valid @ModelAttribute("product") Product product, 
                                BindingResult result, 
                                @RequestParam("imageFile") MultipartFile imageFile,
                                Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.findAll());
            product.setId(id);
            return "products/form";
        }

        Optional<Product> optionalProduct = productService.findById(id);
        if (optionalProduct.isPresent()) {
            Product existingProduct = optionalProduct.get();

            if (!imageFile.isEmpty()) {
                String imageUrl = fileStorageService.storeFile(imageFile);
                product.setImages(imageUrl);
            } else {
                product.setImages(existingProduct.getImages());
            }
        } else {
             throw new IllegalArgumentException("Invalid product Id:" + id);
        }

        product.setId(id);
        productService.save(product);
        return "redirect:/products";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id) {
        productService.deleteById(id);
        return "redirect:/products";
    }
}
