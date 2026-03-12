package org.example.cinemaBooking.Shared.constant;


public final class ApiPaths {

    public static final String API_V1 = "/api/v1";

    public static final class Auth {
        public static final String BASE = "/auth";
        public static final String LOGIN = "/login";
        public static final String REGISTER = "/register";
        public static final String LOGOUT = "/logout";
        public static final String REFRESH = "/refresh";
    }

    public static final class User {
        public static final String BASE = "/users";
    }

    public static final class Booking {
        public static final String BASE = "/bookings";
    }
}
