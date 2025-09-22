package vn.iotstar.controller;

import vn.iotstar.entity.Category;
import vn.iotstar.entity.Product;
import vn.iotstar.repository.CategoryRepository;
import vn.iotstar.service.IProductService;
import vn.iotstar.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/product")
public class ProductAPIController {

    @Autowired
    private IProductService productService;

    @Autowired
    private CategoryRepository categoryRepository; // Use the original repo with Integer ID

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping
    public ResponseEntity<List<Product>> listProducts() {
        List<Product> products = productService.findAll();
        if (products.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable("id") Long id) {
        Optional<Product> product = productService.findById(id);
        return product.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProduct(
            @RequestParam("productName") String productName,
            @RequestParam("unitPrice") double unitPrice,
            @RequestParam("quantity") int quantity,
            @RequestParam("categoryId") Integer categoryId, // Changed to Integer
            @RequestParam(value = "images", required = false) MultipartFile[] images) {

        Optional<Category> category = categoryRepository.findById(categoryId);
        if (category.isEmpty()) {
            return new ResponseEntity<>("Category not found!", HttpStatus.BAD_REQUEST);
        }

        try {
            Product product = new Product();
            product.setProductName(productName);
            product.setUnitPrice(unitPrice);
            product.setQuantity(quantity);
            product.setCategory(category.get());

            if (images != null && images.length > 0) {
                List<String> imageNames = new ArrayList<>();
                for (MultipartFile image : images) {
                    String fileName = fileStorageService.storeFile(image);
                    imageNames.add(fileName);
                }
                product.setImages(String.join(",", imageNames));
            }

            productService.save(product);
            return new ResponseEntity<>(product, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error creating product: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProduct(
            @PathVariable("id") Long id,
            @RequestParam("productName") String productName,
            @RequestParam("unitPrice") double unitPrice,
            @RequestParam("quantity") int quantity,
            @RequestParam("categoryId") Integer categoryId, // Changed to Integer
            @RequestParam(value = "images", required = false) MultipartFile[] images) {

        Optional<Product> existingProductOpt = productService.findById(id);
        if (existingProductOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Optional<Category> category = categoryRepository.findById(categoryId);
        if (category.isEmpty()) {
            return new ResponseEntity<>("Category not found!", HttpStatus.BAD_REQUEST);
        }

        try {
            Product product = existingProductOpt.get();
            product.setProductName(productName);
            product.setUnitPrice(unitPrice);
            product.setQuantity(quantity);
            product.setCategory(category.get());

            if (images != null && images.length > 0) {
                if (product.getImages() != null && !product.getImages().isEmpty()) {
                    Arrays.stream(product.getImages().split(",")).forEach(fileName -> {
                        try {
                            fileStorageService.deleteFile(fileName);
                        } catch (Exception e) {
                            System.err.println("Could not delete old file: " + fileName);
                        }
                    });
                }

                List<String> imageNames = new ArrayList<>();
                for (MultipartFile image : images) {
                    String fileName = fileStorageService.storeFile(image);
                    imageNames.add(fileName);
                }
                product.setImages(String.join(",", imageNames));
            }

            productService.save(product);
            return new ResponseEntity<>(product, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error updating product: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteProduct(@PathVariable("id") Long id) {
        Optional<Product> productOpt = productService.findById(id);
        if (productOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        try {
            Product product = productOpt.get();
            if (product.getImages() != null && !product.getImages().isEmpty()) {
                Arrays.stream(product.getImages().split(",")).forEach(fileName -> {
                    try {
                        fileStorageService.deleteFile(fileName);
                    } catch (Exception e) {
                        System.err.println("Could not delete file: " + fileName);
                    }
                });
            }
            productService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
