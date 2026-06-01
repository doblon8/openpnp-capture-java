package io.github.doblon8.openpnp.capture;

import io.github.doblon8.openpnp.capture.bindings.openpnp_capture$shared;
import org.junit.jupiter.api.Test;

import java.lang.foreign.ValueLayout;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NativeLongTypeTest {

    @Test
    void shouldMatchPlatformLayout() {
        var layout = openpnp_capture$shared.C_LONG;

        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            assertEquals(ValueLayout.JAVA_INT, layout);
        } else {
            assertEquals(ValueLayout.JAVA_LONG, layout);
        }
    }
}
