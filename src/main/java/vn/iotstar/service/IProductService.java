package vn.iotstar.service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.*;
import vn.iotstar.entity.Product;

public interface IProductService {
    Product save(Product entity);
    Optional<Product> findById(Long id);
    void deleteById(Long id);
    Page<Product> findAll(Pageable pageable);
    Page<Product> findByProductNameContaining(String name, Pageable pageable);
    List<Product> findByProductNameContaining(String name);
}
