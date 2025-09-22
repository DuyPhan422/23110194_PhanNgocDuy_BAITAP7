package vn.iotstar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import vn.iotstar.entity.User;
import vn.iotstar.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

	@Autowired
	private UserRepository userRepository;

	@GetMapping("/login")
	public String showLoginForm(HttpSession session) {
		if (session.getAttribute("currentUser") != null) {
			return "redirect:/home";
		}
		return "login";
	}

	@PostMapping("/login")
	public String login(@RequestParam String username, @RequestParam String password, HttpSession session,
			Model model) {

		User user = userRepository.findByUsername(username);

		if (user != null && password.equals(user.getPassword())) {
			session.setAttribute("currentUser", user);
			
			return "redirect:/home";
		} else {
			model.addAttribute("error", "Invalid username or password.");
			return "login";
		}
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/login";
	}
}
