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
import com.tomtom.camera.api.notification.model.RecordingStartedNotification;

/**
 * Concrete implementation of interface {@link RecordingStartedNotification}
 */
class RecordingStartedNotificationV2 implements RecordingStartedNotification {

    @SerializedName("recording_started")
    private RecordingActive mRecordingActive;

    public RecordingStartedNotificationV2() {

    }

    @Override
    public BackchannelNotificationType getNotificationType() {
        return BackchannelNotificationType.RECORDING_STARTED;
    }

    @Override
    public boolean isRecordingActive() {
        if (mRecordingActive != null) {
            return mRecordingActive.isRecordingActive();
        }
        return false;
    }

    @Override
    public String toString() {
        return "Recording active: " + (mRecordingActive != null && mRecordingActive.isRecordingActive());
    }

    private static class RecordingActive {

        @SerializedName("recording_active")
        boolean mIsRecordingActive;

        public boolean isRecordingActive() {
            return mIsRecordingActive;
        }
    }
}
