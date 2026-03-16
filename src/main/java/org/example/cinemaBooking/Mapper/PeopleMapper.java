package org.example.cinemaBooking.Mapper;

import org.example.cinemaBooking.Dto.Request.UpdatePeopleRequest;
import org.example.cinemaBooking.Dto.Response.PeopleResponse;
import org.example.cinemaBooking.Entity.People;
import org.example.cinemaBooking.Dto.Request.CreatePeopleRequest;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface PeopleMapper {
    People toEntity(CreatePeopleRequest createPeopleRequest);

    PeopleResponse toResponse(People people);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    People updatePeople(UpdatePeopleRequest request, @MappingTarget People people);


}