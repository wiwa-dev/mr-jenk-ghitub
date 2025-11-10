package com.buy01.user.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.buy01.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    // Méthode de requête personnalisée pour trouver un utilisateur par email
    // Cette méthode sera automatiquement implémentée par Spring Data MongoDB
    // Elle retourne un Optional<User> pour gérer les cas où l'utilisateur pourrait
    // ne pas être trouvé
    Optional<User> findByEmail(String email);

    Optional<List<User>> findAllByRole(String role);
}
