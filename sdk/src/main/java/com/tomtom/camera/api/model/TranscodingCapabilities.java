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

import com.google.common.collect.ArrayListMultimap;
import com.tomtom.camera.api.model.capability.VideoQualitySetting;

import java.util.List;

/**
 * This interface implements functions for retrieving possible VideoQualitySetting after transcoding
 *  of video for given input VideoQualitySetting
 */
public interface TranscodingCapabilities {

    /**
     * Returns list of possible output VideoQualitySetting after tarnscoding of video for given input
     *  VideoQualitySetting
     * @param inputVideoQualitySetting input {@link VideoQualitySetting}
     * @return list of possible output {@link VideoQualitySetting} for given {@code inputVideoQualitySetting}
     */
    List<VideoQualitySetting> getPossibleOutputVideoQualitySettings(VideoQualitySetting inputVideoQualitySetting);

    /**
     *  Returns list of possible input and output VideoQualitySetting combinations for video transcoding
     *
     * @return multimap list of possible combinations for input and output {@link VideoQualitySetting}
     */
    ArrayListMultimap<VideoQualitySetting, VideoQualitySetting> getInputOutputVideoQualitySettingMultimap();
}
