package com.gpsroot.barcode.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductsName {

    private String productName;
    private String barcode;

    public ProductsName(String productName, String barcode) {
        this.productName = productName;
        this.barcode = barcode;
    }

}
