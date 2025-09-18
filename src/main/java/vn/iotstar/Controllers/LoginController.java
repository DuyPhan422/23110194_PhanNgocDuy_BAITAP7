package vn.iotstar.Controllers;

import jakarta.servlet.http.HttpSession;
import vn.iotstar.Entities.Account;
import vn.iotstar.Service.AccountService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {
    private final AccountService accountService;

    public LoginController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("account", new Account());
        return "web/login"; // 
    }

    @PostMapping("/login")
    public String login(@ModelAttribute Account account,
                        HttpSession session,
                        Model model) {
        Account acc = accountService.findByUsernameAndPassword(account.getUsername(), account.getPassword());
        if (acc != null) {
            session.setAttribute("account", acc);

            // 👉 Sau khi login thì đưa tất cả về trang Home
            return "redirect:/";
        } else {
            model.addAttribute("error", "Invalid username or password!");
            return "web/login";
        }
    }


    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}