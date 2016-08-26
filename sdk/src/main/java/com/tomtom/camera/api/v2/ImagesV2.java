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
import com.tomtom.camera.api.model.Image;
import com.tomtom.camera.api.model.Images;

import java.util.List;

/**
 * Concrete implementation of interface {@link Images}
 */
class ImagesV2 implements Images {

    @SerializedName("total")
    private int mTotal;

    @SerializedName("items")
    private List<ImageV2> mImages;

    public int getTotal() {
        return mTotal;
    }

    public void setTotal(int total) {
        mTotal = total;
    }

    public List<? extends Image> getImages() {
        return mImages;
    }

    public void setImages(List<ImageV2> images) {
        mImages = images;
    }
}
