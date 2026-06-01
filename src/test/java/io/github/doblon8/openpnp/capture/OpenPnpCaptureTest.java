package io.github.doblon8.openpnp.capture;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OpenPnpCaptureTest {

    @Test
    void getLibraryVersion() {
        assertNotNull(OpenPnpCapture.getLibraryVersion());
    }
}