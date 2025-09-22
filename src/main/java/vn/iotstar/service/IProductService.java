package vn.iotstar.service;

import vn.iotstar.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IProductService {
    Product save(Product product);
    List<Product> findAll();
    Page<Product> findAll(Pageable pageable);
    Optional<Product> findById(Long id);
    void deleteById(Long id);
    Page<Product> findByProductNameContaining(String name, Pageable p);
}
