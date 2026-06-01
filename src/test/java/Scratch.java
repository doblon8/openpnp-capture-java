import io.github.doblon8.openpnp.capture.CaptureException;
import io.github.doblon8.openpnp.capture.CaptureProperty;
import io.github.doblon8.openpnp.capture.LogLevel;
import io.github.doblon8.openpnp.capture.OpenPnpCapture;

import javax.imageio.ImageIO;
import java.awt.*;

import static io.github.doblon8.openpnp.capture.OpenPnpCapture.installCustomLogFunction;
import static io.github.doblon8.openpnp.capture.OpenPnpCapture.setLogLevel;

void main() {
    var version = OpenPnpCapture.getLibraryVersion();
    System.out.println("OpenPnP Capture Library Version: " + version);

    setLogLevel(LogLevel.DEBUG);
    installCustomLogFunction((logLevel, message) -> System.out.println("Custom Log [" + logLevel + "]: " + message));

    try (var capture = new OpenPnpCapture()) {
        // No checking if devices and format are available, just get the first ones for testing
        var device = capture.getDevices().getFirst();
        var format = device.getFormats().getFirst();
        System.out.println("Using device '" + device.getName() + "' with format " + format);

        try (var stream = device.openStream(format)) {
            System.out.println("Stream is open: " + stream.isOpen());
            TimeUnit.SECONDS.sleep(1); // wait a bit to get the webcam ready

            System.out.println("Zoom property limits: " + stream.getPropertyLimits(CaptureProperty.ZOOM));
            System.out.println("Current zoom value: " + stream.getPropertyValue(CaptureProperty.ZOOM));
            stream.setProperty(CaptureProperty.ZOOM, 200);
            System.out.println("Zoom value after setting to 200: " + stream.getPropertyValue(CaptureProperty.ZOOM));


            var image = stream.capture();
            ImageIO.write(image, "png", new File("/tmp/image.png"));
            System.out.println("Captured image saved to /tmp/image.png");
        } catch (InterruptedException e) {
            System.err.println("Interrupted while waiting for webcam: " + e.getMessage());
        }
    } catch (CaptureException e) {
        System.err.println("Error using CaptureContext: " + e.getMessage());
    } catch (IOException e) {
        System.err.println("Error saving image: " + e.getMessage());
    }
}
