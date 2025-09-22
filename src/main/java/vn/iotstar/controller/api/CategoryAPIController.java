package vn.iotstar.controller.api;

import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.iotstar.entity.Category;
import vn.iotstar.model.Response;
import vn.iotstar.service.ICategoryService;
import vn.iotstar.service.IStorageService;

@RestController
@RequestMapping(path = "/api/category")
public class CategoryAPIController {

    @Autowired private ICategoryService categoryService;
    @Autowired private IStorageService storageService;

    
    @GetMapping
    public ResponseEntity<?> getAllCategory(
            @RequestParam(value="q", defaultValue = "") String q,
            @RequestParam(value="page", defaultValue = "0") int page,
            @RequestParam(value="size", defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("categoryId").descending());
        Page<Category> data = (q == null || q.isBlank())
                ? categoryService.findAll(pageable)
                : categoryService.findByCategoryNameContaining(q, pageable);
        return new ResponseEntity<>(new Response(true, "Thành công", data), HttpStatus.OK);
    }

    @PostMapping(path = "/getCategory")
    public ResponseEntity<?> getCategory(@Validated @RequestParam("id") Long id) {
        Optional<Category> category = categoryService.findById(id);
        if (category.isPresent()) {
            return new ResponseEntity<>(new Response(true, "Thành công", category.get()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new Response(false, "Thất bại", null), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(path = "/addCategory", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addCategory(@Validated @RequestParam("categoryName") String categoryName,
                                         @Validated @RequestParam(value="icon", required=false) MultipartFile icon) {
        Optional<Category> optCategory = categoryService.findByCategoryName(categoryName);
        if (optCategory.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Category đã tồn tại trong hệ thống");
        } else {
            Category category = new Category();
            if (icon != null && !icon.isEmpty()) {
                String uuString = UUID.randomUUID().toString();
                category.setIcon(storageService.getSorageFilename(icon, uuString));
                storageService.store(icon, category.getIcon());
            }
            category.setCategoryName(categoryName);
            categoryService.save(category);
            return new ResponseEntity<>(new Response(true, "Thêm Thành công", category), HttpStatus.OK);
        }
    }

    @PutMapping(path = "/updateCategory", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateCategory(@Validated @RequestParam("categoryId") Long categoryId,
                                            @Validated @RequestParam("categoryName") String categoryName,
                                            @RequestParam(value="icon", required=false) MultipartFile icon) {
        Optional<Category> optCategory = categoryService.findById(categoryId);
        if (optCategory.isEmpty()) {
            return new ResponseEntity<>(new Response(false, "Không tìm thấy Category", null), HttpStatus.BAD_REQUEST);
        }
        Category c = optCategory.get();

      
        if (icon != null && !icon.isEmpty()) {
            String uuString = UUID.randomUUID().toString();
            c.setIcon(storageService.getSorageFilename(icon, uuString));
            storageService.store(icon, c.getIcon());
        }

        c.setCategoryName(categoryName);
        categoryService.save(c);
        return new ResponseEntity<>(new Response(true, "Cập nhật Thành công", c), HttpStatus.OK);
    }

    @DeleteMapping(path = "/deleteCategory")
    public ResponseEntity<?> deleteCategory(@Validated @RequestParam("categoryId") Long categoryId){
        Optional<Category> optCategory = categoryService.findById(categoryId);
        if (optCategory.isEmpty()) {
            return new ResponseEntity<>(new Response(false, "Không tìm thấy Category", null), HttpStatus.BAD_REQUEST);
        } else {
            categoryService.delete(optCategory.get());
            return new ResponseEntity<>(new Response(true, "Xóa Thành công", optCategory.get()), HttpStatus.OK);
        }
    }
}
