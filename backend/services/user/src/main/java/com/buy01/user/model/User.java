package com.buy01.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("users") // Nom de la collection dans MongoDB
@Data // Annotation Lombok pour générer les getters, setters, toString, equals et
      // hashCode
@NoArgsConstructor // Annotation Lombok pour générer un constructeur sans arguments
@AllArgsConstructor // Annotation Lombok pour générer un constructeur avec tous les champs
public class User {
    @Id
    private String id;

    private String firstName;

    private String lastName;

    @Indexed(unique = true)
    private String email;

    private String password;

    private Role role;

    private String avatar;

}
