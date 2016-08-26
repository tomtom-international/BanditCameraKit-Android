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
import com.google.common.collect.ListMultimap;
import com.tomtom.camera.api.model.Image;
import com.tomtom.camera.api.model.ImageCapabilities;
import com.tomtom.camera.api.model.capability.ImageModeSetting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Concrete implementation of interface {@link ImageCapabilities}
 */
class ImageCapabilitiesV2 implements ImageCapabilities {

    public static final String TAG = "ImageCapabilities";

    ListMultimap<Image.Mode, ImageModeV2> mImageCapabilitiesV2;

    HashMap<Image.Mode, ArrayList<ImageModeSetting>> mImageModeSettingsMap;

    ImageCapabilitiesV2(ArrayList<ImageModeV2> imageCapabilityItems) {
        mImageCapabilitiesV2 = ArrayListMultimap.create();
        mImageModeSettingsMap = new HashMap<>();
        initImageModeSettings(imageCapabilityItems);

    }

    private void initImageModeSettings(ArrayList<ImageModeV2> imageCapabilityItems) {
        for(ImageModeV2 itemV2 : imageCapabilityItems) {
            mImageCapabilitiesV2.put(itemV2.getMode(), itemV2);
        }
        for(Image.Mode mode : mImageCapabilitiesV2.keySet()) {
            List<ImageModeV2> modeItems = mImageCapabilitiesV2.get(mode);
            int size = modeItems.size();
            ArrayList<ImageModeSetting> imageModeSettings = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                ImageModeV2 imageMode = modeItems.get(i);
                ImageModeSetting imageModeSetting = new ImageModeSetting(imageMode.getResolution(), imageMode.getDurationSecs(), imageMode.getCount(), imageMode.getIntervalSecs());
                imageModeSettings.add(imageModeSetting);
            }
            mImageModeSettingsMap.put(mode, imageModeSettings);
        }
    }

    public ArrayList<ImageModeSetting> getModeSettings(Image.Mode imageMode) {
        return new ArrayList<>(mImageModeSettingsMap.get(imageMode));
    }

    public ArrayList<Image.Mode> getSupportedModes() {
        return new ArrayList<Image.Mode>(mImageModeSettingsMap.keySet());
    }
}
