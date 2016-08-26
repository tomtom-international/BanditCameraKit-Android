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
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.tomtom.camera.api.model.CameraFile;
import com.tomtom.camera.api.model.Highlight;
import com.tomtom.camera.api.model.Video;
import com.tomtom.camera.api.model.capability.Framerate;
import com.tomtom.camera.api.model.capability.Resolution;
import com.tomtom.camera.util.ApiUtil;
import com.tomtom.camera.util.Logger;
import com.tomtom.camera.util.RegexUtil;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Concrete implementation of interface {@link Video}
 */
class VideoV2 implements Video {

    private static final String TAG = "VideoV1";

    static final String VIDEO_FILE_COMPARABLE_ID_PATTERN = "\\/.*\\/MOV_(.*).MP4";

    @Expose
    @SerializedName(CLASS_TYPE)
    String mClassType = getClass().getName();

    @Expose
    @SerializedName("id")
    String mVideoId;

    @Expose
    @SerializedName("created")
    Date mDateCreated;

    @Expose
    @SerializedName("size_bytes")
    long mSize;

    @Expose
    @SerializedName("length_secs")
    float mDuration;

    @Expose
    @SerializedName("nr_highlights")
    int mNumberOfHighlights;

    @Expose
    @SerializedName("path")
    String mFilePathOnCamera;

    @Expose
    @JsonAdapter(VideoModeGsonAdapter.class)
    @SerializedName("mode")
    Mode mVideoMode;

    @Expose
    @SerializedName("aspect_ratio")
    String mRatio;

    @Expose
    @JsonAdapter(ResolutionGsonAdapter.class)
    @SerializedName("resolution")
    Resolution mResolution;

    @Expose
    @JsonAdapter(FramerateGsonAdapter.class)
    @SerializedName("framerate")
    Framerate mFramerate = null;

    @Expose
    @JsonAdapter(FramerateFpsGsonAdapter.class)
    @SerializedName("framerate_fps")
    Framerate mFramerateFps = null;

    @Expose
    @SerializedName("is_valid")
    boolean mIsValid;

    private List<Highlight> mTags;

    public VideoV2() {
    }

    @Override
    public int getType() {
        return CameraFile.VIDEO;
    }

    @Override
    public void setVideoId(String videoId) {
        mVideoId = videoId;
    }

    @Override
    public String getVideoId() {
        return mVideoId;
    }

    @Override
    public Date getDateCreated() {
        return mDateCreated;
    }

    @Override
    public long getSize() {
        return mSize;
    }

    @Override
    public String getFilePathOnCamera() {
        return mFilePathOnCamera;
    }

    @Override
    public int getNumberOfHighlights() {
        return mNumberOfHighlights;
    }

    @Override
    public void setNumberOfHighlights(int numberOfHighlights) {
        mNumberOfHighlights = numberOfHighlights;
    }

    @Override
    public String getRatio() {
        return mRatio;
    }

    @Override
    public boolean isMuted() {
        return mVideoMode == Mode.TIME_LAPSE || mVideoMode == Mode.NIGHT_LAPSE;
    }

    @Override
    public Mode getMode() {
        return mVideoMode;
    }

    @Override
    public Resolution getResolution() {
        return mResolution;
    }

    @Override
    public Framerate getFramerate() {
        return mFramerate != null ? mFramerate : mFramerateFps;
    }

    @Override
    public List<Highlight> getHighlights() {
        return mTags;
    }

    @Override
    public void setHighlights(List<Highlight> highlights) {
        mTags = highlights;
    }

    @Override
    public void incrementNumberOfHighlights() {
        mNumberOfHighlights++;
    }

    @Override
    public void decrementNumberOfHighlights() {
        if (mNumberOfHighlights > 0) {
            mNumberOfHighlights--;
        }
    }

    @Override
    public String getPlayableId() {
        return getFileIdentifier();
    }

    @Override
    public float getDurationSecs() {
        return mDuration;
    }

    @Override
    public float getStartOffsetSecs() {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Video) {
            Video compareVideoFile = (Video) o;
            if (compareVideoFile.getFileIdentifier() != null && mVideoId != null) {
                if (compareVideoFile.getFileIdentifier().equals(mVideoId)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(mVideoId);
    }

    @Override
    public String getFileIdentifier() {
        return mVideoId;
    }


    @Override
    public String getComparableId() {
        try {
            return RegexUtil.getMatcher(VIDEO_FILE_COMPARABLE_ID_PATTERN, mFilePathOnCamera).group(1);
        } catch (IllegalStateException e) {
            Logger.error(TAG, e.getMessage());
            return null;
        }
    }

    @Override
    public String getKeyForFiltering() {
        return mVideoMode.value();
    }

    @Override
    public String toJsonString() {
        return new GsonBuilder()
                .registerTypeAdapter(Date.class, new ApiUtil.DateJsonSerializer())
                .registerTypeAdapter(Date.class, new ApiUtil.DateJsonDeserializer())
                .excludeFieldsWithoutExposeAnnotation()
                .create().toJson(this);
    }

    public static VideoV2 fromString(String jsonString) {
        return ApiUtil.getDateHandlingGson().fromJson(jsonString, VideoV2.class);
    }

    @Override
    public int compareTo(CameraFile another) {
        if (another instanceof Video) {
            if (another.getComparableId() != null) {
                if (getComparableId() != null) {
                    return getComparableId().compareTo(another.getComparableId());
                } else {
                    return -1;
                }
            } else {
                return -1;
            }
        }
        return -1;
    }

    @Override
    public String getThumbnailUrl() {
        return GET_VIDEO_THUMBNAIL_RELATIVE_PATH.replace("{video_id}", getFileIdentifier());
    }

    @Override
    public boolean isValid() {
        return mIsValid;
    }

    @Override
    public VideoV2 clone() throws CloneNotSupportedException {
        return (VideoV2) super.clone();
    }

    public static class VideoModeGsonAdapter extends TypeAdapter<Mode> {

        @Override
        public Mode read(JsonReader in) throws IOException {
            String mode = in.nextString();
            return Mode.fromString(mode);
        }

        @Override
        public void write(JsonWriter out, Mode mode) throws IOException {
            if (mode != null) {
                out.value(mode.value());
            } else {
                out.nullValue();
            }
        }
    }

    static class ResolutionGsonAdapter extends TypeAdapter<Resolution> {
        @Override
        public Resolution read(JsonReader in) throws IOException {
            String resolution = in.nextString();
            return Resolution.fromString(resolution);
        }

        @Override
        public void write(JsonWriter out, Resolution resolution) throws IOException {
            if (resolution != null) {
                out.value(resolution.value());
            } else {
                out.nullValue();
            }
        }
    }

    static class FramerateFpsGsonAdapter extends TypeAdapter<Framerate> {
        @Override
        public Framerate read(JsonReader in) throws IOException {
            int fps = in.nextInt();
            return Framerate.fromInt(fps);
        }

        @Override
        public void write(JsonWriter out, Framerate framerate) throws IOException {
            if (framerate != null) {
                out.value(framerate.intValue());
            } else {
                out.nullValue();
            }
        }
    }

    static class FramerateGsonAdapter extends TypeAdapter<Framerate> {
        @Override
        public Framerate read(JsonReader in) throws IOException {
            String fps = in.nextString();
            return Framerate.fromString(fps);
        }

        @Override
        public void write(JsonWriter out, Framerate framerate) throws IOException {
            if (framerate != null) {
                out.value(framerate.value());
            } else {
                out.nullValue();
            }
        }
    }
}