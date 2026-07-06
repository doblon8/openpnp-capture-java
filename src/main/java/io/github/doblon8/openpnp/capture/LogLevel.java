package io.github.doblon8.openpnp.capture;

public enum LogLevel {
    EMERGENCY,
    ALERT,
    CRITICAL,
    ERROR,
    WARNING,
    NOTICE,
    INFO,
    DEBUG,
    VERBOSE;

    private static final LogLevel[] VALUES = values();

    public static LogLevel fromNative(int value) {
        if (value < 0 || value >= VALUES.length) {
            throw new IllegalArgumentException("Unknown log level: " + value);
        }
        return VALUES[value];
    }
}
