package io.github.doblon8.openpnp.capture;

import java.awt.image.BufferedImage;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

import static io.github.doblon8.openpnp.capture.bindings.openpnp_capture.*;

public class CaptureStream implements AutoCloseable {
    private final CaptureContext context;
    private final int id;
    private final CaptureFormat format;

    public CaptureStream(CaptureContext context, int id, CaptureFormat format) {
        this.context = context;
        this.id = id;
        this.format = format;
    }

    /**
     * Capture a single frame from the stream and return it as a BufferedImage.
     *
     * @return a BufferedImage containing the captured frame.
     */
    public BufferedImage capture() {
        int width = format.info().width();
        int height = format.info().height();
        byte[] bytes;

        try (Arena arena = Arena.ofConfined()) {
            int numBytes = width * height * 3; // 3 bytes per pixel for each of R, G, and B channels
            MemorySegment bufferPointer = arena.allocate(ValueLayout.JAVA_BYTE, numBytes);

            int result = Cap_captureFrame(context.getSegment(), id, bufferPointer, numBytes);
            CaptureResult captureResult = CaptureResult.values()[result];
            if (captureResult != CaptureResult.OK) {
                throw new CaptureException("Error capturing frame: " + result);
            }
            bytes = bufferPointer.toArray(ValueLayout.JAVA_BYTE);
        }

        // Note: openpnp-capture documentation says that frames are always 24-bit RGB:
        // https://github.com/openpnp/openpnp-capture/blob/32a9bdd3e8e3a31b12cb6573e7c6076208421651/include/openpnp-capture.h#L192-L202
        // However, the original JNI-based openpnp-capture-java uses TYPE_3BYTE_BGR:
        // https://github.com/openpnp/openpnp-capture-java/blob/06504247a2688d0258f46cb4d7e451cb9715d241/src/main/java/org/openpnp/capture/CaptureStream.java#L102-L109
        // noting that the expected R/B swap did not occur, and that the comment's author thinks it's because
        // setDataElements converts to the BufferedImage's color model, but I don't think that is the case.
        // I just think that the openpnp-capture documentation might be wrong, as the openpnp-capture-java code seems to
        // have been working correctly in practice.
        // So I'll the same approach as openpnp-capture-java.
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        image.getRaster().setDataElements(0, 0, width, height, bytes);
        return image;
    }

    /**
     * Check if a new frame has been captured.
     *
     * @return true if a new frame has been captured, false otherwise.
     */
    public boolean hasNewFrame() {
        int result = Cap_hasNewFrame(context.getSegment(), id);
        return result == 1;
    }

    /**
     * Get the number of frames captured during the lifetime of the stream.
     *
     * @return the number of frames captured during the lifetime of the stream.
     */
    public int getStreamFrameCount() {
        return Cap_getStreamFrameCount(context.getSegment(), id);
    }

    /**
     * Get the min/max limits and the default value of a camera/stream property (e.g. zoom, exposure etc.).
     *
     * @param property the property to get the limits for.
     * @return a PropertyLimits object containing the min/max limits and the default value of the property.
     * @throws CaptureException if an error occurs while getting the property limits.
     */
    public PropertyLimits getPropertyLimits(CaptureProperty property) {
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment minPointer = arena.allocate(ValueLayout.JAVA_INT);
            MemorySegment maxPointer = arena.allocate(ValueLayout.JAVA_INT);
            MemorySegment defaultValuePointer = arena.allocate(ValueLayout.JAVA_INT);

            int result = Cap_getPropertyLimits(context.getSegment(), id, property.value(), minPointer, maxPointer, defaultValuePointer);
            CaptureResult captureResult = CaptureResult.values()[result];
            return switch (captureResult) {
                case OK -> new PropertyLimits(
                        minPointer.get(ValueLayout.JAVA_INT, 0),
                        maxPointer.get(ValueLayout.JAVA_INT, 0),
                        defaultValuePointer.get(ValueLayout.JAVA_INT, 0)
                );
                case PROPERTY_NOT_SUPPORTED ->
                        throw new CaptureException("Property " + property + " is not supported by this stream.");
                default -> throw new CaptureException("Error getting property limit for property " + property + ": context, stream are invalid.");
            };
        }
    }

    /**
     * Check if a stream is open, i.e. is capturing data.
     *
     * @return true if the stream is open, false otherwise.
     */
    public boolean isOpen() {
        int result = Cap_isOpenStream(context.getSegment(), id);
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
