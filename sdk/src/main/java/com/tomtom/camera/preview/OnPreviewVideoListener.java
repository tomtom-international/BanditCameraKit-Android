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

package com.tomtom.camera.preview;

/**
 * Callbacks providing state of preview video
 */
public interface OnPreviewVideoListener {
    /**
     * Provides total length of the video.
     * @param totalLengthMillis total length in millis.
     */
    void onTotalLengthSet(int totalLengthMillis);

    /**
     * Provides progress of preview so UI can be set accordingly. It's used against total length.
     * @param currentMillis current preview time in millis
     */
    void onPreviewTimeProgress(int currentMillis);

    /**
     * Start time of the preview in total length.
     * @param startMillis starting time in millis
     */
    void onPreviewStarted(int startMillis);

    /**
     * Callback for preview end (explicit stop or end of play)
     */
    void onEndReceived();
}
