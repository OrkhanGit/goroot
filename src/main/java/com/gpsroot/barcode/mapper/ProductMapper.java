package com.gpsroot.barcode.mapper;

import com.gpsroot.barcode.dto.ProductDto;
import com.gpsroot.barcode.dto.ProductsName;
import com.gpsroot.barcode.model.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductsName toProductsName(Product product);

    Product toProduct(ProductDto productDto);
    ProductDto toProductDto(Product product);

}
