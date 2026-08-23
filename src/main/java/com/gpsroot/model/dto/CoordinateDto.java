package com.gpsroot.model.dto;

import lombok.Data;

@Data
public class CoordinateDto {

    private Double latitude;
    private Double longitude;
    private String address;
    private Long storeNumber;
    private String mapLink;

}
