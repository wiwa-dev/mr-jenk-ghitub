package com.buy01.product.dto;

import java.util.List;

import com.buy01.product.model.Product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductInfo {
    private Product product;
    private SellerInfo seller;
    private List<MediaInfo> medias;
}
