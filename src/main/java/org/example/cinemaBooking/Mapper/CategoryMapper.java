package org.example.cinemaBooking.Mapper;

import org.example.cinemaBooking.Entity.Category;
import org.example.cinemaBooking.Model.Request.CategoryRequest;
import org.example.cinemaBooking.Model.Response.CategoryResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category toCategory(CategoryRequest request);

    CategoryResponse toCategoryResponse(Category category);

}