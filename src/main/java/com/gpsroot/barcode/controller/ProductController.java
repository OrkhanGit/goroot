package com.gpsroot.barcode.controller;

import com.gpsroot.barcode.dto.ProductDto;
import com.gpsroot.barcode.dto.ProductsName;
import com.gpsroot.barcode.model.Product;
import com.gpsroot.barcode.model.ProductMovement;
import com.gpsroot.barcode.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;


    @PostMapping("/scan/{barcode}")
    public ResponseEntity<Product> scanBarcode(@RequestBody Product product,
                                               @PathVariable String barcode,
                                               @RequestParam String location) {

        return ResponseEntity.ok(productService.scanBarcode(product, barcode, location));
    }

    @GetMapping("/getAllProducts")
    public ResponseEntity<List<ProductsName>> getAllProducts() {

        return ResponseEntity.ok().body(productService.getAllProducts());

    }

    @GetMapping("/getProduct/{barcode}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable String barcode) {

        return ResponseEntity.ok(productService.getProduct(barcode));

    }

    @PutMapping("/update/{currentBarcode}/{newBarcode}/{productName}")
    public ResponseEntity<Void> updateProduct(@PathVariable String currentBarcode,
                                              @PathVariable String newBarcode,
                                              @PathVariable String productName) {
        productService.updateProduct(currentBarcode, newBarcode, productName);
        return ResponseEntity.ok().build();
    }

}
