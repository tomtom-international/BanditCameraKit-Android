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

/**
 * Model for Highlight on the camera. It defines part of the video.
 */
public interface Highlight extends CameraFile, Playable {

    String GET_TAG_THUMBNAIL_RELATIVE_PATH = "videos/{video_id}/tags/{tag_id}/thumb/";
    String HIGHLIGHT_TYPE = "class_type";

    /**
     * Possible types of Highlight. Manual ones are from button, mobile or remote while the rest are
     * automaticaly created while recording.
     */
    enum Type {
        TAG_BUTTON("tag_button"),
        TAG_MOBILE("tag_mobile"),
        TAG_REMOTE("tag_remote"),
        TAG_MAX_SPEED("max_speed_mps"),
        TAG_MAX_HEART_RATE("max_heartrate_bpm"),
        TAG_ACCELERATION("max_acceleration"),
        TAG_DECELERATION("max_deceleration"),
        TAG_MAX_VERTICAL_SPEED("max_vertical_speed_mps"),
        TAG_MAX_G_FORCE("max_g_force"),
        TAG_MAX_ROTATION("max_rotation"),
        TAG_UNKNOWN("unknown");

        private String mHighlightTypeName;

        Type(String tagTypeName) {
            mHighlightTypeName = tagTypeName;
        }

        public String value() {
            return mHighlightTypeName;
        }
    }

    /**
     * Provides unique id for Highlight
     * @return Highlight UUID as String
     */
    String getId();

    /**
     * Provides parent {@link Video} for given highlight.
     * @return Parent {@link Video}
     */
    Video getVideo();

    /**
     * Sets parent video for Highlight
     * @param video parent Video file
     */
    void setVideo(Video video);

    /**
     * Returns value for Highlight. Each type expects different value.
     * @return highlight value
     */
    Object getValue();

    /**
     * Set value for Highlight.
     * @param value Value aligned with type.
     */
    void setValue(Object value);

    /**
     * Sets {@link SensorDataCollection} for given Highlight
     * @param sensorDataCollection sensor data collection
     */
    void setSensorDataCollection(SensorDataCollection sensorDataCollection);

    /**
     * Provides {@link SensorDataCollection} for given Highlight
     * @return SensorDataCollection for given Highlight
     */
    SensorDataCollection getSensorDataCollection();

    /**
     * Provides {@link Type} of Highlight
     * @return Type of Highlight
     */
    Type getHighlightType();

    /**
     * Provides starting point of the highlight in the video.
     * @return Starting point of highlight in the video in seconds
     */
    float getStartSecs();

    /**
     * Sets starting point of the highlight in the video.
     * @param startSecs Starting point of highlight in the video in seconds
     */
    void setStartSecs(float startSecs);

    /**
     * Provides ending point of the highlight in the video.
     * @return Ending point of highlight in the video in seconds
     */
    float getEndSecs();

    /**
     * Provides duration of highlight.
     * @return Duration of highlight in seconds.
     */
    float getDurationSecs();

    /**
     * Sets duration of highlight.
     * @param durationSecs Duration of highlight in seconds.
     */
    void setDurationSecs(float durationSecs);

    /**
     * Provides exact point when highlight was created in the video
     * @return Exact creation point in seconds
     */
    float getHighlightPositionOffsetSecs();

    /**
     * Sets the offset for exact point of highlight creation in the video
     * @param offsetSecs Exact creation point in seconds
     */
    void setOffsetSecs(float offsetSecs);

    /**
     * Returns path to Highlight thumbnail bitmap. It's relative to Camera API base url.
     * @return Path to thumbnail relative to base url.
     */
    String getThumbnailUrl();

    /**
     * Returns closest start offset point where camera can cut highlight to
     * export it as independent video.
     * This can differ from start offset since key frames in some modes can be further than
     * what Highlight model defines.
     * @return Closest start offset point in seconds.
     */
    Float getDeliveredOffsetSecs();

    /**
     * Returns closest length of highlight which camera can export as independent video. This can
     * differ from Highlight defined length since key frames in some modes can be further than
     * what Highlight model defines.
     * @return Closest length in seconds.
     */
    Float getDeliveredLengthSecs();
}
