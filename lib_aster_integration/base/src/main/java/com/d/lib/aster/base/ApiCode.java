package com.d.lib.aster.base;

/**
 * Network general status code definition
 */
public class ApiCode {

    /**
     * Corresponding to HTTP status code
     */
    public static class Http {
        public static final int UNAUTHORIZED = 401;
        public static final int FORBIDDEN = 403;
        public static final int NOT_FOUND = 404;
        public static final int REQUEST_TIMEOUT = 408;
        public static final int INTERNAL_SERVER_ERROR = 500;
        public static final int BAD_GATEWAY = 502;
        public static final int SERVICE_UNAVAILABLE = 503;
        public static final int GATEWAY_TIMEOUT = 504;
    }

    /**
     * Request request code
     */
    public static class Request {
        // Unknown mistake
        public static final int UNKNOWN = 1000;
        // Parsing error
        public static final int PARSE_ERROR = 1001;
        // Network Error
        public static final int NETWORK_ERROR = 1002;
        // Protocol error
        public static final int HTTP_ERROR = 1003;
        // Certificate error
        public static final int SSL_ERROR = 1005;
        // Connection timed out
        public static final int TIMEOUT_ERROR = 1006;
        // Call error
        public static final int INVOKE_ERROR = 1007;
        // Class conversion error
        public static final int CONVERT_ERROR = 1008;
    }
}
