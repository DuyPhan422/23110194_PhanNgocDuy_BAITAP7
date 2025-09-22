package vn.iotstar.controller;

import vn.iotstar.entity.Category;
import vn.iotstar.entity.User;
import vn.iotstar.service.CategoryService;
import vn.iotstar.service.FileStorageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private FileStorageService fileStorageService;

    private boolean isUserAuthorized(HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        return currentUser != null;
    }

    private void addUserRolesToModel(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
            boolean isAdminOrManager = currentUser.getRole() == User.Role.admin || currentUser.getRole() == User.Role.manager;
            model.addAttribute("isAdminOrManager", isAdminOrManager);
        }
    }

    @GetMapping
    public String listCategories(Model model, HttpSession session) {
        if (!isUserAuthorized(session)) {
            return "redirect:/login";
        }
        model.addAttribute("categories", categoryService.findAll());
        addUserRolesToModel(session, model);
        return "category/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model, HttpSession session) {
        if (!isUserAuthorized(session)) {
            return "redirect:/login";
        }
        model.addAttribute("category", new Category());
        addUserRolesToModel(session, model);
        return "category/add";
    }

    @PostMapping("/add")
    public String addCategory(@ModelAttribute("category") Category category, @RequestParam("imageFile") MultipartFile imageFile, HttpSession session) {
        if (!isUserAuthorized(session)) {
            return "redirect:/login";
        }
        if (!imageFile.isEmpty()) {
            String fileName = fileStorageService.storeFile(imageFile);
            category.setImage(fileName);
        }
        categoryService.save(category);
        return "redirect:/category";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model, HttpSession session) {
        if (!isUserAuthorized(session)) {
            return "redirect:/login";
        }
        model.addAttribute("category", categoryService.findById(id));
        addUserRolesToModel(session, model);
        return "category/edit";
    }

    @PostMapping("/edit")
    public String editCategory(@ModelAttribute("category") Category category, @RequestParam("imageFile") MultipartFile imageFile, HttpSession session) {
        if (!isUserAuthorized(session)) {
            return "redirect:/login";
        }
        if (!imageFile.isEmpty()) {
            String fileName = fileStorageService.storeFile(imageFile);
            category.setImage(fileName);
        }
        categoryService.save(category);
        return "redirect:/category";
    }

    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable("id") Integer id, HttpSession session) {
        if (!isUserAuthorized(session)) {
            return "redirect:/login";
        }
        categoryService.deleteById(id);
        return "redirect:/category";
    }

    @GetMapping("/images/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = fileStorageService.loadFileAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @GetMapping("/search")
    public String searchCategory(@RequestParam("keyword") String keyword, Model model, HttpSession session) {
        if (!isUserAuthorized(session)) {
            return "redirect:/login";
        }
        model.addAttribute("categories", categoryService.search(keyword));
        addUserRolesToModel(session, model);
        return "category/list";
    }
}
