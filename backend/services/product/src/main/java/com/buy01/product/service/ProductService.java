package com.buy01.product.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import com.buy01.product.repository.ProductRepository;
import com.buy01.product.model.Product;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> findAllProductsByUserId(String userId) {
        return productRepository.findAllByUserId(userId);
    }

    public Optional<Product> findProductById(String id) {
        return Optional.ofNullable(productRepository.findById(id).orElse(null));
    }

    public Product findProductByUserId(String userId) {
        return productRepository.findByUserId(userId).orElse(null);
    }

    public void deleteProduct(String id) {
        productRepository.deleteById(id);
    }

    public Boolean existsByProductId(String productId) {
        return productRepository.findById(productId).isPresent();
    }
}
