package vn.iotstar.Repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;  

import org.springframework.data.jpa.repository.JpaRepository;

import vn.iotstar.Entities.*;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
    List<Product> findByNameContainingIgnoreCase(String keyword);
}