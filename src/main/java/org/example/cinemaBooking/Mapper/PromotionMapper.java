package org.example.cinemaBooking.Mapper;

import org.example.cinemaBooking.Dto.Request.Promotion.CreatePromotionRequest;
import org.example.cinemaBooking.Dto.Request.Promotion.UpdatePromotionRequest;
import org.example.cinemaBooking.Dto.Response.Promotion.PromotionResponse;
import org.example.cinemaBooking.Entity.Promotion;



import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface PromotionMapper {

    // CREATE
    Promotion toEntity(CreatePromotionRequest request);

    // RESPONSE
    PromotionResponse toResponse(Promotion promotion);

    // UPDATE (🔥 QUAN TRỌNG)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(@MappingTarget Promotion promotion, UpdatePromotionRequest request);
}