package com.buy01.product.dto;

import java.util.List;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MyProductInfo  {
    private String id;

    private String name;

    private String description;

    private Double price;

    private int quantity;

    private String userId;
    
    private List<MediaInfo> medias ;
}
