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
import com.tomtom.camera.api.model.ImageCapabilities;
import com.tomtom.camera.api.model.RecordingCapabilities;
import com.tomtom.camera.api.model.VideoCapabilities;

import java.util.ArrayList;

/**
 * Concrete implementation of interface {@link RecordingCapabilities}
 */
class RecordingCapabilitiesV2 implements RecordingCapabilities {

    @SerializedName("video")
    ArrayList<VideoModeV2> mVideoModeList;

    @SerializedName("image")
    ArrayList<ImageModeV2> mImageModeList;


    @Override
    public VideoCapabilities getVideoCapabilities() {
        return new VideoCapabilitiesV2(mVideoModeList);
    }

    @Override
    public ImageCapabilities getImageCapabilities() {
        return new ImageCapabilitiesV2(mImageModeList);
    }
}