package com.buy01.product.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.buy01.product.service.ProductEventPublisher;
import com.buy01.product.dto.MediaInfo;
import com.buy01.product.dto.MyProductInfo;
import com.buy01.product.dto.ProductDeleteEvent;
import com.buy01.product.dto.ProductInfo;
import com.buy01.product.dto.ProductRequest;
import com.buy01.product.dto.SellerInfo;
import com.buy01.product.media.MediaClient;
import com.buy01.product.model.Product;
import com.buy01.product.service.ProductService;
import com.buy01.product.user.UserClient;

import feign.FeignException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final UserClient userClient;
    private final MediaClient mediaClient;
    private final ProductEventPublisher productEventPublisher;

    @GetMapping
    public ResponseEntity<?> getAllProducts() {
        var products = productService.findAllProducts();
        List<ProductInfo> productInfos = new ArrayList<>();

        List<SellerInfo> sellers = new ArrayList<>();
        List<MediaInfo> tmpMedias;

        // 🔹 Try to fetch all sellers from user-service
        try {
            sellers = userClient.getAllUserByRole("SELLER");
        } catch (FeignException e) {
            System.err.println("⚠️ user-service is unreachable or returned an error: " + e.getMessage());
        }

        // 🔹 Try to fetch all medias from media-service
        try {
            tmpMedias = mediaClient.getAllMedia();
        } catch (FeignException e) {
            System.err.println("⚠️ media-service is unreachable or returned an error: " + e.getMessage());
            tmpMedias = List.of(); // Default to an empty list to avoid crashes
        }

        // 🔹 If no sellers found, return a message
        if (sellers.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No sellers found — returning products without seller association");
        }

        final List<MediaInfo> medias = tmpMedias; // Must be final to use inside lambdas

        // 🔹 Build the combined product info list
        sellers.forEach(seller -> {
            var sellerProducts = products.stream()
                    .filter(p -> p.getUserId().equals(seller.getId()))
                    .toList();

            sellerProducts.forEach(product -> {
                var sellerMedia = medias.stream()
                        .filter(m -> m.getProductId().equals(product.getId()))
                        .toList();

                productInfos.add(new ProductInfo(product, seller, sellerMedia));
            });
        });

        return ResponseEntity.ok(productInfos);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<?> getProductsById(@PathVariable String productId) {
        Optional<Product> myProducts = productService.findProductById(productId);
        if (myProducts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product Not Found");
        }

        return ResponseEntity.ok().body(myProducts.get());
    }

    @GetMapping("/all/{userId}")
    @PreAuthorize("isAuthenticated() and @productSecurity.isOwner(#userId) and hasAnyRole('ADMIN','SELLER')")
    public ResponseEntity<List<MyProductInfo>> getAllProductsByUserId(@PathVariable String userId) {
        List<MediaInfo> tmpMedias;
        List<Product> myProducts = productService.findAllProductsByUserId(userId);
        List<MyProductInfo> finalInfos = new ArrayList<>();

        // 🔹 Try to fetch all medias from media-service
        try {
            tmpMedias = mediaClient.getAllMedia();
        } catch (FeignException e) {
            System.err.println("⚠️ media-service is unreachable or returned an error: " + e.getMessage());
            tmpMedias = List.of(); // Default to an empty list to avoid crashes
        }

        final List<MediaInfo> medias = tmpMedias;

        myProducts.forEach(p -> {
            var sellerMedias = medias.stream()
                    .filter(m -> m.getProductId().equals(p.getId()))
                    .toList();
            MyProductInfo myProductInfo = new MyProductInfo();
            myProductInfo.setId(p.getId());
            myProductInfo.setName(p.getName());
            myProductInfo.setDescription(p.getDescription());
            myProductInfo.setPrice(p.getPrice());
            myProductInfo.setQuantity(p.getQuantity());
            myProductInfo.setUserId(p.getUserId());
            myProductInfo.setMedias(sellerMedias);

            finalInfos.add(myProductInfo);

        });

        return ResponseEntity.ok().body(finalInfos);
    }

    @PostMapping("/{userId}")
    @PreAuthorize("isAuthenticated() and @productSecurity.isOwner(#userId) and hasAnyRole('ADMIN','SELLER')")
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductRequest product, @PathVariable String userId) {

        Product newProduct = new Product();
        newProduct.setName(product.getName());
        newProduct.setDescription(product.getDescription());
        newProduct.setPrice(product.getPrice());
        newProduct.setQuantity(product.getQuantity());
        newProduct.setUserId(userId);
        Product createProduct = productService.createProduct(newProduct);
        return ResponseEntity.ok().body(createProduct);
    }

    @PutMapping("/{productId}")
    @PreAuthorize("isAuthenticated() and @productSecurity.isOwnerProduct(#productId) and hasAnyRole('ADMIN','SELLER')")
    public ResponseEntity<?> updateProduct(@PathVariable String productId, @Valid @RequestBody ProductRequest product) {
        if (!productService.existsByProductId(productId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product Not Found");
        }

        Product productToUpdate = productService.findProductById(productId).get();

        productToUpdate.setName(product.getName());
        productToUpdate.setDescription(product.getDescription());
        productToUpdate.setPrice(product.getPrice());
        productToUpdate.setQuantity(product.getQuantity());

        Product updatedProduct = productService.createProduct(productToUpdate);

        return ResponseEntity.ok().body(updatedProduct);
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("isAuthenticated() and @productSecurity.isOwnerProduct(#productId) and hasAnyRole('ADMIN','SELLER')")
    public ResponseEntity<?> deleteProduct(@PathVariable String productId) {
        if (!productService.existsByProductId(productId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product Not Found");
        }
        productService.deleteProduct(productId);

        ProductDeleteEvent productDeleteEvent = new ProductDeleteEvent(productId, "PRODUCT_DELETED");
        productEventPublisher.publishProductDeleted(productDeleteEvent);
        return ResponseEntity.noContent().build();
    }
}
