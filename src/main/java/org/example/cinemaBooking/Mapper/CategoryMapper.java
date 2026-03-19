package org.example.cinemaBooking.Mapper;

import org.example.cinemaBooking.Dto.Request.CategoryRequest;
import org.example.cinemaBooking.Entity.Category;
import org.example.cinemaBooking.Dto.Response.CategoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category toEntity(CategoryRequest request);

    CategoryResponse toResponse(Category category);


    void updateCategory(CategoryRequest request, @MappingTarget Category category);
}