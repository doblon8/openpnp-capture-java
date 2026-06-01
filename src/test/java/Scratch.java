import io.github.doblon8.openpnp.capture.*;

import javax.imageio.ImageIO;

import static io.github.doblon8.openpnp.capture.OpenPnpCapture.*;

void main() throws Exception {
    System.out.println("openpnp-capture version: " + OpenPnpCapture.getLibraryVersion());

    setLogLevel(LogLevel.INFO);
    installCustomLogFunction((logLevel, message) -> System.out.println("Log [" + logLevel + "]: " + message));

    try (var capture = new OpenPnpCapture()) {
        var device = capture.getDevices().stream()
                .findFirst()
                .orElseThrow(() -> new CaptureException("No capture devices found."));

        var format = device.getFormats().stream()
                .findFirst()
                .orElseThrow(() -> new CaptureException("No formats found."));

        try (var stream = device.openStream(format)) {
            TimeUnit.SECONDS.sleep(1); // let the camera initialize

            var zoomLimits = stream.getPropertyLimits(CaptureProperty.ZOOM);
            System.out.println("Zoom limits: " + zoomLimits);

            var image = stream.capture();
            ImageIO.write(image, "png", new File("/tmp/image.png"));
            System.out.println("Captured image saved to /tmp/image.png");
        }
    }
}
