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

import android.graphics.Bitmap;

/**
 * This interface defines functions for retrieving information regarding
 * thumbnail (Bitmap) from video file identified by it's video_id.
 * Thumbnail is taken at the time specified by offset input parameter.
 */
public interface Thumbnail {

    /**
     * Returns value which represents number of seconds (float value) from beginning of a video
     * from where thumbnail should be obtained.
     * @return float value of offset in seconds
     */
    float getOffsetSecs();

    /**
     * Returns value of video id, which identifies video file,
     * from where thumbnail should be obtained.
     * @return String value of video id
     */
    String getVideoId();

    /**
     * Returns value of Thumbnail Bitmap
     * @return Bitmap instance
     */
    Bitmap getThumbBitmap();
}
