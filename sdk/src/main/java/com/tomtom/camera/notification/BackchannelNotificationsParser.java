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

package com.tomtom.camera.notification;

import com.tomtom.camera.api.model.CameraApiVersion;
import com.tomtom.camera.api.notification.BackchannelNotification;
import com.tomtom.camera.api.v2.BackChannelNotificationParserV2;

import org.json.JSONException;

/**
 * This interface defines function for parsing JSON string of backchannel notification sent
 * from camera
 */
public interface BackchannelNotificationsParser {

    /**
     * Returns parsed backchannel notification from JSON string  sent from camera
     * @param messageString JOSN string
     * @return {@link BackchannelNotification} value
     */
    BackchannelNotification parse(String messageString) throws JSONException;

    class Creator {

        /**
         * Creates BackchannelNotificationsParser instance based on provided {@link CameraApiVersion}.
         * If {@link CameraApiVersion} is null or not supported, it will return null.
         * @param cameraApiVersion CameraApiVersion instance
         * @return {@link BackchannelNotificationsParser} instance or null
         */
        public static BackchannelNotificationsParser newInstance(CameraApiVersion cameraApiVersion) {
            switch (cameraApiVersion.getVersion()) {
                case CameraApiVersion.V2:
                    return new BackChannelNotificationParserV2();
                default:
                    return null;
            }
        }
    }
}
