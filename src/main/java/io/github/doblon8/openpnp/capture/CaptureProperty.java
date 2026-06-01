package io.github.doblon8.openpnp.capture;

public enum CaptureProperty
{
    EXPOSURE(1),
    FOCUS(2),
    ZOOM(3),
    WHITE_BALANCE(4),
    GAIN(5),
    BRIGHTNESS(6),
    CONTRAST(7),
    SATURATION(8),
    GAMMA(9),
    HUE(10),
    SHARPNESS(11),
    BACK_LIGHT_COMPENSATION(12),
    POWER_LINE_FREQUENCY(13),
    LAST(14),
    ;

    private final int value;
    CaptureProperty(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}
