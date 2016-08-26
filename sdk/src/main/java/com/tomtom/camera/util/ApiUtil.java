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

package com.tomtom.camera.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.tomtom.camera.api.model.Highlight;
import com.tomtom.camera.api.model.Image;
import com.tomtom.camera.api.model.Video;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Api util class. Provides Gson for CameraApi and other useful Gson serializers/deserializers.
 */
public class ApiUtil {

    private static final String TAG = "ApiUtil";

    public static final String DEFAULT_DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final String[] SUPPORTED_DATE_FORMAT_PATTERNS = {DEFAULT_DATE_FORMAT_PATTERN, "MMM dd,yyyy HH:mm:ss", "EEE MMM dd HH:mm:ss yyyy", "yyyy-MM-dd'T'HH:mm:ssZ", "yyyy-MM-dd'T'HH24:mm:ss", "yyyy-MM-dd'T'HH:mm:ss'Z'", "EEE MMM dd HH:mm:ss ZZZ yyyy", "yyyy:MM:dd HH:mm:ss"};

    /**
     * Provides {@link Gson} instance with handling date formats.
     * @return
     */
    public static final Gson getDateHandlingGson() {
        return new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateJsonDeserializer())
                .registerTypeAdapter(Date.class, new DateJsonSerializer())
                .registerTypeAdapter(Highlight.class, new HighlightJsonDeserializer())
                .registerTypeAdapter(Video.class, new VideoJsonDeserializer())
                .registerTypeAdapter(Image.class, new ImageJsonDeserializer())
                .create();
    }

    /**
     * Deserializer which takes all possible date formats into account when deserializing date
     */
    public static class DateJsonDeserializer implements JsonDeserializer<Date> {
        @Override
        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            SimpleDateFormat simpleDateFormat;
            for (String format : SUPPORTED_DATE_FORMAT_PATTERNS) {
                try {
                    simpleDateFormat = new SimpleDateFormat(format, Locale.US);
                    simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    return simpleDateFormat.parse(json.getAsString());
                } catch (ParseException e) {
                }
                try {
                    simpleDateFormat = new SimpleDateFormat(format, Locale.getDefault());
                    simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    return simpleDateFormat.parse(json.getAsString());
                } catch (ParseException e) {

                }
            }
            throw new JsonParseException("Unparseable date: \"" + json.getAsString()
                    + "\". Supported formats: " + Arrays.toString(SUPPORTED_DATE_FORMAT_PATTERNS));
        }
    }

    /**
     * Serializer which takes {@link Date} instance and serializes it into default date format string.
     */
    public static class DateJsonSerializer implements JsonSerializer<Date> {
        @Override
        public JsonElement serialize(Date date, Type typeOfDate, JsonSerializationContext context) {
            SimpleDateFormat defaultDateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT_PATTERN, Locale.US);
            defaultDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            String dateString = defaultDateFormat.format(date);
            return date == null ? null : new JsonPrimitive(dateString);
        }
    }

    public static class HighlightJsonDeserializer implements JsonDeserializer<Highlight> {
        @Override
        public Highlight deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            String classPath = json.getAsJsonObject().get(Highlight.HIGHLIGHT_TYPE).getAsString();
            if (classPath.contains("HighlightV2")) {
                classPath = "com.tomtom.camera.api.v2.HighlightV2";
            }
            try {
                Class<Highlight> cls = (Class<Highlight>) Class.forName(classPath);
                return (Highlight) context.deserialize(json, cls);
            }
            catch (ClassNotFoundException e) {
                Logger.error(TAG, e.getMessage());
            }

            return null;
        }
    }

    public static class VideoJsonDeserializer implements JsonDeserializer<Video> {
        @Override
        public Video deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            String classPath = json.getAsJsonObject().get(Video.CLASS_TYPE).getAsString();
            if (classPath.contains("VideoV2")) {
                classPath = "com.tomtom.camera.api.v2.VideoV2";
            }
            try {
                Class<Video> cls = (Class<Video>) Class.forName(classPath);
                return (Video) context.deserialize(json, cls);
            }
            catch (ClassNotFoundException e) {
                Logger.error(TAG, e.getMessage());
            }

            return null;
        }
    }

    public static class ImageJsonDeserializer implements JsonDeserializer<Image> {
        @Override
        public Image deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            String classPath = json.getAsJsonObject().get(Image.IMAGE_CLASS_TYPE).getAsString();
            if (classPath.contains("ImageV2")){
                classPath = "com.tomtom.camera.api.v2.ImageV2";
            }
            try {
                Class<Image> cls = (Class<Image>) Class.forName(classPath);
                return (Image) context.deserialize(json, cls);
            }
            catch (ClassNotFoundException e) {
                Logger.error(TAG, e.getMessage());
            }

            return null;
        }
    }

}
