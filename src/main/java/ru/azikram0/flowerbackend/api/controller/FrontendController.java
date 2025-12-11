package ru.azikram0.flowerbackend.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontendController {

    /**
     * Этот метод отдаёт SPA (index.html) для каталога.
     */
    @GetMapping({"/catalog", "/catalog/**", "/flower/**"})
    public String index() {
        return "forward:/index.html";
    }

    /**
     * Редирект с корня на каталог
     */
    @GetMapping("/")
    public String redirectToCatalog() {
        return "redirect:/catalog";
    }
}