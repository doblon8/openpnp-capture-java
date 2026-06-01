package io.github.doblon8.openpnp.capture;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class NativeLibraryLoadingTest {

    @Test
    void shouldLoadNativeLibrary() {
        assertNotNull(OpenPnpCapture.getLibraryVersion());
    }
}