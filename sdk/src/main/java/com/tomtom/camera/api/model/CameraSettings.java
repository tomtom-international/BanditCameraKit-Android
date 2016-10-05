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

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Model corresponding to Camera settings part of Settings API model. Provides Camera settings states.
 */
public interface CameraSettings {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({GPS_DISABLE, MIC_SWITCH, PICTURE_ROTATION, METERING, WHITE_BALANCE, VIDEO_STABILISATION, HEART_RATE_DISABLE})
    @interface Setting {}
    int GPS_DISABLE = 0;
    int MIC_SWITCH = 1;
    int PICTURE_ROTATION = 2;
    int METERING = 3;
    int WHITE_BALANCE = 4;
    int VIDEO_STABILISATION = 5;
    int HEART_RATE_DISABLE = 6;


    /**
     * Returns Camera's serial number as it's set up in the factory
     * @return Camera serial number as string
     */
    String getCameraSerialNumber();

    /**
     * Unique camera ID used for BLE pairing. Generated as CRC32(serial_number),
     * represented as hexadecimal value.
     * @return Camera BLE identification string
     */
    String getCameraBleId();

    /**
     * Random 8-byte alphanumeric sequence used for BLE authentication.
     * Includes characters from the pool (0-9)(a-z)(A-Z).
     * @return Camera BLE verification code
     */
    String getCameraBleVerificationCode();

    /**
     * 	Returns GNSS module state.
     * @return true (ON) or false (OFF)
     */
    Boolean isGpsEnabled();

    /**
     * Returns if sound is enabled on the camera
     * @return true (ON) or false (OFF)
     */
    Boolean isSoundEnabled();

    /**
     * Returns if light is enabled on the camera
     * @return true (ON) or false (OFF)
     */
    Boolean areLightsEnabled();

    /**
     * Returns if {@link Setting} is supported by concrete API version/implementation.
     * @param setting Setting integer defined by {@link Setting} IntDef.
     * @return true if supported, false if not
     */
    boolean isSettingSupported(@Setting int setting);

    /**
     * Returns if 180 degrees image rotation is enabled on the device
     * @return true (ON) or false (OFF)
     */
    Boolean isImageRotationEnabled();

    /**
     * Returns if external microphone is enabled on the device
     * @return true (ON) or false (OFF)
     */
    Boolean isExternalMicEnabled();

    /**
     * Returns camera's metering mode.
     * @return one of possible values: "center", "normal" and "wide".
     */
    String getMetering();

    /**
     * Returns current white balance mode setting.
     * @return one of possible values: "5000K" (daylight), "4000K" (fluorescent), "3000K" (tungsten) and "2000K" (candlelight).
     */
    String getWhiteBalanceMode();

    /**
     * Returns if video stabilisation is enabled on the device
     * @return true (ON) or false (OFF)
     */
    Boolean isVideoStabilisationEnabled();
}