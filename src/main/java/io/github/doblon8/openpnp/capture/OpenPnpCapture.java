package io.github.doblon8.openpnp.capture;

import io.github.doblon8.openpnp.capture.bindings.CapCustomLogFunc;
import io.github.doblon8.openpnp.capture.bindings.openpnp_capture.Cap_getLibraryVersion;

import static io.github.doblon8.openpnp.capture.bindings.openpnp_capture.Cap_setLogLevel;

public class OpenPnpCapture {

    /**
     * Return the version of the library as a string.
     * <p>
     * In addition to a version number, this should contain information on the platform;
     * e.g. Win32/Win64/Linux32/Linux64/OSX etc., whether or not it is a release or debug build, and the build date.
     *
     * @return the version of the library as a string.
     */
    public static String getLibraryVersion() {
        var capGetLibraryVersion = Cap_getLibraryVersion.makeInvoker();
        return capGetLibraryVersion.apply().getString(0);
    }

    /**
     * Set the logging level.
     *
     * @param level the logging level to set. Log messages with a level below this will be ignored.
     */
    public static void setLogLevel(LogLevel level) {
        Cap_setLogLevel(level.ordinal());
    }
}
