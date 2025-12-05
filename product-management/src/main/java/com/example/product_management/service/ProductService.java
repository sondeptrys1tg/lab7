package com.example.product_management.service;

import com.example.product_management.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ProductService {

    List<Product> getAllProducts(Sort sort);

    List<Product> getAllProducts();

    Optional<Product> getProductById(Long id);

    Product saveProduct(Product product);

    void deleteProduct(Long id);

    List<Product> searchProducts(String keyword);

    Page<Product> searchProducts(String keyword, Pageable pageable);

    List<Product> getProductsByCategory(String category);

    List<Product> searchProductsWithFilters(String keyword, String category, Sort sort);

    // Dashboard statistics methods
    long getTotalProductCount();

    List<String> getAllCategories();

    Map<String, Long> getProductCountByCategory();

    BigDecimal getTotalInventoryValue();

    BigDecimal getAverageProductPrice();

    List<Product> getLowStockProducts(int threshold);

    List<Product> getRecentProducts(int count);
}
