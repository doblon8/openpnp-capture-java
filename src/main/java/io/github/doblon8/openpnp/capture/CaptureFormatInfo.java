package io.github.doblon8.openpnp.capture;

import io.github.doblon8.openpnp.capture.bindings.CapFormatInfo;

import java.lang.foreign.MemorySegment;

public record CaptureFormatInfo(int width, int height, int fourcc, int fps, int bpp) {

    public CaptureFormatInfo(MemorySegment segment) {
        this(
            CapFormatInfo.width(segment),
            CapFormatInfo.height(segment),
            CapFormatInfo.fourcc(segment),
            CapFormatInfo.fps(segment),
            CapFormatInfo.bpp(segment)
        );
    }
}
