package io.github.doblon8.openpnp.capture;

public enum CaptureResult {
    OK,
    ERROR,
    DEVICE_NOT_FOUND,
    FORMAT_NOT_SUPPORTED,
    PROPERTY_NOT_SUPPORTED;

    private static final CaptureResult[] VALUES = CaptureResult.values();

    public static CaptureResult fromNative(int value) {
        if (value < 0 || value >= VALUES.length) {
            throw new CaptureException("Unknown capture result code: " + value);
        }
        return VALUES[value];
    }
}
