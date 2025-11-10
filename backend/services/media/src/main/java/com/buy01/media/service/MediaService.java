package com.buy01.media.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import com.buy01.media.repository.MediaRepository;
import com.buy01.media.cloudinary.CloudinaryService;
import com.buy01.media.model.Media;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MediaService {
    private final MediaRepository mediaRepository;
    private final CloudinaryService cloudinaryService;

    public Media createMedia(Media media) {
        return mediaRepository.save(media);
    }

    public List<Media> findAllMedias() {
        return mediaRepository.findAllByOrderByUploadDateAsc();
    }

    public List<Media> findAllMediasByProductId(String productId) {
        return mediaRepository.findAllByProductId(productId);
    }

    public Optional<Media> findMediaById(String id) {
        return Optional.ofNullable(mediaRepository.findById(id).orElse(null));
    }

    public Media findMediaByUserId(String productId) {
        return mediaRepository.findByProductId(productId).orElse(null);
    }

    public void deleteMedia(String id) {
        mediaRepository.deleteById(id);
    }

    public void deleteAllMediasByProductId(String productId) {
        List<Media> medias = mediaRepository.findAllByProductId(productId);
        medias.forEach(m -> {
            cloudinaryService.deleteMedia(m.getCloudId());
        });
    }

    public Boolean existsByMediaId(String mediaId) {
        return mediaRepository.findById(mediaId).isPresent();
    }
}
