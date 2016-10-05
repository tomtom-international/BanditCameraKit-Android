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
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.tomtom.camera.api.model.CameraFile;
import com.tomtom.camera.api.model.Highlight;
import com.tomtom.camera.api.model.SensorDataCollection;
import com.tomtom.camera.api.model.Video;
import com.tomtom.camera.util.Logger;

import java.io.IOException;
import java.util.Date;

/**
 * Concrete implementation of interface {@link Highlight}
 */
@JsonAdapter(HighlightV2.HighlightTypeAdapter.class)
class HighlightV2 implements Highlight {

    private static final String TAG = "HighlightV2";
    Type mHighlightType;
    String mId;
    float mOffsetSecs;
    float mStart;
    float mDuration;
    Float mDeliveredOffsetSecs;
    Float mDeliveredLengthSecs;
    Object mValue;

    Video mVideoFile;

    SensorDataCollection mSensorData;

    public HighlightV2() {

    }

    public HighlightV2(Highlight highlight) {
        mHighlightType = highlight.getHighlightType();
        mId = highlight.getId();
        mOffsetSecs = highlight.getHighlightPositionOffsetSecs();
        mStart = highlight.getStartSecs();
        mDuration = highlight.getDurationSecs();
        mValue = highlight.getValue();
        mVideoFile = highlight.getVideo();
    }

    @Override
    public int getType() {
        return CameraFile.HIGHLIGHT;
    }

    @Override
    public String getId() {
        return mId;
    }

    @Override
    public void setVideo(Video video){
        mVideoFile = video;
    }

    @Override
    public Video getVideo(){
        return mVideoFile;
    }

    @Override
    public float getStartSecs() {
        return mStart;
    }

    @Override
    public void setStartSecs(float start) {
        mStart = start;
    }

    @Override
    public float getEndSecs() {
        return mStart + mDuration;
    }

    @Override
    public void setDurationSecs(float duration) {
        mDuration = duration;
    }

    @Override
    public Type getHighlightType() {
        return mHighlightType;
    }

    @Override
    public float getHighlightPositionOffsetSecs() {
        return mOffsetSecs;
    }

    @Override
    public void setOffsetSecs(float offsetSecs) {
        mOffsetSecs = offsetSecs;
    }

    @Override
    public Object getValue() {
        return mValue;
    }

    @Override
    public void setValue(Object value) {
        mValue = value;
    }

    @Override
    public void setSensorDataCollection(SensorDataCollection sensorDataCollection) {
        mSensorData = sensorDataCollection;
    }

    @Override
    public SensorDataCollection getSensorDataCollection() {
        return mSensorData;
    }

    @Override
    public String getFileIdentifier() {
        return mVideoFile.getFileIdentifier();
    }

    @Override
    public String getPlayableId() {
        return getFileIdentifier();
    }

    @Override
    public float getStartOffsetSecs() {
        return getStartSecs();
    }

    @Override
    public float getDurationSecs() {
        return mDuration;
    }

    @Override
    public boolean isMuted() {
        return mVideoFile.isMuted();
    }

    @Override
    public String getComparableId() {
        return mId;
    }

    @Override
    public String getKeyForFiltering() {
        return getHighlightType().value();
    }
    @Override
    public Date getDateCreated() {
        if (mVideoFile != null) {
            return mVideoFile.getDateCreated();
        }
        return null;
    }

    @Override
    public Float getDeliveredLengthSecs()  {
        return mDeliveredLengthSecs;
    }

    void setDeliveredOffsetSecs(Float deliveredOffsetSecs) {
        mDeliveredOffsetSecs = deliveredOffsetSecs;
    }

    @Override
    public Float getDeliveredOffsetSecs() {
        return mDeliveredOffsetSecs;
    }

