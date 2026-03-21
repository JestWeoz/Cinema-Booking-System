package org.example.cinemaBooking.Mapper;

import org.example.cinemaBooking.Dto.Request.Product.CreateProductRequest;
import org.example.cinemaBooking.Dto.Request.Product.UpdateProductRequest;
import org.example.cinemaBooking.Dto.Response.Product.ProductResponse;
import org.example.cinemaBooking.Entity.Product;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product toEntity(CreateProductRequest request);

    ProductResponse toResponse(Product product);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(@MappingTarget Product product, UpdateProductRequest request);
}