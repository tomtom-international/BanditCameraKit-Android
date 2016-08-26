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
import com.tomtom.camera.api.model.CameraStatus;

import java.util.concurrent.TimeUnit;

/**
 * Concrete implementation of interface {@link CameraStatus}
 */

class CameraStatusV2 implements CameraStatus {

    public CameraStatusV2() {
    }

    @SerializedName("recording_active")
    boolean mIsRecordingActive;

    @SerializedName("recoding_secs")
    int mRecordingTimeSeconds;

    @SerializedName("battery_level_pct")
    int mBatteryLevel;

    @SerializedName("battery_charging")
    boolean mIsBatteryCharging;

    // V2
    @SerializedName("gnss_fix")
    boolean mGnssFix;

    // V2
    @SerializedName("gnss_strength_pct")
    int mGnssStrengthPct;

    // V2
    @SerializedName("heart_rate_sensor_connected")
    boolean mIsHeartRateSensorConnected;

    // V2
    @SerializedName("cadence_sensor_connected")
    boolean mIsCadenceSensorConnected;

    @SerializedName("preview_active")
    boolean mIsPreviewActive;

    @SerializedName("viewfinder_active")
    boolean mIsViewfinderActive;

    @SerializedName("viewfinder_streaming_port")
    int mViewFinderStreamingPort;

    @SerializedName("backchannel_port")
    int mBackchannelPort;

    @SerializedName("memory_free_bytes")
    long mMemoryFreeBytes;

    @SerializedName("remaining_time_secs")
    int mRemainingTime;

    @SerializedName("remaining_photos")
    int mRemainingPhotos;

    public boolean isRecording(){
        return mIsRecordingActive;
    }

    public long getRecordingTime(TimeUnit timeUnit) {
        if(mRecordingTimeSeconds < 0) {
            return mRecordingTimeSeconds;
        }

       return timeUnit.convert(mRecordingTimeSeconds, TimeUnit.SECONDS);
    }

    public boolean isBatteryCharging() {
        return mIsBatteryCharging;
    }

    public int getBatteryLevelPercentage() {
        return mBatteryLevel;
    }

    public boolean isPreviewActive() {
        return mIsPreviewActive;
    }

    public boolean isViewfinderActive() {
        return mIsViewfinderActive;
    }

    public boolean isGnssFixAvailable() {
        return mGnssFix;
    }

    public int getGnssStrengthPct() {
        return mGnssStrengthPct;
    }

    public boolean isHeartRateSensorConnected() {
        return mIsHeartRateSensorConnected;
    }

    public boolean isCadenceSensorConnected() {
        return mIsCadenceSensorConnected;
    }

    public int getViewFinderStreamingPort() {
        return mViewFinderStreamingPort;
    }

    public int getBackchannelPort() {
        return mBackchannelPort;
    }

    public long getMemoryFreeBytes() {
        return mMemoryFreeBytes;
    }

    public int getRemainingTime() {
        return mRemainingTime;
    }

    public int getRemainingPhotos() {
        return mRemainingPhotos;
    }
}
