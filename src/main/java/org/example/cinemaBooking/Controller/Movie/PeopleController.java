package org.example.cinemaBooking.Controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.example.cinemaBooking.Dto.Request.CreatePeopleRequest;
import org.example.cinemaBooking.Dto.Request.UpdatePeopleRequest;
import org.example.cinemaBooking.Dto.Response.MoviePeopleResponse;
import org.example.cinemaBooking.Dto.Response.PeopleResponse;
import org.example.cinemaBooking.Service.PeopleService;
import org.example.cinemaBooking.Shared.constant.ApiPaths;
import org.example.cinemaBooking.Shared.response.ApiResponse;
import org.example.cinemaBooking.Shared.response.PageResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequestMapping(ApiPaths.API_V1 + ApiPaths.People.BASE)
public class PeopleController {
    PeopleService peopleService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ApiResponse<PeopleResponse> createPeople(@RequestBody @Valid CreatePeopleRequest request) {
        var response = peopleService.createPeople(request);
        log.info("[PEOPLE_CONTROLLER] - Create people with id: {}", response.id());
        return ApiResponse.<PeopleResponse>builder()
                .success(true)
                .data(response)
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ApiResponse<PeopleResponse> updatePeople(@PathVariable String id, @RequestBody @Valid UpdatePeopleRequest request) {
        var response = peopleService.updatePeople(id, request);
        log.info("[PEOPLE_CONTROLLER] - Update people with id: {}", response.id());
        return ApiResponse.<PeopleResponse>builder()
                .success(true)
                .data(response)
                .build();

    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deletePeople(@PathVariable String id) {
        peopleService.deletePeople(id);
        log.info("[PEOPLE_CONTROLLER] - Delete people with id: {}", id);
        return ApiResponse.<Void>builder()
                .success(true)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<PeopleResponse> getPeopleById(@PathVariable String id) {
        var response = peopleService.getPeopleById(id);
        log.info("[PEOPLE_CONTROLLER] - Get people with id: {}", response.id());
        return ApiResponse.<PeopleResponse>builder()
                .success(true)
                .data(response)
                .build();
    }


    @GetMapping
    public ApiResponse<PageResponse<PeopleResponse>> getAllPeople(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String key
    ) {
        var response = peopleService.getAllPeoples(page, size, key);
        log.info("[PEOPLE_CONTROLLER] - Get all people");
        return ApiResponse.<PageResponse<PeopleResponse>>builder()
                .success(true)
                .data(response)
                .build();
    }



//    Lay danh sach phim nguoi tham gia
    @GetMapping("/{peopleId}" + ApiPaths.Movie.BASE)
    public ApiResponse<List<MoviePeopleResponse>> getMoviesByPeopleId(@PathVariable String peopleId){
        var response = peopleService.getMoviesByPeople(peopleId);
        log.info("[PEOPLE_CONTROLLER] - Get movies by people id: {}, total: {}", peopleId, response.size());
        return ApiResponse.<List<MoviePeopleResponse>>builder()
                .success(true)
                .data(response)
                .build();
    }


}
