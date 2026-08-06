package com.gpsroot.barcode.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "productMovement")
public class ProductMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String location;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private LocalDateTime updatedAt;

}
