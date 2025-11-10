package com.buy01.media.model;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document("medias")

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Media {
    @Id
    private String id;
    private String imagePath;
    private String fileName;
    private String cloudId;
    private Long fileSize;
    private String mimeType;
    private Instant uploadDate;
    private String productId;
}
