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

package com.tomtom.camera.app.model;

import com.tomtom.camera.api.model.Video;
import com.tomtom.camera.api.model.VideoMode;
import com.tomtom.camera.api.model.capability.Framerate;
import com.tomtom.camera.api.model.capability.Resolution;

import java.util.ArrayList;

/**
 * Cocrete implementation of VideoMode interface
 */
public class CameraVideoMode implements VideoMode {

    private Video.Mode mMode;
    private Resolution mResolution;
    private Framerate mFramerate;
    private @FieldOfView String mFieldOfView;
    private Integer mIntervalSecs = null;
    private Integer mSlowmotionFactor = null;
    private ArrayList<String> mScenesDisabled = null;

    private CameraVideoMode(){

    }

    @Override
    public Video.Mode getMode() {
        return mMode;
    }

    @Override
    public Resolution getResolution() {
        return mResolution;
    }

    @Override
    public Framerate getFramerate() {
        return mFramerate;
    }

    @Override
    public @FieldOfView String getFieldOfView() {
        return mFieldOfView;
    }

    @Override
    public Integer getIntervalSecs() {
        return mIntervalSecs;
    }

    @Override
    public Integer getSlowMotionRate() {
        return mSlowmotionFactor;
    }

    @Override
    public ArrayList<String> getScenesDisabled() {
        return mScenesDisabled;
    }

    public static class Builder {

        private CameraVideoMode mCameraVideoMode;

        public Builder() {
            mCameraVideoMode = new CameraVideoMode();
        }

        public Builder videoMode(Video.Mode videoMode) {
            mCameraVideoMode.mMode = videoMode;
            return this;
        }

        public Builder resolution(Resolution resolution) {
            mCameraVideoMode.mResolution = resolution;
            return this;
        }

        public Builder framerate(Framerate framerate) {
            mCameraVideoMode.mFramerate = framerate;
            return this;
        }

        public Builder fieldOfView(@FieldOfView String fieldOfView) {
            mCameraVideoMode.mFieldOfView = fieldOfView;
            return this;
        }

        public Builder intervalSecs(Integer intervalSecs) {
            mCameraVideoMode.mIntervalSecs = intervalSecs;
            return this;
        }

        public Builder slowMotionRate(Integer slowMotionFactor) {
            mCameraVideoMode.mSlowmotionFactor = slowMotionFactor;
            return this;
        }

        public Builder scenesDisabled(ArrayList<String> scenesDisabled) {
            mCameraVideoMode.mScenesDisabled = scenesDisabled;
            return this;
        }

        public CameraVideoMode build() {
            return mCameraVideoMode;
        }
    }

    @Override
    public boolean equals(Object o) {
        if(o != null & o instanceof VideoMode) {
            VideoMode other = (VideoMode) o;
            return getMode().equals(other.getMode()) &&
                    getFramerate().equals(other.getFramerate()) &&
                    getResolution().equals(other.getResolution()) &&
                    getFieldOfView().equals(other.getFieldOfView()) &&
                    getIntervalSecs() == other.getIntervalSecs() &&
                    getSlowMotionRate() == other.getSlowMotionRate();
        }
        return false;
    }
}
