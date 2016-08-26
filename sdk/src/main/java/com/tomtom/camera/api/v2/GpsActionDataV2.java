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

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.tomtom.camera.api.model.GpsActionData;

import java.io.IOException;

/**
 * Concrete implementation of interface {@link GpsActionData}
 */
class GpsActionDataV2 implements GpsActionData {
    @JsonAdapter(GpsModeGsonAdapter.class)
    @SerializedName("mode")
    Mode mMode;

    @SerializedName("speed_mps")
    String mSpeed;

    @SerializedName("alt_m")
    String mAlt;

    @SerializedName("lat_deg")
    String mLatitude;

    @SerializedName("lon_deg")
    String mLongitude;

    @SerializedName("heading_deg")
    String mHead;

    @SerializedName("track_deg")
    String mTrack;

    @SerializedName("range_alt_m")
    String mVerticalUncertainty;

    @SerializedName("range_lat_m")
    String mLatUncertainty;

    @SerializedName("range_lon_m")
    String mLongUncertainty;

    @SerializedName("range_track_deg")
    String mTrackUncertainty;

    public Mode getMode() {
        return mMode;
    }

    public void setMode(Mode mode) {
        mMode = mode;
    }

    public String getSpeedMps() {
        return mSpeed;
    }

    public void setSpeed(String speed) {
        mSpeed = speed;
    }

    public String getAltitudeMeters() {
        return mAlt;
    }

    public void setAlt(String alt) {
        mAlt = alt;
    }

    public String getLatitude() {
        return mLatitude;
    }

    public void setLatitude(String latitude) {
        mLatitude = latitude;
    }

    public String getLongitude() {
        return mLongitude;
    }

    public void setLongitude(String longitude) {
        mLongitude = longitude;
    }

    public String getTrack() {
        return mTrack;
    }

    public void setTrack(String track) {
        mTrack = track;
    }

    public String getHead() {
        return mHead;
    }

    public void setHead(String head) {
        mHead = head;
    }

    public String getVerticalUncertaintyMeters() {
        return mVerticalUncertainty;
    }

    public void setVerticalUncertainty(String verticalUncertainty) {
        mVerticalUncertainty = verticalUncertainty;
    }

    public String getLatUncertaintyMeters() {
        return mLatUncertainty;
    }

    public void setLatUncertainty(String latUncertainty) {
        mLatUncertainty = latUncertainty;
    }

    public String getLongUncertaintyMeters() {
        return mLongUncertainty;
    }

    public void setLongUncertainty(String longUncertainty) {
        mLongUncertainty = longUncertainty;
    }

    public String getTrackUncertaintyMeters() {
        return mTrackUncertainty;
    }

    public void setTrackUncertainty(String trackUncertainty) {
        mTrackUncertainty = trackUncertainty;
    }

    public static class GpsModeGsonAdapter extends TypeAdapter<Mode> {

        @Override
        public GpsActionData.Mode read(JsonReader in) throws IOException {
            String mode = in.nextString();
            return GpsActionData.Mode.fromString(mode);
        }

        @Override
        public void write(JsonWriter out, GpsActionData.Mode mode) throws IOException {
            if (mode != null) {
                out.value(mode.value());
            }
            else {
                out.nullValue();
            }
        }
    }
}