    void setmDeliveredLengthSecs(Float deliveredLengthSecs) {
        mDeliveredLengthSecs = deliveredLengthSecs;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Highlight) {
            Highlight compareTag = (Highlight)o;
            if (compareTag.getId() != null
                    && compareTag.getVideo() != null
                    && compareTag.getVideo().getFileIdentifier() != null
                    && mId != null
                    && mVideoFile != null
                    && mVideoFile.getFileIdentifier() != null) {

                if (compareTag.getId().equals(mId) &&
                        compareTag.getVideo().getFileIdentifier().equals(mVideoFile.getFileIdentifier())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String toJsonString() {
        return new Gson().toJson(this);
    }

    @Override
    public boolean isValid() {
        return mVideoFile != null && mVideoFile.isValid();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(mId,mVideoFile.getFileIdentifier());
    }

    @Override
    public int compareTo(CameraFile another) {
        return 0;
    }

    @Override
    public HighlightV2 clone() throws CloneNotSupportedException {
        return (HighlightV2) super.clone();
    }

    @Override
    public String getThumbnailUrl() {
        return GET_TAG_THUMBNAIL_RELATIVE_PATH.replace("{video_id}", getFileIdentifier()).replace("{tag_id}", getId());
    }

    static class HighlightTypeAdapter extends TypeAdapter<Highlight> {

        @Override
        public void write(JsonWriter out, Highlight value) throws IOException {
            out.beginObject();
            out.name(HIGHLIGHT_TYPE);
            out.value(value.getClass().getName());
            if (value.getHighlightType() != null) {
                out.name(value.getHighlightType().value());
                switch (value.getHighlightType()) {
                    case TAG_BUTTON:
                    case TAG_MOBILE:
                    case TAG_REMOTE:
                        if (value.getValue() instanceof Boolean) {
                            out.value((Boolean) value.getValue());
                        } else {
                            Logger.exception(new Exception("Trying to write " + value.getValue() + " to " + value.getHighlightType() + " highlight instead of Boolean"));
                        }
                        break;
                    case TAG_MAX_SPEED:
                    case TAG_ACCELERATION:
                    case TAG_DECELERATION:
                    case TAG_MAX_G_FORCE:
                    case TAG_MAX_VERTICAL_SPEED:
                        if (value.getValue() instanceof Double) {
                            out.value((Double) value.getValue());
                        } else {
                            Logger.exception(new Exception("Trying to write " + value.getValue() + " to " + value.getHighlightType() + " highlight instead of Double"));
                        }
                        break;
                    case TAG_MAX_HEART_RATE:
                    case TAG_MAX_ROTATION:
                        if (value.getValue() instanceof Integer) {
                            out.value((Integer) value.getValue());
                        } else {
                            Logger.exception(new Exception("Trying to write " + value.getValue() + " to " + value.getHighlightType() + " highlight instead of Integer"));
                        }
                        break;
                    default:
                        Logger.error(TAG, "Got type: " + value.getHighlightType() + " value: " + value.getValue());
                }
            }

            String id = value.getId();
            if(id != null) {
                out.name("id");
                out.value(id);
            }

            out.name("offset_secs");
            out.value(value.getHighlightPositionOffsetSecs());

            out.name("highlight_offset_secs");
            out.value(value.getStartSecs());

            out.name("highlight_length_secs");
            out.value(value.getDurationSecs());

            Float delOffsetSec = value.getDeliveredOffsetSecs();
            if(delOffsetSec != null) {
                out.name("delivered_offset_secs");
                out.value(delOffsetSec);
            }

            Float delLenSec = value.getDeliveredLengthSecs();
            if(delLenSec != null) {
                out.name("delivered_length_secs");
                out.value(delLenSec);
            }
            if (value.getVideo() != null) {
                out.name("mVideoFile");
                out.value(value.getVideo().toJsonString());
            }

            out.endObject();

        }

        @Override
        public Highlight read(JsonReader in) throws IOException {

            HighlightV2 tag = new HighlightV2();

            in.beginObject();
            while (in.hasNext()) {
                String name = in.nextName();
                if(name.equals(HIGHLIGHT_TYPE)) {
                    in.skipValue();
                }
                else if (name.equals("id")) {
                    tag.mId = in.nextString();
                } else if (name.equals("offset_secs")) {
                    tag.setOffsetSecs((float)in.nextDouble());
                } else if (name.equals("highlight_offset_secs")) {
                    tag.setStartSecs((float) in.nextDouble());

                } else if (name.equals("highlight_length_secs")) {
                    tag.setDurationSecs((float) in.nextDouble());
                } else if (name.equals("mVideoFile")) {
                    tag.mVideoFile = VideoV2.fromString(in.nextString());
                } else if (name.equals("delivered_offset_secs")) {
                    tag.mDeliveredOffsetSecs = (float) in.nextDouble();
                } else if (name.equals("delivered_length_secs")) {
                    tag.mDeliveredLengthSecs = (float) in.nextDouble();
                } else {
                    boolean isFound = false;
                    for (Type tagType : Type.values()) {
                        if (name.equals(tagType.value())) {
                            tag.mHighlightType = tagType;

                            switch (tagType) {
                                case TAG_BUTTON:
                                case TAG_MOBILE:
                                case TAG_REMOTE:
                                    tag.setValue(in.nextBoolean());
                                    break;
                                case TAG_MAX_SPEED:
                                case TAG_ACCELERATION:
                                case TAG_DECELERATION:
                                case TAG_MAX_G_FORCE:
                                case TAG_MAX_VERTICAL_SPEED:
                                    tag.setValue(in.nextDouble());
                                    break;
                                case TAG_MAX_HEART_RATE:
                                case TAG_MAX_ROTATION:
                                    tag.setValue(in.nextInt());
                                    break;
                            }
                            isFound = true;
                            break;
                        }
                    }
                    if (!isFound) {
                        Logger.exception(new Throwable("Unknown tag type: " + name));
                        tag.mHighlightType = Type.TAG_UNKNOWN;
                        in.skipValue();
                    }
                }
            }

            in.endObject();

            return tag;
        }
    }
}
