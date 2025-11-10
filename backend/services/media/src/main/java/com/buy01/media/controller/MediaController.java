package com.buy01.media.controller;

import java.util.List;

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

import com.buy01.media.cloudinary.CloudinaryService;
import com.buy01.media.dto.MediaRequest;
import com.buy01.media.dto.ProductInfo;
import com.buy01.media.model.Media;
import com.buy01.media.product.ProductClient;
import com.buy01.media.service.MediaService;

import feign.FeignException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/medias")
@RequiredArgsConstructor
public class MediaController {
    private final MediaService mediaService;
    private final ProductClient productClient;
    private final CloudinaryService cloudinaryService;

    @GetMapping
    public ResponseEntity<?> getAllMedias() {
        List<Media> allMedia = mediaService.findAllMedias();
        return ResponseEntity.ok().body(allMedia);
    }

    @GetMapping("/{productId}")
    @PreAuthorize("isAuthenticated() and @mediaSecurity.isOwnerProduct(#productId) and hasAnyRole('ADMIN','SELLER')")
    public ResponseEntity<List<Media>> getAllMediasByProductId(@PathVariable String productId) {
        List<Media> myMedia = mediaService.findAllMediasByProductId(productId);
        return ResponseEntity.ok().body(myMedia);
    }

    @PostMapping("/{productId}")
    @PreAuthorize("isAuthenticated() and @mediaSecurity.isOwnerProduct(#productId) and hasAnyRole('ADMIN','SELLER')")
    public ResponseEntity<?> createMedia(@Valid @RequestBody MediaRequest media, @PathVariable String productId) {
        try {
            var maxSize = 2 * 1024 * 1024;
            var allowType = List.of("image/jpeg", "image/png", "image/gif", "image/webp");
            if (media.getFileSize() > maxSize) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File size must be less than 2MB");
            }

            if (!allowType.contains(media.getMimeType())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Only image files are allowed (JPEG, PNG, GIF, WebP)");
            }
            ProductInfo product = productClient.getProductById(productId);

            if (product == null || product.getId() == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
            }

            Media newMedia = new Media();
            newMedia.setImagePath(media.getImagePath());
            newMedia.setFileName(media.getFileName());
            newMedia.setCloudId(media.getCloudId());
            newMedia.setFileSize(media.getFileSize());
            newMedia.setMimeType(media.getMimeType());
            newMedia.setUploadDate(media.getUploadDate());
            newMedia.setProductId(productId);
            Media createMedia = mediaService.createMedia(newMedia);
            return ResponseEntity.ok().body(createMedia);

        } catch (FeignException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");

        }
    }

    @PutMapping("/{mediaId}")
    @PreAuthorize("isAuthenticated() and @mediaSecurity.isOwnerMedia(#mediaId) and hasAnyRole('ADMIN','SELLER')")
    public ResponseEntity<?> updateProduct(@PathVariable String mediaId, @Valid @RequestBody MediaRequest media) {
        if (!mediaService.existsByMediaId(mediaId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product Not Found");
        }

        Media mediaToUpdate = mediaService.findMediaById(mediaId).get();

        mediaToUpdate.setImagePath(media.getImagePath());
        mediaToUpdate.setFileName(media.getFileName());
        mediaToUpdate.setFileSize(media.getFileSize());
        mediaToUpdate.setFileSize(media.getFileSize());
        Media updatedMedia = mediaService.createMedia(mediaToUpdate);

        return ResponseEntity.ok().body(updatedMedia);
    }

    @DeleteMapping("/{mediaId}/{cloudId}")
    @PreAuthorize("isAuthenticated() and @mediaSecurity.isOwnerMedia(#mediaId) and hasAnyRole('ADMIN','SELLER')")
    public ResponseEntity<?> deleteMedia(@PathVariable String mediaId, @PathVariable String cloudId) {
        if (!mediaService.existsByMediaId(mediaId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("media Not Found");
        }
        cloudinaryService.deleteMedia(cloudId);
        // return ResponseEntity.ok(result);

        mediaService.deleteMedia(mediaId);
        return ResponseEntity.noContent().build();
    }

    // @DeleteMapping("/{productId}")
    // @PreAuthorize("isAuthenticated() and @mediaSecurity.isOwnerMedia(#mediaId)
    // and hasAnyRole('ADMIN','SELLER')")
    // public ResponseEntity<?> deleteMediaProduct(@PathVariable String productId) {
    // if (!mediaService.existsByMediaId(mediaId)) {
    // return ResponseEntity.status(HttpStatus.NOT_FOUND).body("media Not Found");
    // }
    // mediaService.deleteMedia(mediaId);
    // return ResponseEntity.noContent().build();
    // }
}
