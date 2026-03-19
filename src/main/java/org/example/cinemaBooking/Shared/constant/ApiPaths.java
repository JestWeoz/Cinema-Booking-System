package org.example.cinemaBooking.Shared.constant;


public final class ApiPaths {

    public static final String API_V1 = "/api/v1";

    public static final class Auth {
        public static final String BASE = "/auth";
        public static final String LOGIN = "/login";
        public static final String REGISTER = "/register";
        public static final String LOGOUT = "/logout";
        public static final String REFRESH = "/refresh";
        public static final String INTROSPECT = "/introspect";
        public static final String FORGOT_PASSWORD = "/forgot-password";
        public static final String RESET_PASSWORD = "/reset-password";
    }

    public static final class User {
        public static final String BASE = "/users";
        public static final String ME = "/me";
        public static final String CHANGE_PASSWORD = "/change-password";
        public static final String CHANGE_AVATAR = "/change-avatar" ;
        public static final String LOCK = "/lock";
        public static final String UNLOCK = "/unlock";
    }
    public static final class Movie {
        public static final String BASE = "/movies";
        public static final String NOW_SHOWING = "/now-showing";
        public static final String COMING_SOON = "/coming-soon";
        public static final String SEARCH = "/search";
        public static final String RECOMMENDED = "/recommended";
        public static final String IMAGE = "/images";
    }

    public static final class Review {
        public static final String BASE = "/reviews";
        public static final String AVERAGE_RATING = "/average-rating";
    }
    public static final class Category {
        public static final String BASE = "/categories";
    }
    public static final class People {
         public static final String BASE = "/people";
    }
    public static final class Booking {
        public static final String BASE = "/bookings";
    }
}
