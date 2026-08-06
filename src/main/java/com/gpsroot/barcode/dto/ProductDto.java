package com.gpsroot.barcode.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductDto {

    private String productName;
    private String barcode;
    private LocalDateTime createdAt;
    private List<ProductMovementDto> movements;

}