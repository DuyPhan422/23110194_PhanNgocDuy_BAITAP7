package vn.iotstar.baitapcrud.repository;

import vn.iotstar.baitapcrud.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Tìm kiếm theo tên (không phân biệt chữ hoa/thường)
    @Query("SELECT c FROM Category c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Category> findByNameContainingIgnoreCase(@Param("keyword") String keyword);

    // Thêm phương thức mới để hỗ trợ phân trang
    Page<Category> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
}