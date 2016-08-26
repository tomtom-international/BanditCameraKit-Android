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
import com.tomtom.camera.api.model.ImageSession;
import com.tomtom.camera.api.model.ImageSessions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Implementation of {@link ImageSessions}
 */

class ImageSessionsV2 implements ImageSessions {

    @SerializedName("total")
    int mTotal;

    @SerializedName("items")
    ImageSessionV2[] mImageSessions;
    @Override
    public int getTotal() {
        return mTotal;
    }

    @Override
    public List<ImageSession> getItems() {
        if(mImageSessions != null) {
            return Arrays.asList((ImageSession[])mImageSessions);
        }
        return new ArrayList<ImageSession>(0);
    }
}
