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

import com.tomtom.camera.api.model.capability.VideoModeSetting;

import java.util.ArrayList;

/**
 * This interface defines functions for accessing values from camera for video recording capabilities
 */
public interface VideoCapabilities {

    /**
     * Returns list of VideoModeSetting objects, which gives us access to possible resolution,
     * framerate, etc.., for specified video mode in input parameter
     * @param videoMode {@link Video.Mode} instance
     * @return list of {@link VideoModeSetting} for given video mode
     */
    ArrayList<VideoModeSetting> getSettings(Video.Mode videoMode);

    /**
     * Returns list of recording video modes, which camera supports
     * @return list of {@link Video.Mode}
     */
    ArrayList<Video.Mode> getSupportedModes();
}
