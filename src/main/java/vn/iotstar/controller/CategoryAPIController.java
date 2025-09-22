package vn.iotstar.controller;

import vn.iotstar.entity.Category;
import vn.iotstar.service.CategoryService;
import vn.iotstar.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/category")
public class CategoryAPIController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private FileStorageService fileStorageService;

    // Endpoint to get list of categories
    @GetMapping
    public ResponseEntity<List<Category>> listCategories() {
        List<Category> categories = categoryService.findAll();
        if (categories.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    // Endpoint to get a category by id
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategory(@PathVariable("id") Integer id) {
        Category category = categoryService.findById(id);
        if (category == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    // Endpoint to create a new category
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addCategory(
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "image", required = false) MultipartFile imageFile) {
        try {
            Category category = new Category();
            category.setName(name);
            category.setDescription(description);

            if (imageFile != null && !imageFile.isEmpty()) {
                // Using the existing FileStorageService
                String fileName = fileStorageService.storeFile(imageFile);
                category.setImage(fileName);
            }

            categoryService.save(category);
            return new ResponseEntity<>(category, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error creating category: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Endpoint to update a category
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateCategory(
            @PathVariable("id") Integer id,
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "image", required = false) MultipartFile imageFile) {

        Category currentCategory = categoryService.findById(id);
        if (currentCategory == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        try {
            currentCategory.setName(name);
            currentCategory.setDescription(description);

            if (imageFile != null && !imageFile.isEmpty()) {
                String fileName = fileStorageService.storeFile(imageFile);
                // Consider deleting the old file
                if (currentCategory.getImage() != null) {
                    try {
                        fileStorageService.deleteFile(currentCategory.getImage());
                    } catch (Exception e) {
                        // Log the error, but continue the update process
                        System.err.println("Could not delete old file: " + currentCategory.getImage());
                    }
                }
                currentCategory.setImage(fileName);
            }

            categoryService.save(currentCategory);
            return new ResponseEntity<>(currentCategory, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error updating category: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Endpoint to delete a category
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteCategory(@PathVariable("id") Integer id) {
        try {
            Category category = categoryService.findById(id);
            if (category == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            
            // Delete associated image file
            if (category.getImage() != null) {
                 try {
                    fileStorageService.deleteFile(category.getImage());
                } catch (Exception e) {
                    System.err.println("Could not delete file for category ID " + id + ": " + e.getMessage());
                }
            }

            categoryService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
