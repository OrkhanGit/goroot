package com.gpsroot.barcode.mapper;

import com.gpsroot.barcode.dto.ProductDto;
import com.gpsroot.barcode.dto.ProductMovementDto;
import com.gpsroot.barcode.dto.ProductsName;
import com.gpsroot.barcode.model.Product;
import com.gpsroot.barcode.model.ProductMovement;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductsName toProductsName(Product product);

    ProductDto toProductDto(Product product);

    ProductMovementDto toProductMovementDto(ProductMovement productMovement);

}