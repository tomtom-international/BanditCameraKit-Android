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
import com.tomtom.camera.api.notification.BackchannelNotificationType;
import com.tomtom.camera.api.notification.model.TranscodingProgressNotification;

/**
 * Concrete implementation of interface {@link TranscodingProgressNotification}
 */
class TranscodingProgressNotificationV2 implements TranscodingProgressNotification {

    @SerializedName("transcoding_progress")
    private ProgressHolder mProgressHolder;

    @Override
    public BackchannelNotificationType getNotificationType() {
        return BackchannelNotificationType.TRANSCODING_PROGRESS;
    }

    public int getProgressPercent() {
        if (mProgressHolder != null) {
            return mProgressHolder.getProgressPercent();
        }
        return -1;
    }

    private static class ProgressHolder {

        @SerializedName("progress_pct")
        int mProgressPercent;

        public int getProgressPercent() {
            return mProgressPercent;
        }

    }
}
