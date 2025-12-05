package com.example.product_management.service;

import com.example.product_management.entity.Product;
import com.example.product_management.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> getAllProducts(Sort sort) {
        return productRepository.findAll(sort);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public Product saveProduct(Product product) {
        // Validation logic can go here
        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public List<Product> searchProducts(String keyword) {
        Pageable pageable = PageRequest.of(0, 100); // Default to first 100 results
        return productRepository.findByNameContaining(keyword, pageable).getContent();
    }

    @Override
    public Page<Product> searchProducts(String keyword, Pageable pageable) {
        return productRepository.findByNameContaining(keyword, pageable);
    }

    @Override
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    @Override
    public List<Product> searchProductsWithFilters(String keyword, String category, Sort sort) {
        // Create a specification for dynamic filtering
        if (keyword == null) keyword = "";

        // If we have a category filter
        if (category != null && !category.isEmpty()) {
            // If we have a sort parameter
            if (sort != null) {
                // Search by both keyword and category with sorting
                return productRepository.findByCategoryAndNameContainingIgnoreCase(category, keyword, sort);
            } else {
                // Search by both keyword and category without sorting
                return productRepository.findByCategoryAndNameContainingIgnoreCase(category, keyword);
            }
        } else {
            // If we have a sort parameter
            if (sort != null) {
                // Search by keyword only with sorting
                return productRepository.findByNameContainingIgnoreCase(keyword, sort);
            } else {
                // Search by keyword only without sorting
                return productRepository.findByNameContainingIgnoreCase(keyword);
            }
        }
    }

    // Dashboard statistics methods implementation

    @Override
    public long getTotalProductCount() {
        return productRepository.count();
    }

    @Override
    public List<String> getAllCategories() {
        return productRepository.findAllCategories();
    }

    @Override
    public Map<String, Long> getProductCountByCategory() {
        List<String> categories = getAllCategories();
        Map<String, Long> categoryCounts = new HashMap<>();

        for (String category : categories) {
            long count = productRepository.countByCategory(category);
            categoryCounts.put(category, count);
        }

        return categoryCounts;
    }

    @Override
    public BigDecimal getTotalInventoryValue() {
        return productRepository.calculateTotalValue();
    }

    @Override
    public BigDecimal getAverageProductPrice() {
        return productRepository.calculateAveragePrice();
    }

    @Override
    public List<Product> getLowStockProducts(int threshold) {
        return productRepository.findLowStockProducts(threshold);
    }

    @Override
    public List<Product> getRecentProducts(int count) {
        // Get all products sorted by createdAt in descending order and limit to count
        Pageable pageable = PageRequest.of(0, count, Sort.by("createdAt").descending());
        return productRepository.findAll(pageable).getContent();
    }
}
