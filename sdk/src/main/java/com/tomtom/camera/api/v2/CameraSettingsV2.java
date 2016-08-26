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

package com.tomtom.camera.api.v2;

import com.google.gson.annotations.SerializedName;
import com.tomtom.camera.api.model.CameraSettings;

/**
 * Concrete implementation of interface {@link CameraSettings}
 */
class CameraSettingsV2 implements CameraSettings {

    @SerializedName("serial_number")
    String mCameraSerialNumber;

    @SerializedName("ble_camera_id")
    String mCameraBleId;

    @SerializedName("ble_verification_code")
    String mCameraBleVerificationCode;

    @SerializedName("gnss_enabled")
    Boolean mGpsEnabled;

    @SerializedName("sound_enabled")
    Boolean mSoundEnabled;

    @SerializedName("lights_enabled")
    Boolean mLightsEnabled;

    @SerializedName("image_rotation_enabled")
    Boolean mImageRotationEnabled;

    @SerializedName("external_microphone_enabled")
    Boolean mExternalMicEnabled;

    @SerializedName("metering")
    String mMetering;

    @SerializedName("white_balance_mode")
    String mWhiteBalanceMode;

    @SerializedName("video_stabilisation_enabled")
    Boolean mVideoStabilisationEnabled;

    CameraSettingsV2() {

    }

    CameraSettingsV2(CameraSettings cameraSettings) {
        mGpsEnabled = cameraSettings.isGpsEnabled();
        mSoundEnabled = cameraSettings.isSoundEnabled();
    }

    public String getCameraSerialNumber() {
        return mCameraSerialNumber;
    }

    public String getCameraBleId() {
        return mCameraBleId;
    }

    public String getCameraBleVerificationCode() {
        return mCameraBleVerificationCode;
    }

    public Boolean isGpsEnabled() {
        return mGpsEnabled;
    }

    public Boolean isSoundEnabled() {
        return mSoundEnabled;
    }

    public Boolean areLightsEnabled() {
        return mLightsEnabled;
    }

    @Override
    public Boolean isImageRotationEnabled() {
        return mImageRotationEnabled;
    }

    @Override
    public Boolean isExternalMicEnabled() {
        return mExternalMicEnabled;
    }

    @Override
    public String getMetering() {
        return mMetering;
    }

    @Override
    public String getWhiteBalanceMode() {
        return mWhiteBalanceMode;
    }

    @Override
    public Boolean isVideoStabilisationEnabled() {
        return mVideoStabilisationEnabled;
    }

    @Override
    public boolean isSettingSupported(@Setting int setting) {
        switch(setting){
            case GPS_DISABLE:
            case MIC_SWITCH:
            case PICTURE_ROTATION:
                return true;
            default:
                return false;
        }
    }

    static class Builder {

        private CameraSettingsV2 mInstance;

        Builder() {
            mInstance = new CameraSettingsV2();
        }

        Builder gpsEnabled(boolean isEnabled) {
            mInstance.mGpsEnabled = isEnabled;
            return this;
        }

        Builder rotationEnabled(boolean isEnabled) {
            mInstance.mImageRotationEnabled = isEnabled;
            return this;
        }

        Builder externalMic(boolean isEnabled) {
            mInstance.mExternalMicEnabled = isEnabled;
            return this;
        }

        CameraSettingsV2 build() {
            return mInstance;
        }


    }
}
