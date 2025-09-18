package vn.iotstar.Service; // Sửa package

import vn.iotstar.Entities.Account;
import vn.iotstar.Repository.AccountRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {
    private final AccountRepository repo;

    public AccountService(AccountRepository repo) {
        this.repo = repo;
    }

    public Page<Account> findAll(Pageable pageable) { return repo.findAll(pageable); }
    public List<Account> findAll() { return repo.findAll(); }
    public Account save(Account a) { return repo.save(a); }
    public void delete(Long id) { repo.deleteById(id); }
    public Account get(Long id) { return repo.findById(id).orElseThrow(); }
    public List<Account> searchNoPaging(String keyword) { return repo.findByUsernameContainingIgnoreCase(keyword); }
    public Page<Account> searchPaging(String keyword, Pageable pageable) {
        return (keyword == null || keyword.isEmpty()) ?
                repo.findAll(pageable) :
                repo.findByUsernameContainingIgnoreCase(keyword, pageable);
    }
    public Account findByUsernameAndPassword(String u, String p) {
        return repo.findByUsernameAndPassword(u, p);
    }
}