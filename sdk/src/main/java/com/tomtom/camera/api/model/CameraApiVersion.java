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

package com.tomtom.camera.api.model;

import com.google.gson.annotations.SerializedName;
import com.tomtom.camera.api.CameraApi;

import java.util.ArrayList;

/**
 * Model which is used for creating {@link CameraApi}. Defines version,
 * revision and list of supported versions.
 */
public class CameraApiVersion {

    public static final String V2 = "2";

    public static final String REV_0 = "0";
    public static final String REV_1 = "1";

    @SerializedName("version")
    String mVersion;

    @SerializedName("revision")
    String mRevision = REV_0;

    @SerializedName("supported_versions")
    ArrayList<String> mSupportedVersions = new ArrayList<String>(0);

    /**
     * Returns version number.
     * @return String representation of API version
     */
    public String getVersion() {
        return mVersion;
    }

    /**
     * Returns revision number.
     * @return String representation of API revision
     */
    public String getRevision() {
        return mRevision;
    }

    /**
     * Provides List of supported versions.
     * @return List of String representations of supported versions
     */
    public ArrayList<String> getSupportedVersions() {
        return mSupportedVersions;
    }
}
