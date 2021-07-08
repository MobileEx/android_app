package com.pilates.app.util;

public final class Constant {

    public static final class HandlerMessage {
        public static final int HANDLE_TRAINEE_USERNAME = 1;
        public static final int HANDLE_REMOTE_VIDEO = 2;
        public static final int HANDLE_CONNECTION_ESTABLISHED = 3;
        public static final int HANDLE_TRAINEE_LEAVED = 4;
        public static final int HANDLE_ON_HOLD = 5;
        public static final int HANDLE_SWITCHED = 6;
        public static final int CLASS_INITIALIZED = 7;
        public static final int HANDLE_MEDIA_STATS = 8;
        public static final int HANDLE_CLASS_STARTED = 9;
        public static final int HANDLE_CLASS_NOT_STARTED = 10;
        public static final int CONNECT_DONE = 11;
        public static final int HANDLE_TRAINEE_JOINED = 12;
        public static final int HANDLE_CLASS_NOT_EXISTS = 13;
        public static final int HANDLE_OFFER_RECEIVED = 14;

    }

    public static final class LoginScreens {
        public static final int LOGIN = 0;
        public static final int PRESIGNUP = 1;
        public static final int SIGNUP = 2;
        public static final int EXPERIENCE = 3;
        public static final int TIMES_PER_WEEK = 4;
        public static final int CLASS_DURATIONS = 5;
        public static final int TIME_OF_DAY = 6;
        public static final int ACCOUNT_DETAILS = 7;
        public static final int BASIC_COMPLETE = 8;
        public static final int ABOUT_BIO = 9;
        public static final int PROFILE_PIC = 10; // :(
        public static final int GALLERY = 11;
        public static final int DOCUMENTS = 12;
        public static final int PILATES_TYPES = 13;
        public static final int CONFIRMATION = 14;
        public static final int COMPLETE = 15;

        public static final int TRAINEE_EXPERIENCE_LEVEL = 20;
        public static final int TRAINEE_TIMES_PER_WEEK = 21;
        public static final int TRAINEE_CLASS_DURATIONS = 22;
        public static final int TRAINEE_TIME_OF_DAY = 23;
        public static final int TRAINEE_GOALS = 24;
        public static final int TRAINEE_INSTRUCTOR = 25;
        public static final int TRAINEE_COMPLETE = 26;
    }

    public static final class PickReason {
        public static final int AVATAR = 1;
        public static final int GALLERY = 2;
        public static final int DOCUMENT = 3;
    }

    public static class WooConfigurations {
        public static final String GOOGLE_CLIENT_ID = "209163990760-r4kkp5llc3epvnvqtb0tp6ab130eiag6.apps.googleusercontent.com";
    }
    public static class ServiceEndpoints {
        //prod
        public static final String USER_MANAGEMENT = "http://52.212.246.239:8082";
        //public static final String USER_MANAGEMENT = "http://192.168.100.4:8082";
        //public static final String SIGNALING_APP = "ws://192.168.100.4:8080/streaming/callone";
        public static final String SIGNALING_APP = "ws://18.203.172.206:8080/streaming/callone";
    }
}
