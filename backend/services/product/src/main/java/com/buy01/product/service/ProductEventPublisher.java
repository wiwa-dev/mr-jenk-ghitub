package com.buy01.product.service;

import com.buy01.product.dto.ProductDeleteEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import static org.springframework.kafka.support.KafkaHeaders.TOPIC;


@Service
@RequiredArgsConstructor
@Slf4j
public class ProductEventPublisher {

    private final KafkaTemplate<String, ProductDeleteEvent> kafkaTemplate;

    public void publishProductDeleted(ProductDeleteEvent event) {

        log.info("Publishing product deleted event for product: {}", event.getProductId());
        Message<ProductDeleteEvent> message = MessageBuilder
        .withPayload(event)
        .setHeader(TOPIC, "product-events")
        .build();
        // Send asynchronously and get a CompletableFuture
        var future = kafkaTemplate.send(message);

        // Handle the result asynchronously
        future.whenComplete((result, exception) -> {
            if (exception == null) {
                // Success
                log.info("Successfully published event for product: {} to topic: {}, partition: {}, offset: {}",
                        event.getProductId(),
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                // Failure
                log.error("Failed to publish event for product: {} - Error: {}",
                        event.getProductId(), exception.getMessage(), exception);

            }
        });
    }
}
