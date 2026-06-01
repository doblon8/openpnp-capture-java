package io.github.doblon8.openpnp.capture;


import java.util.List;

public record CaptureDevice(int id, String name, String uniqueId, List<CaptureFormat> formats) {
}
