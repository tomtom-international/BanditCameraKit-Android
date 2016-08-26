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
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.tomtom.camera.api.model.Scene;

/**
 * Concrete implementation of interface {@link Scene}
 */
class SceneV2 implements Scene {

    @JsonAdapter(Mode.GsonAdapter.class)
    @SerializedName("mode")
    private Mode mSceneMode;

    @SerializedName("brightness")
    private int mBrightness;

    @SerializedName("contrast")
    private int mContrast;

    @SerializedName("hue")
    private int mHue;

    @SerializedName("saturation")
    private int mSaturation;

    @SerializedName("sharpness")
    private int mSharpness;

    SceneV2(Scene scene) {
        mSceneMode = scene.getSceneMode();
        mBrightness = scene.getBrightness();
        mContrast = scene.getContrast();
        mHue = scene.getHue();
        mSaturation = scene.getSaturation();
        mSharpness = scene.getSharpness();
    }

    @Override
    public Mode getSceneMode() {
        return mSceneMode;
    }

    @Override
    public int getBrightness() {
        return mBrightness;
    }

    @Override
    public int getContrast() {
        return mContrast;
    }

    @Override
    public int getHue() {
        return mHue;
    }

    @Override
    public int getSaturation() {
        return mSaturation;
    }

    @Override
    public int getSharpness() {
        return mSharpness;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Scene) {
            Scene compareObject = ((Scene) o);
            if(mSceneMode != Mode.PHONE) {
                return mSceneMode == compareObject.getSceneMode();
            }

            return mSceneMode == compareObject.getSceneMode() &&
                    mBrightness == compareObject.getBrightness() &&
                    mContrast == compareObject.getContrast() &&
                    mHue == compareObject.getHue() &&
                    mSaturation == compareObject.getSaturation() &&
                    mSharpness == compareObject.getSaturation();
            }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(mSceneMode, mBrightness, mContrast, mHue, mSaturation, mSharpness);
    }
}
