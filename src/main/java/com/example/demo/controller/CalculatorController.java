package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CalculatorController {

    @Value("${app.version}")
    private String version;

    private String persistanceOnPost = null;

    @GetMapping("/calculate")
    public String showCalculator(Model model) {
        model.addAttribute("expression", "");
        persistanceOnPost = version;
        model.addAttribute("persistanceOnPost", persistanceOnPost);
        model.addAttribute("theme", version.equals(persistanceOnPost) ? "dark" : "light");
        return "calculator";
    }

    @PostMapping("/calculate")
    public String calculate(@RequestParam String expression, Model model) {
        model.addAttribute("expression", expression);
        model.addAttribute("persistanceOnPost", persistanceOnPost);
        if(persistanceOnPost == null){
            persistanceOnPost = version;
        }
        if(persistanceOnPost!= null) {
            model.addAttribute("theme", version.equals(persistanceOnPost) ? "dark" : "light");
        } else {
            model.addAttribute("theme", version.equals("v2") ? "dark" : "light");
        }
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
