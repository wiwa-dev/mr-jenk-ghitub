package com.buy01.product.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("products") // Nom de la collection dans MongoDB
@Data // Annotation Lombok pour générer les getters, setters, toString, equals et
      // hashCode
@NoArgsConstructor // Annotation Lombok pour générer un constructeur sans arguments
@AllArgsConstructor // Annotation Lombok pour générer un constructeur avec tous les champs
public class Product {
    @Id
    private String id;

    private String name;

    private String description;

    private Double price;

    private int quantity;

    private String userId;

}
