package com.buy01.media.dto;

import java.time.Instant;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MediaRequest {

    @NotBlank(message = "Imagepath is required")
    private String imagePath;

    private String fileName;
    private String cloudId;
    @NotNull(message = "fileSize is required")
    private Long fileSize;
    
    @NotBlank(message = "mimeType is required")
    private String mimeType;

    private Instant uploadDate;

}
