package org.example.cinemaBooking.Mapper;

import org.example.cinemaBooking.Dto.Request.CreateComboRequest;
import org.example.cinemaBooking.Dto.Request.UpdateComboRequest;
import org.example.cinemaBooking.Dto.Response.ComboResponse;
import org.example.cinemaBooking.Entity.Combo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = ComboItemMapper.class)
public interface ComboMapper {

    @Mapping(target = "items", ignore = true)
    Combo toEntity(CreateComboRequest createComboRequest);

    @Mapping(target = "items", source = "items")
    ComboResponse toResponse(Combo combo);

    @Mapping(target = "items", ignore = true)
    void updateCombo(UpdateComboRequest updateComboRequest, @MappingTarget Combo combo);
}