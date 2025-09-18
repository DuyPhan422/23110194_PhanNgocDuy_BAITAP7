package vn.iotstar.Controllers;

import jakarta.servlet.http.HttpSession;
import vn.iotstar.Entities.Account;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // Trang chủ
	@GetMapping("/")
	public String home(HttpSession session, Model model) {
	    Account acc = (Account) session.getAttribute("account");
	    if (acc == null) {
	        // Nếu chưa đăng nhập thì về login
	        return "redirect:/login";
	    }
	    model.addAttribute("account", acc);
	    return "web/index";
	}
}
