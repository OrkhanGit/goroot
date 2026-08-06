package com.gpsroot.barcode.service;

import com.gpsroot.barcode.dto.ProductDto;
import com.gpsroot.barcode.dto.ProductsName;
import com.gpsroot.barcode.mapper.ProductMapper;
import com.gpsroot.barcode.model.Product;
import com.gpsroot.barcode.model.ProductMovement;
import com.gpsroot.barcode.repository.ProductRepository;
import com.gpsroot.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional
    public Product scanBarcode(Product product, String barcode, String location) {

        Optional<Product> byBarcode = productRepository.findByBarcode(barcode);

        if (byBarcode.isPresent()) {
            Product existing = byBarcode.get();

            ProductMovement movement = new ProductMovement();
            movement.setLocation(location);
            movement.setUpdatedAt(LocalDateTime.now());
            movement.setProduct(existing);

            existing.getMovements().add(movement);

            return productRepository.save(existing);

        } else {
            product.setBarcode(barcode);
            return productRepository.save(product);
        }
    }

    public List<ProductsName> getAllProducts() {

        return productRepository.findAllByOrderByProductNameAsc().stream()
                .map(productMapper::toProductsName)
                .toList();

    }

    public ProductDto getProduct(String barcode) {

        Product byBarcode = productRepository.findByBarcode(barcode)
                .orElseThrow(() -> new NotFoundException("barcode not found"));

        return productMapper.toProductDto(byBarcode);

    }
}
