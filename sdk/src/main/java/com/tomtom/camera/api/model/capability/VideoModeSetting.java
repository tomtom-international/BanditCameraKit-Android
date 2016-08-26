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

package com.tomtom.camera.api.model.capability;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;

/**
 * Model for Camera video mode setting. Each video mode setting is defined with {@link Resolution},
 * possible {@link Framerate}s, intervals (Timelapse mode), slow motion factors (SlowMotion mode) and
 * fields of view.
 */
public class VideoModeSetting {

    Resolution mResolution;
    ArrayList<Framerate> mFramerateList;
    ArrayList<Integer> mIntervalList;
    ArrayList<Integer> mSlowMotionFactorList;
    ArrayList<String> mFieldOfViewList;

    public VideoModeSetting(Resolution resolution, ArrayList<Framerate> framerateList, ArrayList<Integer> intervalList, ArrayList<Integer> slowMotionFactorList, ArrayList<String> fieldOfViewList) {
        mResolution = resolution;
        mFramerateList = framerateList;
        mIntervalList = intervalList;
        mSlowMotionFactorList = slowMotionFactorList;
        mFieldOfViewList = fieldOfViewList;
    }

    /**
     * Returns video {@link Resolution}
     * @return {@link Resolution} enum value.
     */
    @NonNull
    public Resolution getResolution() {
        return mResolution;
    }

    /**
     * Returns list of supported {@link Framerate}s for given video mode setting.
     * @return ArrayList of {@link Framerate} enum values.
     */
    @NonNull
    public ArrayList<Framerate> getFramerates() {
        return mFramerateList;
    }

    /**
     * Returns list of supported fields of view defined in @StringDef
     * {@link com.tomtom.camera.api.model.VideoMode.FieldOfView}
     * @return ArrayList of String representations of FieldOfView.
     */
    @NonNull
    public ArrayList<String> getFieldOfViews() {
        return mFieldOfViewList;
    }

    /**
     * Returns list of slow motion factors for given video mode setting.
     * @return ArrayList of slow motion factors is it's supported, otherwise null
     */
    @Nullable
    public ArrayList<Integer> getSlowMotionFactors() {
        return mSlowMotionFactorList;
    }

    /**
     * Returns list of intervals between two shots for given video mode setting.
     * @return ArrayList of interval in seconds  if mode is lapse based (Timelapse, Nightlapse),
     * otherwise null
     */
    @Nullable
    public ArrayList<Integer> getIntervals() {
        return mIntervalList;
    }
}