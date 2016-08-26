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
import com.tomtom.camera.api.notification.BackchannelNotification;
import com.tomtom.camera.api.notification.BackchannelNotificationType;
import com.tomtom.camera.notification.BackchannelNotificationsParser;
import com.tomtom.camera.util.ApiUtil;
import com.tomtom.camera.util.Logger;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Concrete implementation of interface {@link BackchannelNotificationsParser}
 */
public class BackChannelNotificationParserV2 implements BackchannelNotificationsParser {

    private static String TAG = "BackchannelNotificationsParser";

    static final String[] DATE_FORMATS = {"EEE MMM dd HH:mm:ss yyyy", "yyyy-MM-dd'T'HH:mm:ssZ", "yyyy-MM-dd'T'HH24:mm:ss", "yyyy-MM-dd'T'HH:mm:ss'Z'", "EEE MMM dd HH:mm:ss ZZZ yyyy"};

    private Gson mGson;

    public BackChannelNotificationParserV2() {
        mGson = ApiUtil.getDateHandlingGson();
    }

    public BackchannelNotification parse(String messageString) throws JSONException {

        boolean isOneObjectFound = false;
        int indexOfFirstObject = -1;
        int indexOfSecondObject = -1;
        for (BackchannelNotificationType notificationType : BackchannelNotificationType.values()) {
            int indexOfCurrentlyChecked = messageString.indexOf(notificationType.value());
            if (indexOfCurrentlyChecked != -1) {
                if (isOneObjectFound) {
                    indexOfSecondObject = indexOfCurrentlyChecked;
                    break;
                } else {
                    indexOfFirstObject = indexOfCurrentlyChecked;
                    isOneObjectFound = true;
                }
            }
        }

        if (indexOfFirstObject != -1 && indexOfSecondObject != -1) {
            Logger.exception(new Throwable("Found more than one object in notification string: " + messageString));
            messageString = "{\"" + messageString.substring(indexOfFirstObject > indexOfSecondObject ? indexOfFirstObject : indexOfSecondObject);
        }

        JSONObject parentObject = new JSONObject(messageString);
        for (BackchannelNotificationType notificationType : BackchannelNotificationType.values()) {
            if (parentObject.has(notificationType.value())) {
                switch(notificationType) {
                    case RECORDING_STARTED:
                        return mGson.fromJson(parentObject.toString(), RecordingStartedNotificationV2.class);
                    case RECORDING_STOPPED:
                        return mGson.fromJson(parentObject.toString(), RecordingStoppedNotificationV2.class);
                    case MEMORY_LOW:
                        return mGson.fromJson(parentObject.toString(), MemoryLowNotificationV2.class);
                    case PHOTO_CAPTURED:
                        return mGson.fromJson(parentObject.toString(), PhotoCapturedNotificationV2.class);
                    case SHUTTING_DOWN:
                        return mGson.fromJson(parentObject.toString(), ShuttingDownNotificationV2.class);
                    case TAG_CREATED:
                        return mGson.fromJson(parentObject.toString(), HighlightCreatedNotificationV2.class);
                    case TRANSCODING_PROGRESS:
                        return mGson.fromJson(parentObject.toString(), TranscodingProgressNotificationV2.class);
                    case VIEWFINDER_STARTED:
                        return mGson.fromJson(parentObject.toString(), ViewfinderStartedNotificationV2.class);
                    case VIEWFINDER_STOPPED:
                        return mGson.fromJson(parentObject.toString(), ViewfinderStoppedNotificationV2.class);
                    case WIFI_STOPPED:
                        return mGson.fromJson(parentObject.toString(), WiFiStoppedNotificationV2.class);
                    case MEMORY_ERROR:
                        return mGson.fromJson(parentObject.toString(), MemoryErrorNotificationV2.class);
                }
            }
        }

        return null;
    }
}
