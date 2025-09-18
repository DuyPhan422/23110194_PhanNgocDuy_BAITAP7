package vn.iotstar.Controllers;

import vn.iotstar.Entities.Account;
import vn.iotstar.Service.AccountService;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/accounts")
public class AdminAccountController {
    private final AccountService service;

    public AdminAccountController(AccountService service) {
        this.service = service;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "5") int size,
                       Model model) {
        Pageable pageable = PageRequest.of(page, size);
        model.addAttribute("accounts", service.findAll(pageable));
        return "admin/accounts/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("account", new Account());
        return "admin/accounts/addOrEdit";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Account account) {
        service.save(account);
        return "redirect:/admin/accounts";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("account", service.get(id));
        return "admin/accounts/addOrEdit";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("account", service.get(id));
        return "admin/accounts/detail";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/admin/accounts";
    }
    @GetMapping("/search")
    public String search(@RequestParam(required = false) String keyword, Model model) {
        List<Account> results;
        if (keyword != null && !keyword.isEmpty()) {
            results = service.searchNoPaging(keyword); // tự viết query tìm theo username
        } else {
            results = service.findAll();
        }

        model.addAttribute("results", results);
        model.addAttribute("keyword", keyword);

        return "admin/accounts/search";
    }

    // Search paginated
    @GetMapping("/searchpaginated")
    public String searchPaginated(@RequestParam(required = false) String keyword,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "5") int size,
                                  Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Account> result = service.searchPaging(keyword, pageable);

        model.addAttribute("page", result);
        model.addAttribute("keyword", keyword);

        return "admin/accounts/searchpaginated";
    }
}