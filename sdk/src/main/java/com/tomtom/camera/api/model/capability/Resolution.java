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

public enum Resolution {
    RES_4K("4k", 3840, 2160),
    RES_2_7K("2.7k", 2740, 1524),
    RES_FHD("1080p", 1920, 1080),
    RES_HD("720p", 1280, 720),
    RES_WVGA("wvga", 853, 480);

    String mVal;
    int mWidthPx;
    int mHeightPx;

    Resolution(String val, int widthPx, int heightPx){
        mVal = val;
        mWidthPx = widthPx;
        mHeightPx = heightPx;
    }

    /**
     * Returns {@link Resolution} instance based on given width and height.
     * @param widthPx Width in pixels
     * @param heightPx Height in pixels
     * @return {@link} Resolution instance if defined in enum, otherwise is null.
     */
    @Nullable
    public static Resolution getResolution(int widthPx, int heightPx) {
        for (Resolution resolution : values()) {
            if (resolution.mWidthPx == widthPx &&
                    resolution.mHeightPx == heightPx) {
                return resolution;
            }
        }
        return null;
    }

    /**
     * Returns name (string representation) of {@link Resolution} (e.g. 1080p, 720p...)
     * @return String value of {@link Resolution}
     */
    @NonNull
    public String value() {
        return mVal;
    }

    /**
     * Returns {@link Resolution} width in pixels.
     * @return Width in pixels
     */
    @NonNull
    public int getWidthPx() {
        return mWidthPx;
    }

    /**
     * Returns {@link Resolution} height in pixels.
     * @return Height in pixels
     */
    @NonNull
    public int getHeightPx() {
        return mHeightPx;
    }

    /**
     * Looks up if there's {@link Resolution} which is defined with same name
     * @param val Name of resolution
     * @return {@link Resolution} instance if exists, otherwise null
     */
    @Nullable
    public static Resolution fromString(String val) {
        for (Resolution resolution : values()) {
            if (resolution.mVal.equals(val)) {
                return resolution;
            }
        }
        return null;
    }
}
