import io.github.doblon8.openpnp.capture.CaptureContext;
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

    try (var context = new CaptureContext()) {
        System.out.println("CaptureContext created successfully.");
    } catch (CaptureException e) {
        System.err.println("Error using CaptureContext: " + e.getMessage());
    }
}
