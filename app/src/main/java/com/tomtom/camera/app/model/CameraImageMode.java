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

import com.tomtom.camera.api.model.Image;
import com.tomtom.camera.api.model.ImageMode;

/**
 * Concrete implementation of ImageMode interface
 */

public class CameraImageMode implements ImageMode {

    private Image.Mode mImageMode;
    private String mResolution;
    private Integer mIntervalSecs;
    private Integer mDurationSecs;
    private Integer mCount;

    private CameraImageMode() {

    }

    @Override
    public Image.Mode getMode() {
        return mImageMode;
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

    public static class Builder {

        private CameraImageMode mBanditImageMode;

        public Builder() {
            mBanditImageMode = new CameraImageMode();
        }

        public Builder mode(Image.Mode mode) {
            mBanditImageMode.mImageMode = mode;
            return this;
        }

        public Builder resolution(String resolution) {
            mBanditImageMode.mResolution = resolution;
            return this;
        }

        public Builder intervalSecs(Integer intervalSecs) {
            mBanditImageMode.mIntervalSecs = intervalSecs;
            return this;
        }

        public Builder durationSecs(Integer durationSecs) {
            mBanditImageMode.mDurationSecs = durationSecs;
            return this;
        }

        public Builder count(Integer count) {
            mBanditImageMode.mCount = count;
            return this;
        }

        public CameraImageMode build() {
            return mBanditImageMode;
        }


    }
}
