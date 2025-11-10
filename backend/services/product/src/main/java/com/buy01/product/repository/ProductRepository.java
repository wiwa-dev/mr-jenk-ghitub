package com.buy01.product.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.buy01.product.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends MongoRepository<Product, String> {
    // Méthode de requête personnalisée pour trouver un utilisateur par email
    // Cette méthode sera automatiquement implémentée par Spring Data MongoDB
    // Elle retourne un Optional<Product> pour gérer les cas où le produit pourrait ne pas être trouvé
    Optional<Product> findByUserId(String userId);  
    
    List<Product> findAllByUserId(String userId);
}
