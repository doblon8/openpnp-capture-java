package io.github.doblon8.openpnp.capture;

import static io.github.doblon8.openpnp.capture.bindings.openpnp_capture.Cap_closeStream;
import static io.github.doblon8.openpnp.capture.bindings.openpnp_capture.Cap_isOpenStream;

public class CaptureStream implements AutoCloseable {
    private final CaptureContext context;
    private final int id;

    public CaptureStream(CaptureContext context, int id) {
        this.context = context;
        this.id = id;
    }

    /**
     * Check if a stream is open, i.e. is capturing data.
     *
     * @return true if the stream is open, false otherwise.
     */
    public boolean isOpen() {
        var result = Cap_isOpenStream(context.getSegment(), id);
        return result == 1;
    }

    /**
     * Close a capture stream.
     *
     * @throws CaptureException if an error occurs while closing the capture stream.
     */
    @Override
    public void close() {
        int result = Cap_closeStream(context.getSegment(), id);
        CaptureResult captureResult = CaptureResult.values()[result];
        if (captureResult != CaptureResult.OK) {
            throw new CaptureException("Error closing capture stream: " + result);
        }
    }
}
