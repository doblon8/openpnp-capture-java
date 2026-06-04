import io.github.doblon8.openpnp.capture.*;

import javax.imageio.ImageIO;

void main() throws Exception {
    System.out.println("openpnp-capture version: " + OpenPnpCapture.getLibraryVersion());

    OpenPnpCapture.setLogLevel(LogLevel.INFO);
    OpenPnpCapture.installCustomLogFunction((logLevel, message) -> System.out.println("Log [" + logLevel + "]: " + message));

    try (var capture = new OpenPnpCapture()) {
        var device = capture.getDevices().stream()
                .findFirst()
                .orElseThrow(() -> new CaptureException("No capture devices found."));

        var format = device.getFormats().stream()
                .findFirst()
                .orElseThrow(() -> new CaptureException("No formats found."));

        try (var stream = device.openStream(format)) {
            TimeUnit.SECONDS.sleep(1); // let the camera initialize

            var exposureLimits = stream.getPropertyLimits(CaptureProperty.EXPOSURE);
            System.out.println("Exposure limits: " + exposureLimits);

            var image = stream.capture();
            ImageIO.write(image, "png", new File("/tmp/image.png"));
            System.out.println("Captured image saved to /tmp/image.png");
        }
    }
}
