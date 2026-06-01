import io.github.doblon8.openpnp.capture.CaptureException;
import io.github.doblon8.openpnp.capture.LogLevel;
import io.github.doblon8.openpnp.capture.OpenPnpCapture;

import static io.github.doblon8.openpnp.capture.OpenPnpCapture.installCustomLogFunction;
import static io.github.doblon8.openpnp.capture.OpenPnpCapture.setLogLevel;

void main() {
    var version = OpenPnpCapture.getLibraryVersion();
    System.out.println("OpenPnP Capture Library Version: " + version);

    setLogLevel(LogLevel.VERBOSE);
    installCustomLogFunction((logLevel, message) -> System.out.println("Custom Log [" + logLevel + "]: " + message));

    try (var capture = new OpenPnpCapture()) {
        var devices = capture.getDevices();
        for (var device : devices) {
            System.out.println("Device id: " + device.id());
            System.out.println("Device name: " + device.name());
            System.out.println("Device unique id: " + device.uniqueId());
            var numFormats = capture.getNumFormats(device.id());
            System.out.println("Number of formats: " + numFormats);
            var firstFormatInfo = capture.getFormatInfo(device.id(), 0);
            System.out.println("Capture format info: " + firstFormatInfo);
        }
    } catch (CaptureException e) {
        System.err.println("Error using CaptureContext: " + e.getMessage());
    }
}
