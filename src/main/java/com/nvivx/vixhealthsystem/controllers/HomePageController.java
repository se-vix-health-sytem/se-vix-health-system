package com.nvivx.vixhealthsystem.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomePageController {
    @GetMapping("/")
    public String home(Model model) {
        return "index";   // resolves to templates/index.html
    }
}
