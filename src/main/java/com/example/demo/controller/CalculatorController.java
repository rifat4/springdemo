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

    @GetMapping("/calculator")
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

    @PostMapping("/calculator")
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
        expr = expr.replaceAll("\\s+", ""); // remove whitespace
        return evalInfix(expr);
    }

    private double evalInfix(String expr) {
        // Convert infix (e.g. "2+3*4") to postfix (RPN: "2 3 4 * +") and evaluate
        java.util.Stack<Double> values = new java.util.Stack<>();
        java.util.Stack<Character> ops = new java.util.Stack<>();

        for (int i = 0; i < expr.length();) {
            char c = expr.charAt(i);

            if (Character.isDigit(c) || c == '.') {
                // Parse number (supports decimals)
                int j = i;
                while (j < expr.length() && 
                    (Character.isDigit(expr.charAt(j)) || expr.charAt(j) == '.')) {
                    j++;
                }
                double val = Double.parseDouble(expr.substring(i, j));
                values.push(val);
                i = j;
            } else if (c == '(') {
                ops.push(c);
                i++;
            } else if (c == ')') {
                while (!ops.isEmpty() && ops.peek() != '(') {
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                }
                if (!ops.isEmpty() && ops.peek() == '(') {
                    ops.pop(); // discard '('
                }
                i++;
            } else if ("+-*/".indexOf(c) != -1) {
                // Handle unary minus (negative numbers)
                if ((c == '-' && (i == 0 || expr.charAt(i - 1) == '(' || "+-*/".indexOf(expr.charAt(i - 1)) != -1))) {
                    // Treat as part of the number
                    int j = i + 1;
                    while (j < expr.length() && 
                        (Character.isDigit(expr.charAt(j)) || expr.charAt(j) == '.')) {
                        j++;
                    }
                    double val = Double.parseDouble(expr.substring(i, j));
                    values.push(val);
                    i = j;
                    continue;
                }

                while (!ops.isEmpty() && precedence(ops.peek()) >= precedence(c)) {
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                }
                ops.push(c);
                i++;
            } else {
                throw new IllegalArgumentException("Invalid character: " + c);
            }
        }

        while (!ops.isEmpty()) {
            values.push(applyOp(ops.pop(), values.pop(), values.pop()));
        }

        if (values.size() != 1) throw new IllegalArgumentException("Invalid expression");
        return values.pop();
    }

    private int precedence(char op) {
        return switch (op) {
            case '+', '-' -> 1;
            case '*', '/' -> 2;
            default -> -1;
        };
    }

    private double applyOp(char op, double b, double a) {
        return switch (op) {
            case '+' -> a + b;
            case '-' -> a - b;
            case '*' -> a * b;
            case '/' -> {
                if (b == 0) throw new ArithmeticException("Division by zero");
                yield a / b;
            }
            default -> throw new IllegalArgumentException("Unknown operator: " + op);
        };
    }


}