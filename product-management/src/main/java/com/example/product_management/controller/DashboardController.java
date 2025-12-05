package com.example.product_management.controller;

import com.example.product_management.entity.Product;
import com.example.product_management.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public String showDashboard(Model model) {
        // Add statistics to model

        // Total products count
        long totalProducts = productService.getTotalProductCount();
        model.addAttribute("totalProducts", totalProducts);

        // Products by category (for pie chart or list)
        Map<String, Long> productsByCategory = productService.getProductCountByCategory();
        model.addAttribute("productsByCategory", productsByCategory);

        // Total inventory value
        BigDecimal totalInventoryValue = productService.getTotalInventoryValue();
        model.addAttribute("totalInventoryValue", totalInventoryValue);

        // Average product price
        BigDecimal averagePrice = productService.getAverageProductPrice();
        model.addAttribute("averagePrice", averagePrice);

        // Low stock alerts (quantity < 10)
        List<Product> lowStockProducts = productService.getLowStockProducts(10);
        model.addAttribute("lowStockProducts", lowStockProducts);

        // Recent products (last 5 added)
        List<Product> recentProducts = productService.getRecentProducts(5);
        model.addAttribute("recentProducts", recentProducts);

        return "dashboard";
    }
}
