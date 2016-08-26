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
import com.tomtom.camera.api.model.Preview;
/**
 * Concrete implementation of interface {@link Preview}
 */
class PreviewV2 implements Preview {

    public static final String TAG = "Preview";

    @SerializedName("id")
    String mVideoId;

    @SerializedName("preview_active")
    boolean mIsActive;

    @SerializedName("preview_port")
    int mPreviewPort;

    @SerializedName("offset_secs")
    Float mStartPositionSecs;

    @SerializedName("length_secs")
    Float mLengthSecs;

    public PreviewV2() {

    }

    public PreviewV2(boolean isPreviewActive, Float startPosition, Float length, String videoId, int previewPort) {
        mIsActive = isPreviewActive;
        mStartPositionSecs = startPosition;
        mLengthSecs = length;
        mVideoId = videoId;
        mPreviewPort = previewPort;
    }

    public PreviewV2(boolean isPreviewActive, int previewPort, String videoId) {
        this(isPreviewActive, null, null, videoId, previewPort);
    }

    public void setVideoId(String videoId) {
        mVideoId = videoId;
    }

    public String getVideoId() {
        return mVideoId;
    }

    public boolean isActive() {
        return mIsActive;
    }


    public Float getStartPositionSecs() {
        if (mStartPositionSecs != null) {
            return mStartPositionSecs;
        }
        return 0f;
    }

    public int getPreviewPort() {
        return mPreviewPort;
    }

    public Float getDurationSecs() {
        if (mLengthSecs != null) {
            return mLengthSecs;
        }
        return 0f;
    }

    public void setLength(float length) {
        mLengthSecs = length;
    }
}
