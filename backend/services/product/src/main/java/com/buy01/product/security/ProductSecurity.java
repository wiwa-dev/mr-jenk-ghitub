package com.buy01.product.security;

import org.springframework.stereotype.Component;

import com.buy01.product.service.ProductService;

import org.springframework.security.core.context.SecurityContextHolder;

import lombok.RequiredArgsConstructor;

@Component("productSecurity")
@RequiredArgsConstructor
public class ProductSecurity {
    private final ProductService productService;

    public Boolean isOwner(String userId) {
        return userId.equals(SecurityContextHolder.getContext().getAuthentication().getCredentials());
    }

    public Boolean isOwnerProduct(String productId) {
        Object currentUserId = SecurityContextHolder.getContext().getAuthentication().getCredentials();
        String userId = productService.findProductById(productId).get().getUserId();
        return userId.equals(currentUserId);
    }

}
