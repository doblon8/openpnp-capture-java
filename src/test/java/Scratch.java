import io.github.doblon8.openpnp.capture.CaptureException;
import io.github.doblon8.openpnp.capture.LogLevel;
import io.github.doblon8.openpnp.capture.OpenPnpCapture;

import static io.github.doblon8.openpnp.capture.OpenPnpCapture.installCustomLogFunction;
import static io.github.doblon8.openpnp.capture.OpenPnpCapture.setLogLevel;

void main() {
    var version = OpenPnpCapture.getLibraryVersion();
    System.out.println("OpenPnP Capture Library Version: " + version);

    setLogLevel(LogLevel.DEBUG);
    installCustomLogFunction((logLevel, message) -> System.out.println("Custom Log [" + logLevel + "]: " + message));

    try (var capture = new OpenPnpCapture()) {
        var devices = capture.getDevices();
        for (var device : devices) {
            System.out.println("Device ID: " + device.id() + ", Name: " + device.name() + ", Unique ID: " + device.uniqueId());
            System.out.println("-----");
            var formats = device.formats();
            for (var format : formats) {
                System.out.println(format);
            }
        }
    } catch (CaptureException e) {
        System.err.println("Error using CaptureContext: " + e.getMessage());
    }
}
