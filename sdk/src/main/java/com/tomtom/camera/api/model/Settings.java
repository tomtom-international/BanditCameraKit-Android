/*
 * Copyright (C) 2012-2016. TomTom International BV (http://tomtom.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tomtom.camera.api.model;

/**
 * This interface defines functions for reaching different types of settings
 */
public interface Settings {

    /**
     * Returns {@link CameraSettings} object, which give as possibility to access part of settings
     * for camera configuration (camera serial number, is gps enabled, is microphone enabled, etc. )
     * @return Object which represents {@link CameraSettings}
     */
    CameraSettings getCameraSettings();

    /**
     * Returns {@link WifiSettings} object, which give as possibility to retrieve SSID of
     * the camera's access point
     * @return Object which represents {@link WifiSettings}
     */
    WifiSettings getWifiSettings();

    /**
     * Returns {@link VideoMode} object, which give as possibility to access part of settings
     * regarding current video mode,field of view, resolution etc...
     * @return Object which represents {@link VideoMode} , if it can be found, null if not.
     */
    VideoMode getVideoMode();

    /**
     * Returns {@link ImageMode} object, which give as possibility to access part of settings
     * regarding current image mode, resolution etc...
     * @return Object which represents {@link ImageMode}, if it can be found, null if not.
     */
    ImageMode getImageMode();

    /**
     * Returns {@link Scene} object, which give as possibility to access part of settings
     * regarding current scene mode, hue, brightness,saturation, etc...
     * @return Object which represents {@link Scene}
     */
    Scene getSceneSettings();
}
