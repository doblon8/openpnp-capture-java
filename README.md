# openpnp-capture-java

Java Foreign Function & Memory bindings for [openpnp-capture](https://github.com/openpnp/openpnp-capture).

## Installation

`openpnp-capture-java` is available from Maven Central.

To add `openpnp-capture-java` as a dependency using Maven, include the following in your `pom.xml`:

```xml
<dependency>
  <groupId>io.github.doblon8</groupId>
  <artifactId>openpnp-capture-java</artifactId>
  <version>0.0.1</version>
</dependency>
```

For other build tools, retrieve the dependency from Maven Central using the same coordinates.

`openpnp-capture-java` bundles the native `openpnp-capture` library for supported platforms, so no system-wide installation is required.

## Example

```java
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
```

## License

This project is licensed under the MIT License, the same as the `openpnp-capture` library.

See the [LICENSE](./LICENSE) file for details.
