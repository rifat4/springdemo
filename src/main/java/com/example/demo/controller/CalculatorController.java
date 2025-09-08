package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CalculatorController {

    @GetMapping("/calculate")
    public String showCalculator(Model model) {
        model.addAttribute("expression", "");
        return "calculator";
    }

    @PostMapping("/calculate")
    public String calculate(@RequestParam String expression, Model model) {
        model.addAttribute("expression", expression);
        try {
            double result = evaluate(expression);
            model.addAttribute("result", result);
        } catch (Exception e) {
            model.addAttribute("result", "Error");
        }
        return "calculator";
    }

    private double evaluate(String expr) {
        String[] tokens = expr.trim().split(" ");
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
