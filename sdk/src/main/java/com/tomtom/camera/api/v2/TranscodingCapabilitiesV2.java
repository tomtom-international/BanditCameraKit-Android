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

import com.google.common.collect.ArrayListMultimap;
import com.google.gson.annotations.SerializedName;
import com.tomtom.camera.api.model.TranscodingCapabilities;
import com.tomtom.camera.api.model.capability.VideoQualitySetting;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete implementation of interface {@link TranscodingCapabilities}
 */
class TranscodingCapabilitiesV2 implements TranscodingCapabilities {

    @SerializedName("inputoutput_pairs")
    ArrayListMultimap<VideoQualitySetting, VideoQualitySetting> mInputOutputVideoQualityMultimap;

    TranscodingCapabilitiesV2(ArrayList<TranscodingCapabilityV2> transcodingCapabilities) {
        mInputOutputVideoQualityMultimap = ArrayListMultimap.create();

        for(TranscodingCapabilityV2 transcodingCapabilityV2 : transcodingCapabilities) {
            mInputOutputVideoQualityMultimap.put(new VideoQualitySetting(transcodingCapabilityV2.getInputResolution(), transcodingCapabilityV2.getInputFramerate()),
                    new VideoQualitySetting(transcodingCapabilityV2.getOutputResolution(), transcodingCapabilityV2.getOutputFramerate()));
        }
    }

    @Override
    public List<VideoQualitySetting> getPossibleOutputVideoQualitySettings(VideoQualitySetting inputVideoQualitySetting) {
        return mInputOutputVideoQualityMultimap.get(inputVideoQualitySetting);
    }

    @Override
    public ArrayListMultimap<VideoQualitySetting, VideoQualitySetting> getInputOutputVideoQualitySettingMultimap() {
        return mInputOutputVideoQualityMultimap;
    }
}
