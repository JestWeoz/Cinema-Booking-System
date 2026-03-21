package org.example.cinemaBooking.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.cinemaBooking.Dto.Request.CreatePromotionRequest;
import org.example.cinemaBooking.Dto.Request.PromotionFilterRequest;
import org.example.cinemaBooking.Dto.Request.UpdatePromotionRequest;
import org.example.cinemaBooking.Dto.Response.PromotionResponse;
import org.example.cinemaBooking.Dto.Response.ValidationResultResponse;
import org.example.cinemaBooking.Service.PromotionService;
import org.example.cinemaBooking.Shared.constant.ApiPaths;
import org.example.cinemaBooking.Shared.response.ApiResponse;
import org.example.cinemaBooking.Shared.response.PageResponse;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping(ApiPaths.API_V1 + ApiPaths.Promotion.BASE)
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class PromotionController {

    PromotionService promotionService;

    // CREATE
    @PostMapping
    public ApiResponse<PromotionResponse> create(@RequestBody @Valid CreatePromotionRequest request) {
        return ApiResponse.<PromotionResponse>builder()
                .success(true)
                .message("Promotion created successfully")
                .data(promotionService.createPromotion(request))
                .build();
    }

    // UPDATE
    @PutMapping("/{id}")
    public ApiResponse<PromotionResponse> update(@PathVariable String id,
                                                 @RequestBody UpdatePromotionRequest request) {
        return ApiResponse.<PromotionResponse>builder()
                .success(true)
                .message("Promotion updated successfully")
                .data(promotionService.updatePromotion(id, request))
                .build();
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        promotionService.deletePromotion(id);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("Promotion deleted successfully")
                .build();
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ApiResponse<PromotionResponse> getById(@PathVariable String id) {
        return ApiResponse.<PromotionResponse>builder()
                .success(true)
                .data(promotionService.getPromotionById(id))
                .build();
    }

    @GetMapping("/code/{code}")
    public ApiResponse<PromotionResponse> getByCode(@PathVariable String code) {
        return ApiResponse.<PromotionResponse>builder()
                .success(true)
                .data(promotionService.getPromotionByCode(code))
                .build();
    }

    @GetMapping("/active")
    public ApiResponse<PageResponse<PromotionResponse>> getActivePromotion(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt`") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        return ApiResponse.<PageResponse<PromotionResponse>>builder()
                .success(true)
                .data(promotionService.getActivePromotions(page, size, sortBy, sortDir))
                .build();
    }
    // FILTER
    @GetMapping
    public ApiResponse<PageResponse<PromotionResponse>> getPromotions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "`createdAt`") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestBody @Valid PromotionFilterRequest request) {
        PageResponse<PromotionResponse> response = promotionService.getPromotions(page, size, sortBy, sortDir, request);

        return ApiResponse.<PageResponse<PromotionResponse>>builder()
                .success(true)
                .data(response)
                .build();
    }

    // PREVIEW
    @PostMapping("/preview")
    public ApiResponse<ValidationResultResponse> preview(
            @RequestParam String code,
            @RequestParam String userId,
            @RequestParam BigDecimal orderValue
    ) {
        return ApiResponse.<ValidationResultResponse>builder()
                .success(true)
                .data(promotionService.previewPromotion(code, userId, orderValue))
                .build();
    }

    // APPLY
    @PostMapping("/apply")
    public ApiResponse<Void> apply(
            @RequestParam String promotionId,
            @RequestParam String userId
    ) {
        promotionService.applyPromotion(promotionId, userId);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("Promotion applied successfully")
                .build();
    }
}