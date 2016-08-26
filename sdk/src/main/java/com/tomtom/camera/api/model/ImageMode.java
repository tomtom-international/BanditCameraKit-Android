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
 * Describes single image mode. Used for setting and getting current image mode.
 */
public interface ImageMode {

    /**
     * Provides {@link com.tomtom.camera.api.model.Image.Mode}
     * @return {@link com.tomtom.camera.api.model.Image.Mode} enum value of current image mode
     */
    Image.Mode getMode();

    /**
     * Provides image mode resolution
     * @return String representing image mode resolution
     */
    String getResolution();

    /**
     * Provides interval between two shots in seconds, if mode supports
     * @return interval of seconds if supported, otherwise {@code null}
     */
    Integer getIntervalSecs();

    /**
     * Provides duration of shooting in seconds, if mode supports
     * @return duration in seconds if supported, otherwise {@code null}
     */
    Integer getDurationSecs();

    /**
     * Provides number of shots being taken, if mode supports
     * @return number of shots if supported, otherwise {@code null}
     */
    Integer getCount();
}
