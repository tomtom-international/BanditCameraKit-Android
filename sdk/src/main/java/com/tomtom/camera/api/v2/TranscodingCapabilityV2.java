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
import com.tomtom.camera.api.model.capability.Framerate;
import com.tomtom.camera.api.model.capability.Resolution;

import java.io.IOException;

/**
 * This class is model of camera transcoding capabilities.
 * It contains transcoding possibilities (what output resolution/framerate videos are possible
 * to generate from given input resolution/framerate video).
 */
class TranscodingCapabilityV2 {

    @SerializedName("input_resolution")
    @JsonAdapter(ResolutionGsonAdapter.class)
    private Resolution mInputResolution;

    @SerializedName("input_framerate_fps")
    @JsonAdapter(FramerateGsonAdapter.class)
    private Framerate mInputFramerate;

    @SerializedName("output_resolution")
    @JsonAdapter(ResolutionGsonAdapter.class)
    private Resolution mOutputResolution;

    @SerializedName("output_framerate_fps")
    @JsonAdapter(FramerateGsonAdapter.class)
    private Framerate mOutputFramerate;

    Resolution getInputResolution() {
        return mInputResolution;
    }

    Framerate getInputFramerate() {
        return mInputFramerate;
    }

    Resolution getOutputResolution() {
        return mOutputResolution;
    }

    Framerate getOutputFramerate() {
        return mOutputFramerate;
    }

    static class ResolutionGsonAdapter extends TypeAdapter<Resolution> {
        @Override
        public Resolution read(JsonReader in) throws IOException {
            String resolution = in.nextString();
            return Resolution.fromString(resolution);
        }

        @Override
        public void write(JsonWriter out, Resolution resolution) throws IOException {
            if (resolution != null) {
                out.value(resolution.value());
            }
            else {
                out.nullValue();
            }
        }
    }

    static class FramerateGsonAdapter extends TypeAdapter<Framerate> {
        @Override
        public Framerate read(JsonReader in) throws IOException {
            int fps = in.nextInt();
            return Framerate.fromInt(fps);
        }

        @Override
        public void write(JsonWriter out, Framerate framerate) throws IOException {
            if (framerate != null) {
                out.value(framerate.intValue());
            }
            else {
                out.nullValue();
            }
        }
    }
}
