package org.example.cinemaBooking.Exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    UNCAUGHT_EXCEPTION(9999, "Uncaught exception", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_EXISTED(1001, "User already existed", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(1002, "User not found", HttpStatus.NOT_FOUND),
    USERNAME_EXISTED(1003, "Username already existed", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1004, "Username is invalid", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1005, "Password is invalid", HttpStatus.BAD_REQUEST),
    WRONG_PASSWORD(1006, "Wrong password", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(1007, "Email already existed", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID(1008, "Email is invalid", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1009, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    ROLE_INVALID(1010, "Role is invalid", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(1011, "U dont have permission", HttpStatus.FORBIDDEN),
    CATEGORY_NOT_FOUND(1012, "Category not found", HttpStatus.NOT_FOUND),
    PRODUCT_NOT_FOUND(1013, "Product not found", HttpStatus.NOT_FOUND),
    TABLE_NOT_FOUND(1014, "Table not found", HttpStatus.NOT_FOUND),
    ORDER_NOT_FOUND(1015, "Order not found", HttpStatus.NOT_FOUND),
    ;


     int code;
     String message;
     HttpStatusCode httpStatusCode;

}
