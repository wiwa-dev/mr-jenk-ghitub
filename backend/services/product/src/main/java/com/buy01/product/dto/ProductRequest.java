package com.buy01.product.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    @NotBlank
    @Size(min = 5, max = 50, message = "Name must be between 5 and 50 characters")
    private String name;

    @NotBlank(message = "Description is required")
    @Size(min = 10, message = "Minimum 10 characters is required")
    private String description;

    @NotNull(message = "Price is required")
    // @Positive(message = "Price must be positive")
    private Double price;

    @NotNull(message = "Quantity is required")
    @PositiveOrZero(message = "Quantity must be zero or positive")
    private int quantity;
}
