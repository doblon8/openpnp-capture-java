package io.github.doblon8.openpnp.capture;

import java.lang.foreign.MemorySegment;

import static io.github.doblon8.openpnp.capture.bindings.openpnp_capture.Cap_createContext;
import static io.github.doblon8.openpnp.capture.bindings.openpnp_capture.Cap_releaseContext;

public final class CaptureContext {

    private final MemorySegment segment;

    public CaptureContext() {
        segment = Cap_createContext();
    }

    public CaptureResult releaseContext() {
        int result = Cap_releaseContext(segment);
        return CaptureResult.values()[result];
    }
}