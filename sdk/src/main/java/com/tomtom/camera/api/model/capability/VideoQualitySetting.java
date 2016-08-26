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

import com.google.common.base.Objects;

/**
 * Model which describes transcoding option defined by video {@link Resolution} and {@link Framerate}
 * pair.
 */
public final class VideoQualitySetting {

    private final Resolution mResolution;
    private final Framerate mFramerate;

    public VideoQualitySetting(@NonNull Resolution resolution, @NonNull Framerate framerate) {
        mResolution = resolution;
        mFramerate = framerate;
    }

    public Resolution getResolution() {
        return mResolution;
    }

    public Framerate getFramerate() {
        return mFramerate;
    }

    @Override
    public boolean equals(Object o) {
        if(o != null && o instanceof VideoQualitySetting) {
            VideoQualitySetting other = (VideoQualitySetting) o;
            if(mResolution == other.getResolution() &&
                    mFramerate == other.getFramerate()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hashCode = Objects.hashCode(mResolution, mFramerate);
        return hashCode;
    }
}
