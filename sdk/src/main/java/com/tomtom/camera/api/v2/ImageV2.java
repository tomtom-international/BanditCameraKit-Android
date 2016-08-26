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

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.tomtom.camera.api.model.Image;
import com.tomtom.camera.util.Logger;
import com.tomtom.camera.util.RegexUtil;

import java.io.IOException;
import java.util.Date;

/**
 * Concrete implementation of interface {@link Image}
 */
class ImageV2 extends CameraFileV2 implements Image {

    private static final String TAG  = "Image";

    private static final String IMAGE_COMPARABLE_ID_PATTERN = "\\/.*\\/IMG_(.*).JPG";

    @SerializedName(IMAGE_CLASS_TYPE)
    String mClassType = getClass().getName();

    @SerializedName("id")
    String mId;

    @SerializedName("path")
    String mFilePathOnCamera;

    @SerializedName("created")
    Date mCreated;

    @SerializedName("lat_deg")
    float mLat;

    @SerializedName("lon_deg")
    float mLon;

    @JsonAdapter(ImageModeGsonAdapter.class)
    @SerializedName("mode")
    Image.Mode mMode;

    @SerializedName("resolution")
    String mResolution;

    @SerializedName("aspect_ratio")
    String mAspectRatio;

    @SerializedName("is_valid")
    boolean mIsValid;

    public ImageV2() {
    }

    @Override
    public int getType() {
        return PHOTO;
    }

    public void setId(String id) {
        mId = id;
    }

    @Override
    public String getPath() {
        return mFilePathOnCamera;
    }

    public void setPath(String path) {
        mFilePathOnCamera = path;
    }

    @Override
    public Date getDateCreated() {
        return mCreated;
    }

    @Override
    public float getLat() {
        return mLat;
    }

    public void setLat(float lat) {
        mLat = lat;
    }

    @Override
    public float getLon() {
        return mLon;
    }

    public void setLon(float lon) {
        mLon = lon;
    }

    @Override
    public String getFileIdentifier() {
        return mId;
    }

    @Override
    public boolean isValid() {
        return mIsValid;
    }

    @Override
    public Image.Mode getMode() {
        return mMode;
    }

    @Override
    public String getResolution() {
        return mResolution;
    }

    @Override
    public String getComparableId() {
        try {
            return RegexUtil.getMatcher(IMAGE_COMPARABLE_ID_PATTERN, mFilePathOnCamera).group(1);
        }
        catch (IllegalStateException e) {
            Logger.error(TAG, e.getMessage());
            return null;
        }
    }

    @Override
    public String getKeyForFiltering() {
        return mMode.value();
    }

    @Override
    public String toJsonString() {
        return new Gson().toJson(this);
    }

    @Override
    public String getThumbnailUrl() {
        return GET_IMAGE_THUMBNAIL_RELATIVE_PATH.replace("{image_id}", getFileIdentifier());
    }

    public static class ImageModeGsonAdapter extends TypeAdapter<Mode> {

        @Override
        public Image.Mode read(JsonReader in) throws IOException {
            String mode = in.nextString();
            return Image.Mode.fromString(mode);
        }

        @Override
        public void write(JsonWriter out, Image.Mode mode) throws IOException {
            if (mode != null) {
                out.value(mode.value());
            }
            else {
                out.nullValue();
            }
        }
    }
}