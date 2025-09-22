package vn.iotstar.controller.api;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.iotstar.entity.Category;
import vn.iotstar.entity.Product;
import vn.iotstar.model.Response;
import vn.iotstar.repository.CategoryRepository;
import vn.iotstar.service.IProductService;
import vn.iotstar.service.IStorageService;

@RestController
@RequestMapping(path = "/api/product")
public class ProductAPIController {

    @Autowired private IProductService productService;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private IStorageService storageService;

    @GetMapping
    public ResponseEntity<?> getAllProduct(
            @RequestParam(value="q", defaultValue = "") String q,
            @RequestParam(value="page", defaultValue = "0") int page,
            @RequestParam(value="size", defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("productId").descending());
        Page<Product> data = (q == null || q.isBlank())
                ? productService.findAll(pageable)
                : productService.findByProductNameContaining(q, pageable);
        return new ResponseEntity<>(new Response(true, "Thành công", data), HttpStatus.OK);
    }

    @PostMapping(path = "/getProduct")
    public ResponseEntity<?> getProduct(@Validated @RequestParam("id") Long id) {
        Optional<Product> p = productService.findById(id);
        if (p.isPresent()) {
            return new ResponseEntity<>(new Response(true, "Thành công", p.get()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new Response(false, "Thất bại", null), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(path = "/addProduct", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addProduct(
            @RequestParam("productName") String productName,
            @RequestParam("quantity") int quantity,
            @RequestParam("unitPrice") double unitPrice,
            @RequestParam("description") String description,
            @RequestParam("discount") double discount,
            @RequestParam("status") short status,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam(value="images", required=false) MultipartFile images) {

        Optional<Category> cat = categoryRepository.findById(categoryId);
        if (cat.isEmpty()) {
            return ResponseEntity.badRequest().body(new Response(false, "categoryId không hợp lệ", null));
        }

        Product p = new Product();
        p.setProductName(productName);
        p.setQuantity(quantity);
        p.setUnitPrice(unitPrice);
        p.setDescription(description);
        p.setDiscount(discount);
        p.setStatus(status);
        p.setCategory(cat.get());
        p.setCreateDate(new Date());

        if (images != null && !images.isEmpty()) {
            String uu = UUID.randomUUID().toString();
            String filename = storageService.getSorageFilename(images, uu);
            storageService.store(images, filename);
            p.setImages(filename);
        }

        p = productService.save(p);
        return new ResponseEntity<>(new Response(true, "Thêm Thành công", p), HttpStatus.OK);
    }

    @PutMapping(path = "/updateProduct", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProduct(
            @RequestParam("productId") Long productId,
            @RequestParam("productName") String productName,
            @RequestParam("quantity") int quantity,
            @RequestParam("unitPrice") double unitPrice,
            @RequestParam("description") String description,
            @RequestParam("discount") double discount,
            @RequestParam("status") short status,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam(value="images", required=false) MultipartFile images) {

        Optional<Product> opt = productService.findById(productId);
        if (opt.isEmpty()) {
            return new ResponseEntity<>(new Response(false, "Không tìm thấy Product", null), HttpStatus.BAD_REQUEST);
        }
        Product p = opt.get();

        Optional<Category> cat = categoryRepository.findById(categoryId);
        if (cat.isEmpty()) {
            return ResponseEntity.badRequest().body(new Response(false, "categoryId không hợp lệ", null));
        }

        p.setProductName(productName);
        p.setQuantity(quantity);
        p.setUnitPrice(unitPrice);
        p.setDescription(description);
        p.setDiscount(discount);
        p.setStatus(status);
        p.setCategory(cat.get());

        if (images != null && !images.isEmpty()) {
            String uu = UUID.randomUUID().toString();
            String filename = storageService.getSorageFilename(images, uu);
            storageService.store(images, filename);
            p.setImages(filename);
        }

        p = productService.save(p);
        return new ResponseEntity<>(new Response(true, "Cập nhật Thành công", p), HttpStatus.OK);
    }

    @DeleteMapping(path = "/deleteProduct")
    public ResponseEntity<?> deleteProduct(@RequestParam("productId") Long productId) {
        Optional<Product> opt = productService.findById(productId);
        if (opt.isEmpty()) {
            return new ResponseEntity<>(new Response(false, "Không tìm thấy Product", null), HttpStatus.BAD_REQUEST);
        }
        productService.deleteById(productId);
        return new ResponseEntity<>(new Response(true, "Xóa Thành công", opt.get()), HttpStatus.OK);
    }
}
