package com.example.demo.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CalculatorController {

    @Value("${app.version}")
    private String version;

    @GetMapping("/calculate")
    public String showCalculator(Model model, HttpServletResponse response) {
        // Set a cookie with the instance/version that served the GET
        ResponseCookie cookie = ResponseCookie.from("persistenceOnPost", version)
                .path("/")
                .httpOnly(true)   // not visible to JS
                .secure(true)     // set true if you use HTTPS
                .sameSite("Lax")
                .build();
        response.addHeader("Set-Cookie", cookie.toString());

        model.addAttribute("expression", "");
        model.addAttribute("persistenceOnPost", version);
        model.addAttribute("theme", "v2".equals(version) ? "dark" : "light");
        return "calculator";
    }

    @PostMapping("/calculate")
    public String calculate(@RequestParam String expression,
                            @CookieValue(value = "persistenceOnPost", required = false) String persistenceOnPost,
                            Model model) {

        // Fallback in case the cookie is missing for any reason
        if (persistenceOnPost == null) {
            persistenceOnPost = version;
        }

        model.addAttribute("expression", expression);
        model.addAttribute("persistenceOnPost", persistenceOnPost);

        // IMPORTANT: decide theme from the *carried* version, not the current node's version
        model.addAttribute("theme", "v2".equals(persistenceOnPost) ? "dark" : "light");

        try {
            double result = evaluate(expression);
            model.addAttribute("result", result);
        } catch (Exception e) {
            model.addAttribute("result", "Error");
        }
        return "calculator";
    }


    private double evaluate(String expr) {
        expr = expr.replaceAll("\\s+", ""); // remove all spaces
        String[] tokens = expr.split("(?<=[0-9])(?=[+\\-*/])|(?<=[+\\-*/])(?=[0-9])");

        if (tokens.length != 3) throw new IllegalArgumentException("Invalid expression");

        double num1 = Double.parseDouble(tokens[0]);
        String op = tokens[1];
        double num2 = Double.parseDouble(tokens[2]);

        return switch (op) {
            case "+" -> num1 + num2;
            case "-" -> num1 - num2;
            case "*" -> num1 * num2;
            case "/" -> num2 != 0 ? num1 / num2 : Double.NaN;
            default -> throw new IllegalArgumentException("Unknown operator");
        };
    }

}
