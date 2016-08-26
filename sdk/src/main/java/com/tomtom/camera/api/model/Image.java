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

import java.util.HashMap;

/**
 * Model corresponding to Image model from Camera Api. Provides all information about image.
 */
public interface Image extends CameraFile {

    String GET_IMAGE_THUMBNAIL_RELATIVE_PATH = "images/{image_id}/thumb/";
    String IMAGE_CLASS_TYPE = "IMAGE_TYPE";

    /**
     * Image modes on the camera.
     */
    enum Mode {
        SINGLE("single"),
        BURST("burst"),
        CONTINUOUS("continuous"),
        UNKNOWN("Unknown");

        private String mVal;
        private static HashMap<String, Mode> sModeMap;


        Mode(String val) {
            mVal = val;
        }

        public String value() {
            return mVal;
        }

        static {
            sModeMap = new HashMap<String, Mode>();
            sModeMap.put("normal", SINGLE);
            sModeMap.put(SINGLE.value(), SINGLE);
            sModeMap.put(BURST.value(), BURST);
            sModeMap.put(CONTINUOUS.value(), CONTINUOUS);
        }

        public static Mode fromString(String value) {
            Image.Mode mode = sModeMap.get(value);
            if(mode == null){
                return UNKNOWN;
            }
            return mode;
        }
    }

    /**
     * Path of the image file on the camera.
     * @return Path to image in format: {@code '/'+<DCIM_folder>+'/'+<filename>}
     */
    String getPath();

    /**
     * Provides latitude in degrees
     * @return latitude in degrees, 0 if it's not saved for given image
     */
    float getLat();

    /**
     * Provides longitude in degrees
     * @return longitude in degrees, 0 if it's not saved for given image
     */
    float getLon();

    /**
     * Returns path to Highlight thumbnail bitmap. It's relative to Camera API base url.
     * @return Path to thumbnail relative to base url.
     */
    String getThumbnailUrl();

    /**
     * Provides mode used to record image
     */
    Image.Mode getMode();

    /**
     * Provides captured image resolution
     */
    String getResolution();
}
