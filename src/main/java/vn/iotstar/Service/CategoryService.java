package vn.iotstar.Service;

import vn.iotstar.Entities.Category;
import vn.iotstar.Repository.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository repo;

    public CategoryService(CategoryRepository repo) {
        this.repo = repo;
    }

    public Page<Category> findAll(Pageable pageable) { return repo.findAll(pageable); }
    public List<Category> findAll() { return repo.findAll(); }
    public Category save(Category c) { return repo.save(c); }
    public void delete(Long id) { repo.deleteById(id); }
    public Category get(Long id) { return repo.findById(id).orElseThrow(); }
    public List<Category> searchNoPaging(String keyword) { return repo.findByNameContainingIgnoreCase(keyword); }
    public Page<Category> searchPaging(String keyword, Pageable pageable) {
        return (keyword == null || keyword.isEmpty()) ?
                repo.findAll(pageable) :
                repo.findByNameContainingIgnoreCase(keyword, pageable);
    }
}
