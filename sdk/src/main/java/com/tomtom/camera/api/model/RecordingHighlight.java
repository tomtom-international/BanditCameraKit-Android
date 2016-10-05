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

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.tomtom.camera.util.Logger;

import java.io.IOException;
import java.util.Date;

/**
 * This class implements highlights interface but it is used only for api calls for inserting tag
 * into video which is currently being recorded.
 * for description of functions see {@link Highlight}
 */
@JsonAdapter(RecordingHighlight.HighlightTypeAdapter.class)
public final class RecordingHighlight implements Highlight {

    private static final String TAG = "RecordingHighlight";

    /**
     * According to described functionality of the class, this field must be set to Type.TAG_MOBILE;
     */
    private Type mTagType = Type.TAG_MOBILE;
    /**
     * According to described functionality of the class, this field must be set to true;
     */
    private Object mValue = Boolean.valueOf(true);

    @Override
    public String getId() {
        return null;
    }

    @Override
    public Video getVideo() {
        return null;
    }

    @Override
    public void setVideo(Video video) {

    }

    @Override
    public Object getValue() {
        return mValue;
    }

    @Override
    public void setValue(Object value) {

    }

    @Override
    public void setSensorDataCollection(SensorDataCollection sensorDataCollection) {

    }

    @Override
    public SensorDataCollection getSensorDataCollection() {
        return null;
    }

    @Override
    public Type getHighlightType() {
        return mTagType;
    }

    @Override
    public float getStartSecs() {
        return 0;
    }

    @Override
    public void setStartSecs(float start) {

    }

    @Override
    public float getEndSecs() {
        return 0;
    }

    @Override
    public String getPlayableId() {
        return null;
    }

    @Override
    public float getStartOffsetSecs() {
        return 0;
    }

    @Override
    public float getDurationSecs() {
        return 0;
    }

    @Override
    public boolean isMuted() {
        return false;
    }

    @Override
    public RecordingHighlight clone() throws CloneNotSupportedException {
        return (RecordingHighlight) super.clone();
    }

    @Override
    public void setDurationSecs(float duration) {

    }

    @Override
    public float getHighlightPositionOffsetSecs() {
        return 0;
    }

    @Override
    public void setOffsetSecs(float offsetSecs) {

    }

    @Override
    public String getThumbnailUrl() {
        return null;
    }

    @Override
    public Float getDeliveredOffsetSecs() {
        return null;
    }

    @Override
    public Float getDeliveredLengthSecs() {
        return null;
    }

    @Override
    public int getType() {
        return HIGHLIGHT;
    }

    @Override
    public String getFileIdentifier() {
        return null;
    }

    @Override
    public String getComparableId() {
        return null;
    }

    @Override
    public String getKeyForFiltering() {
        return null;
    }

    @Override
    public Date getDateCreated() {
        return null;
    }

    @Override
    public String toJsonString() {
        return null;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public int compareTo(CameraFile another) {
        return 0;
    }

    static class HighlightTypeAdapter extends TypeAdapter<Highlight> {

        @Override
        public void write(JsonWriter out, Highlight value) throws IOException {
            out.beginObject();
            out.name(value.getHighlightType().value());

            switch (value.getHighlightType()) {
                case TAG_BUTTON:
                case TAG_MOBILE:
                case TAG_REMOTE:
                    if(value.getValue() instanceof Boolean) {
                        out.value((Boolean) value.getValue());
                    }
                    else {
                        Logger.exception(new Exception("Trying to write " + value.getValue() + " to " + value.getHighlightType() + " highlight instead of Boolean"));
                    }
                    break;
                case TAG_MAX_SPEED:
                case TAG_ACCELERATION:
                case TAG_DECELERATION:
                case TAG_MAX_G_FORCE:
                case TAG_MAX_VERTICAL_SPEED:
                    if(value.getValue() instanceof Double) {
                        out.value((Double) value.getValue());
                    }
                    else {
                        Logger.exception(new Exception("Trying to write " + value.getValue() + " to " + value.getHighlightType() + " highlight instead of Double"));
                    }
                    break;
                case TAG_MAX_HEART_RATE:
                case TAG_MAX_ROTATION:
                    if(value.getValue() instanceof Integer) {
                        out.value((Integer) value.getValue());
                    }
                    else {
                        Logger.exception(new Exception("Trying to write " + value.getValue() + " to " + value.getHighlightType() + " highlight instead of Integer"));
                    }
                    break;
                default:
                    Logger.error(TAG, "Got type: " + value.getHighlightType() + " value: " + value.getValue());
            }

            out.endObject();

        }

        @Override
        public Highlight read(JsonReader in) throws IOException {

            RecordingHighlight tag = new RecordingHighlight();

            in.beginObject();
            while (in.hasNext()) {
                String name = in.nextName();
                    boolean isFound = false;
                    for (Type tagType : Type.values()) {
                        if (name.equals(tagType.value())) {
                            tag.mTagType = tagType;

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
                        tag.mTagType = Type.TAG_UNKNOWN;
                        in.skipValue();
                    }
                }

            in.endObject();

            return tag;
        }
    }
}
