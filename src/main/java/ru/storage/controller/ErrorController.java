package ru.storage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/error")
public class ErrorController {
    @GetMapping("/403")
    public String showAccessDeniedPage(Model model) {
        model.addAttribute("errorMessage", "Error 403! Access denied");
        return "error_page";
    }
}
