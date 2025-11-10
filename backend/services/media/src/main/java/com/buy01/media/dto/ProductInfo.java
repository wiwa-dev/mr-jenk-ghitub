package com.buy01.media.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor // Annotation Lombok pour générer un constructeur sans arguments
@AllArgsConstructor // Annotation Lombok pour générer un constructeur avec tous les champs
@Data
public class ProductInfo {
    private String id;

    private String name;

    private String description;

    private Double price;

    private int quantity;

    private String userId;

}
