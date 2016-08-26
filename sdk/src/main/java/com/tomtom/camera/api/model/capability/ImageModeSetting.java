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

/**
 * Model for Camera image mode setting.
 */
public class ImageModeSetting {

    String mResolution;
    Integer mDurationSecs;
    Integer mCount;
    Integer mIntervalSecs;

    public ImageModeSetting(String resolution, Integer durationSecs, Integer count, Integer intervalSecs) {
        mResolution = resolution;
        mDurationSecs = durationSecs;
        mCount = count;
        mIntervalSecs = intervalSecs;
    }

    /**
     * Returns image resolution as String
     * @return String representation of image resolution, never null.
     */
    @NonNull
    public String getResolution() {
        return mResolution;
    }

    /**
     * Returns for how long will current image mode take shots.
     * @return Seconds of duration of recording if mode supports it, otherwise null.
     */
    @Nullable
    public Integer getDurationSecs() {
        return mDurationSecs;
    }

    /**
     * Returns for how many shots will be taken in current image mode setting.
     * @return Number of shots if mode supports it, otherwise null.
     */
    @Nullable
    public Integer getCount() {
        return mCount;
    }

    /**
     * Returns interval between two shots in current image mode setting.
     * @return Seconds between two shots if mode supports it, otherwise null.
     */
    @Nullable
    public Integer getIntervalSecs() {
        return mIntervalSecs;
    }
}
