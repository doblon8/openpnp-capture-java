package io.github.doblon8.openpnp.capture;

import io.github.doblon8.openpnp.capture.bindings.CapCustomLogFunc;
import io.github.doblon8.openpnp.capture.bindings.CapFormatInfo;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import static io.github.doblon8.openpnp.capture.bindings.openpnp_capture.*;

public class OpenPnpCapture implements AutoCloseable {

    private final CaptureContext context;
    private static final Arena arena = Arena.ofAuto();

    public OpenPnpCapture() {
        this.context = new CaptureContext();
    }

    /**
     * Get a list of capture devices on the system.
     *
     * @return a list of capture devices on the system.
     */
    public List<CaptureDevice> getDevices() {
        int deviceCount = getDeviceCount();
        List<CaptureDevice> devices = new ArrayList<>(deviceCount);
        for (int i = 0; i < deviceCount; i++) {
            String deviceName = getDeviceName(i);
            String deviceUniqueId = getDeviceUniqueId(i);
            devices.add(new CaptureDevice(i, deviceName, deviceUniqueId));
        }
        return devices;
    }

    /**
     * Get the number of capture devices on the system.
     * <p>
     * Note: this can change dynamically due to the plugging and unplugging of USB devices.
     *
     * @return the number of capture devices found.
     */
    public int getDeviceCount() {
        return Cap_getDeviceCount(context.getSegment());
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

    /**
     * Return the version of the library as a string.
     * <p>
     * In addition to a version number, this should contain information on the platform;
     * e.g. Win32/Win64/Linux32/Linux64/OSX etc., whether or not it is a release or debug build, and the build date.
     *
     * @return the version of the library as a string.
     */
    public static String getLibraryVersion() {
        return Cap_getLibraryVersion().getString(0);
    }

    /**
     * Set the logging level.
     *
     * @param level the logging level to set. Log messages with a level below this will be ignored.
     */
    public static void setLogLevel(LogLevel level) {
        Cap_setLogLevel(level.ordinal());
    }

    /**
     * Install a custom callback for logging function
     *
     * @param logFunction a function that takes a LogLevel and a String message,
     *                    which will be called for each log message generated by the library.
     */
    public static void installCustomLogFunction(BiConsumer<LogLevel, String> logFunction) {
        MemorySegment functionPointer = CapCustomLogFunc.allocate((level, stringPointer) ->
                logFunction.accept(LogLevel.values()[level], stringPointer.getString(0)), arena);
        Cap_installCustomLogFunction(functionPointer);
    }

    @Override
    public void close() throws CaptureException {
        context.close();
    }
}
