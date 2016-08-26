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

package com.tomtom.camera.api.notification;

import java.util.HashMap;

/**
 *  Enum which corresponds to available notifications sent from camera.
 */
public enum BackchannelNotificationType {

    RECORDING_STARTED("recording_started"),
    RECORDING_STOPPED("recording_stopped"),
    TAG_CREATED("tag_created"),
    PHOTO_CAPTURED("photo_captured"),
    MEMORY_LOW("memory_low"),
    WIFI_STOPPED("wifi_stopped"),
    TRANSCODING_PROGRESS("transcoding_progress"),
    VIEWFINDER_STARTED("viewfinder_started"),
    VIEWFINDER_STOPPED("viewfinder_stopped"),
    SHUTTING_DOWN("shutting_down"),
    MEMORY_ERROR("memory_error");

    private String mVal;

    static final HashMap<String, BackchannelNotificationType> sLookUpMap = new HashMap<>();

    static {
        sLookUpMap.put(RECORDING_STARTED.value(), RECORDING_STARTED);
        sLookUpMap.put(RECORDING_STOPPED.value(), RECORDING_STOPPED);
        sLookUpMap.put(TAG_CREATED.value(), TAG_CREATED);
        sLookUpMap.put(PHOTO_CAPTURED.value(), PHOTO_CAPTURED);
        sLookUpMap.put(MEMORY_LOW.value(), MEMORY_LOW);
        sLookUpMap.put(WIFI_STOPPED.value(), WIFI_STOPPED);
        sLookUpMap.put(TRANSCODING_PROGRESS.value(), TRANSCODING_PROGRESS);
        sLookUpMap.put(VIEWFINDER_STARTED.value(), VIEWFINDER_STARTED);
        sLookUpMap.put(VIEWFINDER_STOPPED.value(), VIEWFINDER_STOPPED);
        sLookUpMap.put(SHUTTING_DOWN.value(), SHUTTING_DOWN);
    }

    private BackchannelNotificationType(String name){
        mVal = name;
    }

    /**
     * Returns value as {@link String}.
     * @return String representation of {@link BackchannelNotificationType}
     */
    public String value(){
        return mVal;
    }

    /**
     * Returns {@link BackchannelNotificationType} instance which corresponds to given string.
     * @param value String representation of notification type (e.g. recording_started)
     * @return {@link BackchannelNotificationType} instance if it's defined by enum. If not, returns null.
     */
    public static BackchannelNotificationType fromString(String value) {
        return sLookUpMap.get(value);
    }
}
