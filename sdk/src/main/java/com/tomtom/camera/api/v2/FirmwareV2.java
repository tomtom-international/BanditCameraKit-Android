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

import com.google.gson.annotations.SerializedName;
import com.tomtom.camera.api.model.Firmware;
/**
 * Concrete implementation of interface {@link Firmware}
 */
class FirmwareV2 implements Firmware {

    @SerializedName("major")
    int mMajor;

    @SerializedName("minor")
    int mMinor;

    @SerializedName("revision")
    int mRevision;

    @SerializedName("build")
    int mBuild;

    @SerializedName("pending_major")
    int mPendingMajor;

    @SerializedName("pending_minor")
    int mPendingMinor;

    @SerializedName("pending_revision")
    int mPendingRevision;

    @SerializedName("pending_build")
    int mPendingBuild;

    public FirmwareV2(int major, int minor, int revision, int build, int pendingMajor, int pendingMinor, int pendingRevision, int pendingBuild) {
        mMajor = major;
        mMinor = minor;
        mRevision = revision;
        mBuild = build;
        mPendingMajor = pendingMajor;
        mPendingMinor = pendingMinor;
        mPendingRevision = pendingRevision;
        mPendingBuild = pendingBuild;
    }

    public int getMajor() {
        return mMajor;
    }

    public int getMinor() {
        return mMinor;
    }

    public int getRevision() {
        return mRevision;
    }

    public int getBuild() {
        return mBuild;
    }

    public void setBuild(int build) {
        mBuild = build;
    }

    public int getPendingMajor() {
        return mPendingMajor;
    }

    public int getPendingMinor() {
        return mPendingMinor;
    }

    public int getPendingRevision() {
        return mPendingRevision;
    }

    public int getPendingBuild() {
        return mPendingBuild;
    }

    public String getCurrent() {
        return new StringBuilder(String.valueOf(mMajor)).append(".").append(mMinor).append(".").append(mRevision).append(".").append(mBuild).toString();
    }

    public String getPending() {
        return new StringBuilder(String.valueOf(mPendingMajor)).append(".").append(mPendingMinor).append(".").append(mPendingRevision).append(".").append(mPendingBuild).toString();
    }
}