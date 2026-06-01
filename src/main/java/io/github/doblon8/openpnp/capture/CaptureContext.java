package io.github.doblon8.openpnp.capture;

import java.lang.foreign.MemorySegment;

import static io.github.doblon8.openpnp.capture.bindings.openpnp_capture.Cap_createContext;
import static io.github.doblon8.openpnp.capture.bindings.openpnp_capture.Cap_releaseContext;

public final class CaptureContext implements AutoCloseable {

    private final MemorySegment segment;

    public CaptureContext() {
        segment = Cap_createContext();
    }

    @Override
    public void close() throws CaptureException{
        int result = Cap_releaseContext(segment);
        CaptureResult captureResult = CaptureResult.values()[result];
        switch (captureResult) {
            case OK -> {
                // Context released successfully
            }
            case ERROR -> throw new CaptureException("Error releasing capture context.");
            default -> throw new CaptureException("Failed to release capture context: " + result);
        }
    }

    public MemorySegment getSegment() {
        return segment;
    }
}