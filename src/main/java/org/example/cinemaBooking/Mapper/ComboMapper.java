package org.example.cinemaBooking.Mapper;

import org.example.cinemaBooking.Dto.Request.Combo.CreateComboRequest;
import org.example.cinemaBooking.Dto.Request.Combo.UpdateComboRequest;
import org.example.cinemaBooking.Dto.Response.Combo.ComboResponse;
import org.example.cinemaBooking.Entity.Combo;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = ComboItemMapper.class)
public interface ComboMapper {

    @Mapping(target = "items", ignore = true)
    Combo toEntity(CreateComboRequest createComboRequest);

    @Mapping(target = "items", source = "items")
    ComboResponse toResponse(Combo combo);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "items", ignore = true)
    void updateCombo(UpdateComboRequest updateComboRequest, @MappingTarget Combo combo);
}