package io.github.doblon8.openpnp.capture;

import io.github.doblon8.openpnp.capture.bindings.CapFormatInfo;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.List;
import java.util.stream.IntStream;

import static io.github.doblon8.openpnp.capture.bindings.openpnp_capture.*;

public final class CaptureDevice {
    private final CaptureContext context;
    private final int id;
    private final String name;
    private final String uniqueId;
    private final List<CaptureFormat> formats;

    public CaptureDevice(CaptureContext context, int deviceId) {
        this.context = context;
        this.id = deviceId;
        this.name = getDeviceName(deviceId);
        this.uniqueId = getDeviceUniqueId(deviceId);
        this.formats = IntStream.range(0, getNumFormats(deviceId))
                .mapToObj(formatId -> new CaptureFormat(formatId, getFormatInfo(deviceId, formatId)))
                .toList();
    }

    /**
     * Open a capture stream to a device with specific format requirements
     * <p>
     * Although the (internal) frame buffer format is set via the fourCC ID,
     * the frames returned by Cap_captureFrame are always 24-bit RGB.
     *
     * @param format the format requirements for the capture stream. The format must be supported by the device,
     *               i.e. it must be in the list of formats returned by getFormats().
     * @return a capture stream to the device with the given format requirements.
     */
    public CaptureStream openStream(CaptureFormat format) {
        int streamId = Cap_openStream(context.getSegment(), id, format.id());
        if (streamId == -1) {
            throw new CaptureException("Error opening capture stream: " + streamId);
        }
        return new CaptureStream(context, streamId);
    }

    /**
     * Get the name of a capture device.
     *
     * @param deviceId the device id of the capture device.
     * @return the name of the capture device.
     * @throws CaptureException if no device with the given id exists.
     */
    private String getDeviceName(int deviceId) {
        MemorySegment stringPointer = Cap_getDeviceName(context.getSegment(), deviceId);
        if (stringPointer.equals(MemorySegment.NULL)) {
            throw new CaptureException("Device id " + deviceId + " does not exist.");
        }
        return stringPointer.getString(0);
    }

    /**
     * Get the unique name of a capture device.
     * <p>
     * The string contains a unique concatenation of the device name and other parameters.
     * These parameters are platform dependent.
     *
     * @param deviceId the device id of the capture device.
     * @return the unique name of the capture device.
     * @throws CaptureException if no device with the given id exists.
     */
    private String getDeviceUniqueId(int deviceId) {
        MemorySegment stringPointer = Cap_getDeviceUniqueID(context.getSegment(), deviceId);
        if (stringPointer.equals(MemorySegment.NULL)) {
            throw new CaptureException("Device id " + deviceId + " does not exist.");
        }
        return stringPointer.getString(0);
    }

    /**
     * Returns the number of formats supported by a certain device.
     *
     * @param deviceId the device id of the capture device.
     * @return the number of formats supported by the capture device with the given id.
     * @throws CaptureException if no device with the given id exists.
     */
    private int getNumFormats(int deviceId) throws CaptureException {
        int result = Cap_getNumFormats(context.getSegment(), deviceId);
        if (result == -1) {
            throw new CaptureException("Device id " + deviceId + " does not exist.");
        }
        return result;
    }

    /**
     * Get the format information from a device.
     *
     * @param deviceId the device id of the capture device.
     * @param formatId the format id of the capture format.
     * @return the format information of the capture format with the given id on the capture device with the given id.
     * @throws CaptureException if no device with the given id exists, or if no format with the given id exists on the device.
     */
    private CaptureFormatInfo getFormatInfo(int deviceId, int formatId) throws CaptureException {
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment formatInfoSegment = CapFormatInfo.allocate(arena);
            int result = Cap_getFormatInfo(context.getSegment(), deviceId, formatId, formatInfoSegment);
            CaptureResult captureResult = CaptureResult.values()[result];
            if (captureResult == CaptureResult.OK) {
                return new CaptureFormatInfo(formatInfoSegment);
            }
            throw new CaptureException("Failed to get format info: " + result);
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public List<CaptureFormat> getFormats() {
        return formats;
    }
}
