package vn.iotstar.service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import vn.iotstar.entity.Product;
import vn.iotstar.repository.ProductRepository;

@Service
public class ProductServiceImpl implements IProductService {
    private final ProductRepository repo;
    public ProductServiceImpl(ProductRepository repo){ this.repo = repo; }

    @Override public Product save(Product entity){ return repo.save(entity); }
    @Override public Optional<Product> findById(Long id){ return repo.findById(id); }
    @Override public void deleteById(Long id){ repo.deleteById(id); }
    @Override public Page<Product> findAll(Pageable pageable){ return repo.findAll(pageable); }
    @Override public Page<Product> findByProductNameContaining(String name, Pageable pageable){
        return repo.findByProductNameContaining(name, pageable);
    }
    @Override public List<Product> findByProductNameContaining(String name){ return repo.findByProductNameContaining(name); }
}
