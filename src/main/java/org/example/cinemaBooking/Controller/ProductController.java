    package org.example.cinemaBooking.Controller;

    import jakarta.validation.Valid;
    import lombok.RequiredArgsConstructor;
    import lombok.experimental.FieldDefaults;
    import lombok.extern.slf4j.Slf4j;
    import org.example.cinemaBooking.Dto.Request.Product.CreateProductRequest;
    import org.example.cinemaBooking.Dto.Request.Product.UpdateProductRequest;
    import org.example.cinemaBooking.Dto.Response.Product.ProductResponse;
    import org.example.cinemaBooking.Service.Product.ProductService;
    import org.example.cinemaBooking.Shared.constant.ApiPaths;
    import org.example.cinemaBooking.Shared.response.ApiResponse;
    import org.example.cinemaBooking.Shared.response.PageResponse;
    import org.springframework.security.access.prepost.PreAuthorize;
    import org.springframework.web.bind.annotation.*;

    @RestController
    @RequiredArgsConstructor
    @Slf4j
    @FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
    @RequestMapping(ApiPaths.API_V1 + ApiPaths.Product.BASE)
    public class ProductController {
        ProductService productService;

        @PreAuthorize("hasRole('ADMIN')")
        @PostMapping
        public ApiResponse<ProductResponse> createProduct(@RequestBody @Valid CreateProductRequest request){
            ProductResponse response = productService.createProduct(request);
            log.info(("[PRODUCT_CONTROLLER] Created product with id: {}"), response.id());
            return ApiResponse.<ProductResponse>builder()
                    .success(true)
                    .message("Product created successfully")
                    .data(response)
                    .build();
        }

        @PreAuthorize("hasRole('ADMIN')")
        @PutMapping("/{id}")
        public ApiResponse<ProductResponse> updateProduct(@PathVariable String id, @RequestBody @Valid UpdateProductRequest request) {
            ProductResponse response = productService.updateProduct(id, request);
            log.info(("[PRODUCT_CONTROLLER] Updated product with id: {}"), response.id());
            return ApiResponse.<ProductResponse>builder()
                    .success(true)
                    .message("Product updated successfully")
                    .data(response)
                    .build();
        }

        @PreAuthorize("hasRole('ADMIN')")
        @DeleteMapping("/{id}")
        public ApiResponse<Void> deleteProduct(@PathVariable String id) {
            productService.deleteProduct(id);
            log.info(("[PRODUCT_CONTROLLER] Deleted product with id: {}"), id);
            return ApiResponse.<Void>builder()
                    .success(true)
                    .message("Product deleted successfully")
                    .build();
        }


        @GetMapping("/{id}")
        public ApiResponse<ProductResponse> getProductById(@PathVariable String id) {
            var productResponse = productService.getProductById(id);
            log.info(("[PRODUCT_CONTROLLER] Retrieved product with id: {}"), id);
            return ApiResponse.<ProductResponse>builder()
                    .success(true)
                    .message("Product retrieved successfully")
                    .data(productResponse)
                    .build();
        }

        @PreAuthorize("hasRole('ADMIN')")
        @PatchMapping("/{id}/toggle-active")
        public ApiResponse<ProductResponse> toggleProductActiveStatus(@PathVariable String id) {
            productService.toggleActiveProduct(id);
            log.info("[PRODUCT_CONTROLLER] Toggled active status for product with id: {}", id);
            return ApiResponse.<ProductResponse>builder()
                    .success(true)
                    .message("Product active status toggled successfully")
                    .data(productService.getProductById(id))
                    .build();
        }


        @GetMapping
        public ApiResponse<PageResponse<ProductResponse>> getAll(
                @RequestParam(defaultValue = "0") int page,
                @RequestParam(defaultValue = "10") int size,
                @RequestParam(defaultValue = "createdAt") String sortBy,
                @RequestParam(defaultValue = "desc") String direction,
                @RequestParam(required = false) String keyword
        ) {
            return ApiResponse.<PageResponse<ProductResponse>>builder()
                    .success(true)
                    .message("Products retrieved successfully")
                    .data(productService.getAllProducts(page, size, sortBy, direction, keyword))
                    .build();
        }

        @GetMapping("/active")
        public ApiResponse<PageResponse<ProductResponse>> getActiveProducts(
                @RequestParam(defaultValue = "1") int page,
                @RequestParam(defaultValue = "10") int size,
                @RequestParam(defaultValue = "price") String sortBy,
                @RequestParam(defaultValue = "asc") String direction){
            return ApiResponse.<PageResponse<ProductResponse>>builder()
                    .success(true)
                    .message("Active products retrieved successfully")
                    .data(productService.getProductActive(page, size, sortBy, direction))
                    .build();
        }
    }
