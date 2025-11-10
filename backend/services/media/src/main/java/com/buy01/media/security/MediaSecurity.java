package com.buy01.media.security;

import org.springframework.stereotype.Component;

import com.buy01.media.product.ProductClient;
import com.buy01.media.service.MediaService;

import feign.FeignException;

import org.springframework.security.core.context.SecurityContextHolder;

import lombok.RequiredArgsConstructor;

@Component("mediaSecurity")
@RequiredArgsConstructor
public class MediaSecurity {
    private final ProductClient productClient;
    private final MediaService mediaService;
    public Boolean isOwner(String userId) {
        return userId.equals(SecurityContextHolder.getContext().getAuthentication().getCredentials());
    }

    public Boolean isOwnerProduct(String productId) {
        Object currentUserId = SecurityContextHolder.getContext().getAuthentication().getCredentials();
        try {
            String userId = productClient.getProductById(productId).getUserId();
            return userId.equals(currentUserId);

        } catch (FeignException e) {
            return false;
        }
    }
    public Boolean isOwnerMedia(String mediaId) {
        Object currentUserId = SecurityContextHolder.getContext().getAuthentication().getCredentials();
        try {
            String productId = mediaService.findMediaById(mediaId).get().getProductId();
            String userId = productClient.getProductById(productId).getUserId();
            return userId.equals(currentUserId);

        } catch (Exception e) {
            return false;
        }
    }

}
