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

import java.util.concurrent.TimeUnit;

/**
 * Encapsulates multiple camera statuses.
 */
public interface CameraStatus {

    /**
     * 	Returns camera recording status.
     * @return true (Recording) or false (Not recording)
     */
    boolean isRecording();

    /**
     * Returns camera recording time in given timeunit.
     * @param timeUnit the unit of the {@code timeUnit} argument
     * @return Time in given TimeUnit if recording, -1 if not recording
     */
    long getRecordingTime(TimeUnit timeUnit);

    /**
     * Provides information if camera (battery) is charging
     * @return true if charging, false if not
     */
    boolean isBatteryCharging();

    /**
     * Provides information about current camera battery level
     * @return value of battery level in percents
     */
    int getBatteryLevelPercentage();

    /**
     * Provides information if preview is currently active.
     * @return true if active, false if not
     */
    boolean isPreviewActive();

    /**
     * Provides information if viewfinder is currently active.
     * @return true if active, false if not
     */
    boolean isViewfinderActive();

    /**
     * Provides information about GNSS fix.
     * @return true if it is obtained, false if no fix
     */
    boolean isGnssFixAvailable();

    /**
     * Provides information about GNSS signal strength
     * @return Signal strentgh percentage (0-100)
     */
    int getGnssStrengthPct();

    /**
     * Provides information about connected HR sensor
     * @return true if connected, false if not
     */
    boolean isHeartRateSensorConnected();

    /**
     * Provides information about connected cadence sensor
     * @return true if connected, false if not
     */
    boolean isCadenceSensorConnected();

    /**
     * Provides viewfinder streaming port number
     * @return port number
     */
    int getViewFinderStreamingPort();

    /**
     * Provides backchannel notification port number
     * @return port number
     */
    int getBackchannelPort();

    /**
     * Provides information about remaining free space on SD card
     * @return available space on SD card in bytes
     */
    long getMemoryFreeBytes();

    /**
     * Provides information about remaining video recording time based on the currently selected
     * (or last remembered) video mode and it's parameters.
     * @return remaining recording time in seconds.
     */
    int getRemainingTime();

    /**
     * Provides information about remaining number of photos based on the currently selected
     * (or last remembered) image mode and it's parameters.
     * @return number of remaining photos
     */
    int getRemainingPhotos();
}