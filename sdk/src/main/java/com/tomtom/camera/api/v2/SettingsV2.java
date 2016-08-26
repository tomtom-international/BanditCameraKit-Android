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
import com.tomtom.camera.api.model.ImageMode;
import com.tomtom.camera.api.model.Scene;
import com.tomtom.camera.api.model.Settings;
import com.tomtom.camera.api.model.VideoMode;
import com.tomtom.camera.api.model.WifiSettings;

/**
 * Concrete implementation of interface {@link Settings}
 */
class SettingsV2 implements Settings {

    @SerializedName("camera")
    CameraSettingsV2 mCameraSettings;

    @SerializedName("wifi")
    WifiSettingsV2 mWifiSettings;

    @SerializedName("video")
    VideoModeV2 mVideoMode;

    @SerializedName("image")
    ImageModeV2 mImageMode;

    @SerializedName("scene")
    SceneV2 mScene;

    SettingsV2(CameraSettingsV2 cameraSettings) {
        mCameraSettings = cameraSettings;
    }

    SettingsV2(VideoModeV2 videoMode) {
        mVideoMode = videoMode;
    }

    SettingsV2(ImageModeV2 imageMode) {
        mImageMode = imageMode;
    }

    @Override
    public CameraSettings getCameraSettings() {
        return mCameraSettings;
    }

    @Override
    public WifiSettings getWifiSettings() {
        return mWifiSettings;
    }

    @Override
    public VideoMode getVideoMode() {
        return mVideoMode;
    }

    @Override
    public ImageMode getImageMode() {
        return mImageMode;
    }

    @Override
    public Scene getSceneSettings() {
        return mScene;
    }
}
