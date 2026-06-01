import io.github.doblon8.openpnp.capture.OpenPnpCapture;

void main() {
    var version = OpenPnpCapture.getLibraryVersion();
    System.out.println("OpenPnP Capture Library Version: " + version);
}
