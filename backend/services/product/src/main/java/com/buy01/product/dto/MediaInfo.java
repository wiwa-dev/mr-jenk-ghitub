package com.buy01.product.dto;

import java.time.Instant;

import lombok.Data;

@Data
public class MediaInfo {
    private String id;
    private String imagePath;
    private String fileName;
    private String cloudId;
    private String fileSize;
    private String mimeType;
    private Instant uploadDate;
    private String productId;
}
