package com.example.product_management.controller;

import com.example.product_management.entity.Product;
import com.example.product_management.repository.ProductRepository;
import com.example.product_management.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private ProductRepository productRepository;

    @Autowired
    public ProductController(ProductService productService, ProductRepository productRepo) {
        this.productService = productService;
        this.productRepository = productRepo;
    }

    // List all products
    @GetMapping
    public String listProducts(
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            Model model) {

        List<Product> products;
        Sort sort = null;

        if (sortBy != null) {
            sort = sortDir.equals("asc") ?
                    Sort.by(sortBy).ascending() :
                    Sort.by(sortBy).descending();
        }

        // If we have search parameters, use them
        if ((keyword != null && !keyword.isEmpty()) || (category != null && !category.isEmpty())) {
            products = productService.searchProductsWithFilters(keyword, category, sort);
        } else if (sort != null) {
            products = productService.getAllProducts(sort);
        } else {
            products = productService.getAllProducts();
        }

        // Get all categories for the dropdown
        List<String> categories = productRepository.findAllCategories();

        model.addAttribute("products", products);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("keyword", keyword);
        model.addAttribute("categories", categories);
        model.addAttribute("selectedCategory", category);

        return "product-list";
    }

    // Show form for new product
    @GetMapping("/new")
    public String showNewForm(Model model) {
        Product product = new Product();
        model.addAttribute("product", product);
        return "product-form";
    }

    // Show form for editing product
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return productService.getProductById(id)
                .map(product -> {
                    model.addAttribute("product", product);
                    return "product-form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Product not found");
                    return "redirect:/products";
                });
    }

    // Save product (create or update)
    @PostMapping("/save")
    public String saveProduct(
            @Valid @ModelAttribute("product") Product product,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "product-form";
        }

        try {
            productService.saveProduct(product);
            redirectAttributes.addFlashAttribute("message", "Product saved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }

        return "redirect:/products";
    }

    // Delete product
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("message", "Product deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting product: " + e.getMessage());
        }
        return "redirect:/products";
    }

    // This endpoint has been combined with the main listing endpoint
    // to provide a unified interface for filtering and sorting


    @GetMapping("/advanced-search")
    public String advancedSearch(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            Model model) {
        List<Product> products = productRepository.searchProducts(name, category, minPrice, maxPrice);
        model.addAttribute("products", products);
        return "product-list";
    }

}
