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
     * Get the name of a capture device.
     * <p>
     * If a device with the given id does not exist, null is returned.
     *
     * @param deviceId the device id of the capture device.
     * @return the name of the capture device.
     * @throws CaptureException if no device with the given id exists.
     */
    public String getDeviceName(int deviceId) {
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
     * <p>
     * If a device with the given id does not exist, null is returned.
     *
     * @param deviceId the device id of the capture device
     * @return the unique name of the capture device.
     * @throws CaptureException if no device with the given id exists.
     */
    public String getDeviceUniqueId(int deviceId) {
        MemorySegment stringPointer = Cap_getDeviceUniqueID(context.getSegment(), deviceId);
        if (stringPointer.equals(MemorySegment.NULL)) {
            throw new CaptureException("Device id " + deviceId + " does not exist.");
        }
        return stringPointer.getString(0);
    }

    /**
     * Returns the number of formats supported by a certain device.
     *
     * @param deviceId the device id of the capture device
     * @return the number of formats supported by the capture device with the given id.
     * @throws CaptureException if no device with the given id exists.
     */
    public int getNumFormats(int deviceId) throws CaptureException {
        int result = Cap_getNumFormats(context.getSegment(), deviceId);
        if (result == -1) {
            throw new CaptureException("Device id " + deviceId + " does not exist.");
        }
        return result;
    }

    public CaptureFormatInfo getFormatInfo(int deviceId, int formatId) throws CaptureException {
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
