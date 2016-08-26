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

import com.tomtom.camera.api.model.Capabilities;
import com.tomtom.camera.api.model.ImageCapabilities;
import com.tomtom.camera.api.model.SceneCapabilities;
import com.tomtom.camera.api.model.TranscodingCapabilities;
import com.tomtom.camera.api.model.VideoCapabilities;

/**
 * Concrete implementation of interface {@link Capabilities}
 */
class CapabilitiesV2 implements Capabilities {

    private RecordingCapabilitiesV2 mRecordingCapabilities;
    private TranscodingCapabilitiesV2 mTranscoderCapabilities;
    private SceneCapabilities mSceneCapabilities;

    CapabilitiesV2(RecordingCapabilitiesV2 recordingCapabilitiesV2, TranscodingCapabilitiesV2 transcoderCapabilitiesV2, SceneCapabilitiesV2 sceneCapabilitiesV2) {
        mRecordingCapabilities = recordingCapabilitiesV2;
        mTranscoderCapabilities = transcoderCapabilitiesV2;
        mSceneCapabilities = sceneCapabilitiesV2;
    }

    @Override
    public VideoCapabilities getVideoCapabilities() {
        return mRecordingCapabilities.getVideoCapabilities();
    }

    @Override
    public ImageCapabilities getImageCapabilities() {
        return mRecordingCapabilities.getImageCapabilities();
    }

    @Override
    public TranscodingCapabilities getTranscodingCapabilities() {
        return mTranscoderCapabilities;
    }

    @Override
    public SceneCapabilities getSceneCapabilities() {
        return mSceneCapabilities;
    }
}
