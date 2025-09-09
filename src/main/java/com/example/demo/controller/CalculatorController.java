package com.example.demo.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CalculatorController {

    @Value("${app.version}")
    private String version;

    @GetMapping("/calculate")
    public String showCalculator(Model model, HttpSession session) {
        session.setAttribute("persistenceOnPost", version);
        model.addAttribute("expression", "");
        model.addAttribute("persistenceOnPost", version);
        model.addAttribute("theme", version.equals("v2") ? "dark" : "light");
        return "calculator";
    }

    @PostMapping("/calculate")
    public String calculate(@RequestParam String expression, HttpSession session, Model model) {
        String persistenceOnPost = (String) session.getAttribute("persistenceOnPost");

        model.addAttribute("expression", expression);
        model.addAttribute("persistenceOnPost", persistenceOnPost);
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
