package ru.azikram0.flowerbackend.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontendController {

    @GetMapping({"/catalog", "/catalog/**"})
    public String index() {
        return "forward:/index.html";
    }

    @GetMapping("/")
    public String redirectToCatalog() {
        return "redirect:/catalog";
    }
}