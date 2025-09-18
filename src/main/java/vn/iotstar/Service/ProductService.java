package vn.iotstar.Service;


import vn.iotstar.Entities.Product;
import vn.iotstar.Repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository repo;

    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    public List<Product> findAll() { return repo.findAll(); }
    public Page<Product> findAll(Pageable pageable) { return repo.findAll(pageable); }
    public Product save(Product p) { return repo.save(p); }
    public void delete(Long id) { repo.deleteById(id); }
    public Product get(Long id) { return repo.findById(id).orElseThrow(); }
    public List<Product> searchNoPaging(String keyword) { return repo.findByNameContainingIgnoreCase(keyword); }
    public Page<Product> searchPaging(String keyword, Pageable pageable) {
        return (keyword == null || keyword.isEmpty()) ?
                repo.findAll(pageable) :
                repo.findByNameContainingIgnoreCase(keyword, pageable);
    }
}
