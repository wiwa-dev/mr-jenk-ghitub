package com.buy01.media.product;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.buy01.media.dto.ProductInfo;

@FeignClient(name = "product-service", url = "${application.config.product-url}")
public interface ProductClient {
    
    @GetMapping("/{productId}")
    ProductInfo getProductById(@PathVariable String productId);
}
