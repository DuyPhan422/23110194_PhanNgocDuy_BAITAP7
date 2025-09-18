package vn.iotstar.Repository;

import vn.iotstar.Entities.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Page<Account> findByUsernameContainingIgnoreCase(String keyword, Pageable pageable);
    List<Account> findByUsernameContainingIgnoreCase(String keyword);
    Account findByUsernameAndPassword(String username, String password);
}
