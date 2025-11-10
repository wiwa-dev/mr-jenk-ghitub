package com.buy01.media.cloudinary;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Service
public class CloudinaryService {
    private final Cloudinary cloudinary;


    public CloudinaryService(@Value("${cloudinary.name}") String cloudName,
            @Value("${cloudinary.api_key}") String apiKey,
            @Value("${cloudinary.api_secret}") String apiSecret) {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret));
    }

    @SuppressWarnings("rawtypes")
    public Map deleteMedia(String publicId)  {
        try {
            var result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            return result;
        } catch (Exception e) {
            System.err.println("Erreur lors de la suppression du média: {}" + e.getMessage());

        }
        return ObjectUtils.emptyMap();
    }

}
