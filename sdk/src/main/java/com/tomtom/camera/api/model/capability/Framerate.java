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
 * Enum which corresponds to available camera framerates.
 */

public enum Framerate {
    FPS_180("180fps", 180),
    FPS_120("120fps", 120),
    FPS_60("60fps", 60),
    FPS_30("30fps", 30),
    FPS_15("15fps", 15);

    public static final String FRAMES_PER_SECOND_SUFIX = "fps";

    String mVal;
    int mNum;

    Framerate(String val, int num){
        mVal = val;
        mNum = num;
    }

    /**
     * Returns value as {@link String}. It will always have 'fps' suffix.
     * @return String representation of {@link Framerate}
     */
    @NonNull
    public String value() {
        return mVal;
    }

    /**
     * Returns value as integer.
     * @return Integer value of {@link Framerate}
     */
    @NonNull
    public int intValue(){
        return mNum;
    }

    /**
     * Returns {@link Framerate} instance which corresponds to given string.
     * @param val String representation of framerate (e.g. 60fps)
     * @return {@link Framerate} instance if it's defined by enum. If not, returns null.
     */
    @Nullable
    public static Framerate fromString(String val) {
        for (Framerate framerate : values()) {
            if (framerate.mVal.equals(val)) {
                return framerate;
            }
        }
        return null;
    }

    /**
     * Returns {@link Framerate} instance which corresponds to given number of frames per second.
     * @param val Framerate number as integer
     * @return {@link Framerate} instance if it's defined by enum. If not, returns null.
     */
    @Nullable
    public static Framerate fromInt(int val) {
        for (Framerate framerate : values()) {
            if (framerate.mNum == val) {
                return framerate;
            }
        }
        return null;
    }
}
