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


import com.tomtom.camera.api.model.capability.Framerate;
import com.tomtom.camera.api.model.capability.Resolution;

import java.util.HashMap;
import java.util.List;

/**
 * This interface correspondents to video model and defines functions for accessing model fields
 */
public interface Video extends CameraFile, Playable {

    String GET_VIDEO_THUMBNAIL_RELATIVE_PATH = "videos/{video_id}/thumb/";
    String CLASS_TYPE = "class_type";

    /**
     * Sets video id, which uniquely identifies video
     * @param  videoId String id of video
     */
    void setVideoId(String videoId);

    /**
     * Returns video id, which uniquely identifies video
     * @return String video id
     */
    String getVideoId();

    /**
     * Returns video file size in bytes
     * @return long value of video file size
     */
    long getSize();

    /**
     * Returns video duration in seconds
     * @return float value of video duration
     */
    float getDurationSecs();

    /**
     * Returns path of the file on camera
     * @return String
     */
    String getFilePathOnCamera();

    /**
     * Returns number of highlights
     * @return int number of highlights
     */
    int getNumberOfHighlights();

    /**
     * Sets number of highlights in video
     * @param numberOfHighlights {@link Mode}
     */
    void setNumberOfHighlights(int numberOfHighlights);

    /**
     * Returns value of video aspect ratio
     * @return String aspect ratio
     */
    String getRatio();

    /**
     * Returns mode of video
     * @return {@link Mode}
     */
    Mode getMode();

    /**
     * Returns true if video is muted
     * @return true if muted, false otherwise
     */
    boolean isMuted();

    /**
     * Returns resolution of video.
     * @return {@link Resolution} .
     */
    Resolution getResolution();

    /**
     * Returns framerate value of video.
     * @return {@link Framerate} .
     */
    Framerate getFramerate();

    /**
     * Returns list of highlights, which are tagged for this video.
     * @return list of {@link Highlight}
     */
    List<Highlight> getHighlights();

    /**
     * Sets list of highlights, which are tagged for this video.
     * @param highlights list of {@link Highlight}
     */
    void setHighlights(List<Highlight> highlights);

    /**
     *  Increments number of highlights in video
     */
    void incrementNumberOfHighlights();

    /**
     *  Decrement number of highlights in video
     */
    void decrementNumberOfHighlights();

    /**
     * Returns path of the thumbnail on camera
     * @return String
     */
    String getThumbnailUrl();

    /**
     * Enum which corresponds to available camera video modes.
     */

    enum Mode {
        NORMAL("normal"),
        CINEMATIC("cinematic"),
        SLOW_MOTION("slow_motion"),
        TIME_LAPSE("time_lapse"),
        NIGHT_LAPSE("night_lapse"),
        UNKNOWN("unknown");


        private static HashMap<String, Mode> sModeMap;
        private String mVal;

        static {
            sModeMap = new HashMap<String, Mode>();
            sModeMap.put(NORMAL.value(), NORMAL);
            sModeMap.put(CINEMATIC.value(), CINEMATIC);
            sModeMap.put(SLOW_MOTION.value(), SLOW_MOTION);
            sModeMap.put(TIME_LAPSE.value(), TIME_LAPSE);
            sModeMap.put(NIGHT_LAPSE.value(), NIGHT_LAPSE);
        }

        Mode(String val) {
            mVal = val;
        }

        /**
         * Returns value as {@link String}.
         * @return String representation of {@link Mode}
         */
        public String value(){
            return mVal;
        }

        /**
         * Returns {@link Mode} based on provided String value.
         * @param value String representation of {@link Mode}
         * @return {@link Mode} instance if it can be found in map, UNKNOWN if not.
         */
        public static Mode fromString(String value) {
            Mode mode = sModeMap.get(value);
            if(mode != null) {
                return mode;
            }
            return UNKNOWN;
        }
    }
}
