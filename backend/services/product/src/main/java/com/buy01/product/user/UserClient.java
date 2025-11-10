package com.buy01.product.user;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.buy01.product.dto.SellerInfo;

@FeignClient(name = "user-service", url = "${application.config.user-url}")
public interface UserClient {

    @GetMapping("/all/{role}")
    List<SellerInfo> getAllUserByRole(@PathVariable String role);
}
