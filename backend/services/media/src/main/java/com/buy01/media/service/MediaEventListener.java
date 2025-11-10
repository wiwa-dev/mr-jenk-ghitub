package com.buy01.media.service;

import com.buy01.media.dto.ProductDeleteEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaEventListener {

    private final MediaService mediaService;

    @KafkaListener(topics = "product-events")
    public void handleProductDeleted(ProductDeleteEvent event) {
        if (!"PRODUCT_DELETED".equals(event.getEventType())) {
            return;

        }
        log.info("Received product deletion event for product: {}", event.getProductId());

        try {
            mediaService.deleteAllMediasByProductId(event.getProductId());
            log.info("Successfully deleted media for product: {}", event.getProductId());
        } catch (Exception e) {
            // TODO: handle exception
            log.error("Failed to delete media product {}: {}",event.getEventType(),e.getMessage());
        }
    }
}