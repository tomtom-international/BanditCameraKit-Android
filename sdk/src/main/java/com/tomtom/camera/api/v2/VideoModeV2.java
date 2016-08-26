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

import com.google.common.base.Objects;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.tomtom.camera.api.model.Video;
import com.tomtom.camera.api.model.VideoMode;
import com.tomtom.camera.api.model.capability.Framerate;
import com.tomtom.camera.api.model.capability.Resolution;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Concrete implementation of interface {@link VideoMode}
 */
class VideoModeV2 implements VideoMode {

    @JsonAdapter(ModeGsonAdapter.class)
    @SerializedName("mode")
    Video.Mode mMode;

    @JsonAdapter(FramerateGsonAdapter.class)
    @SerializedName("framerate_fps")
    private Framerate mFramerate;

    @JsonAdapter(ResolutionGsonAdapter.class)
    @SerializedName("resolution")
    private Resolution mResolution;

    @SerializedName("fov")
    private @FieldOfView String mFieldOfView;

    @SerializedName("slow_motion_rate")
    private Integer mSlowMotionRate;

    @SerializedName("interval_secs")
    private Integer mIntervalSecs;

    @SerializedName("scenes_disabled")
    ArrayList<String> mScenesDisabled;

    VideoModeV2(Video.Mode mode, Resolution resolution, Framerate framerate, @FieldOfView String fieldOfView, ArrayList<String> disabledScenes) {
        mMode = mode;
        mResolution = resolution;
        mFramerate = framerate;
        mFieldOfView = fieldOfView;
        mScenesDisabled = disabledScenes;
    }

    VideoModeV2(Video.Mode mode, Resolution resolution, Framerate framerate, @FieldOfView String fieldOfView, Integer slowMotionRate, Integer intervalSecs) {
        this(mode, resolution, framerate, fieldOfView, null);
        mSlowMotionRate = slowMotionRate;
        mIntervalSecs = intervalSecs;
    }

    public @FieldOfView String getFieldOfView() {
        return mFieldOfView;
    }

    public Framerate getFramerate() {
        return mFramerate;
    }

    public Resolution getResolution() {
        return mResolution;
    }

    public Video.Mode getMode() {
        return mMode;
    }

    @Override
    public Integer getIntervalSecs() {
        return mIntervalSecs;
    }

    @Override
    public Integer getSlowMotionRate() {
        if(mMode.equals(Video.Mode.SLOW_MOTION) && mSlowMotionRate == null) {
            return mFramerate.intValue() / Framerate.FPS_30.intValue();
        }
        return mSlowMotionRate;
    }

    @Override
    public ArrayList<String> getScenesDisabled() {
        return mScenesDisabled;
    }

    @Override
    public boolean equals(Object o) {
        if(o != null & o instanceof VideoMode) {
            VideoMode other = (VideoMode) o;
            return getMode().equals(other.getMode()) &&
                    getFramerate().equals(other.getFramerate()) &&
                    getResolution().equals(other.getResolution()) &&
                    getFieldOfView().equals(other.getFieldOfView()) &&
                    getIntervalSecs().equals(other.getIntervalSecs()) &&
                    getSlowMotionRate().equals(other.getSlowMotionRate());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(mMode, mFramerate, mResolution, mFieldOfView, mIntervalSecs, mSlowMotionRate);
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

    static class ModeGsonAdapter extends TypeAdapter<Video.Mode> {
        @Override
        public Video.Mode read(JsonReader in) throws IOException {
            String mode = in.nextString();
            return Video.Mode.fromString(mode);
        }

        @Override
        public void write(JsonWriter out, Video.Mode mode) throws IOException {
            if (mode != null) {
                out.value(mode.value());
            }
            else {
                out.nullValue();
            }
        }
    }
}
