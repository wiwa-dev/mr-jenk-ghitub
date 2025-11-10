package com.buy01.product.media;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.buy01.product.dto.MediaInfo;

@FeignClient(name = "media-service", url = "${application.config.media-url}")
public interface MediaClient {

    @GetMapping
    List<MediaInfo> getAllMedia();
}
