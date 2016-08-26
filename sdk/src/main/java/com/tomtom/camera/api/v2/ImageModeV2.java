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
import com.tomtom.camera.api.model.Image;
import com.tomtom.camera.api.model.ImageMode;

import java.io.IOException;

/**
 * Concrete implementation of interface {@link ImageMode}
 */
class ImageModeV2 implements ImageMode {

    @JsonAdapter(ImageModeGsonAdapter.class)
    @SerializedName("mode")
    Image.Mode mMode;

    @SerializedName("resolution")
    String mResolution;

    @SerializedName("duration_secs")
    Integer mDurationSecs;

    @SerializedName("interval_secs")
    Integer mIntervalSecs;

    @SerializedName("count")
    Integer mCount;

    ImageModeV2(Image.Mode imageMode) {
        mMode = imageMode;
    }

    ImageModeV2(Image.Mode imageMode, String resolution, Integer durationSecs, Integer intervalSecs, Integer count) {
        mMode = imageMode;
        mResolution = resolution;
        mDurationSecs = durationSecs;
        mIntervalSecs = intervalSecs;
        mCount = count;
    }
    @Override
    public Image.Mode getMode() {
        return mMode;
    }

    @Override
    public String getResolution() {
        return mResolution;
    }

    @Override
    public Integer getIntervalSecs() {
        return mIntervalSecs;
    }

    @Override
    public Integer getDurationSecs() {
        return mDurationSecs;
    }

    @Override
    public Integer getCount() {
        return mCount;
    }


    public static class ImageModeGsonAdapter extends TypeAdapter<Image.Mode> {

        @Override
        public Image.Mode read(JsonReader in) throws IOException {
            String mode = in.nextString();
            return Image.Mode.fromString(mode);
        }

        @Override
        public void write(JsonWriter out, Image.Mode mode) throws IOException {
            if (mode != null) {
                out.value(mode.value());
            }
            else {
                out.nullValue();
            }
        }
    }

}
