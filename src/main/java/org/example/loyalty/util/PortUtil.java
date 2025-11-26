package org.example.loyalty.util;

/**
 * Utility class to store the current HTTP server port.
 * Used by services to make internal HTTP requests.
 */
public class PortUtil {

    private static int port = 0; // default 0, meaning random

    private PortUtil() {
        // private constructor to prevent instantiation
    }

    public static void setPort(int p) {
        port = p;
    }

    public static int getPort() {
        return port;
    }
}

