package com.tomtom.camera.api.model;

/**
 * Wrapper for Video and Image capabilities.
 */

public interface RecordingCapabilities {
    VideoCapabilities getVideoCapabilities();
    ImageCapabilities getImageCapabilities();
}
