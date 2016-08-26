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
import com.tomtom.camera.api.model.Video;
import com.tomtom.camera.api.model.Videos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Concrete implementation of interface {@link Videos}
 */
class VideosV2 implements Videos {

    @SerializedName("total")
    int mTotal;

    @SerializedName("items")
    VideoV2[] mItems;

    @Override
    public int getTotal() {
        return mTotal;
    }

    @Override
    public List<Video> getItems() {
        if(mItems != null) {
            return Arrays.asList((Video[])mItems);
        }
        return new ArrayList<Video>(0);
    }
}