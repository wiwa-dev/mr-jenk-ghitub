package com.buy01.media.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.buy01.media.model.Media;

import java.util.List;
import java.util.Optional;

public interface MediaRepository extends MongoRepository<Media, String> {

    Optional<Media> findByProductId(String productId);

    List<Media> findAllByProductId(String productId);

    List<Media> findAllByOrderByUploadDateAsc();
}
