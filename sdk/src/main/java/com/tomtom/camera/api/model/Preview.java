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

/**
 * Preview model corresponding to Camera API Preview model. Used for starting and stopping preview.
 */
public interface Preview {
    /**
     * Provides id of the video which we want to preview
     * @return
     */
    String getVideoId();

    /**
     * Provides starting position for preview.
     * @return Position defined in seconds.
     */
    Float getStartPositionSecs();

    /**
     * Provides information if we want to start or stop preview.
     * @return {@code true} if start, {@code false} if stop.
     */
    boolean isActive();

    /**
     * Provides information about port which we want preview to be streamed to
     * @return Preview port number.
     */
    int getPreviewPort();

    /**
     * Provides information about duration of preview from the start position.
     * @return Duration of preview in seconds.
     */
    Float getDurationSecs();
}
