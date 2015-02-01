package com.greenyetilab.utils;

/**
 * Assert implementation
 */
public class Assert {
    public static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
