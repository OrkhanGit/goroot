package com.gpsroot.barcode.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProductMovementDto {

    private String location;
    private LocalDateTime updatedAt;

}