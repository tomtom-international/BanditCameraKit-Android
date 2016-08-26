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

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Date;

/**
 * Parent interface for all camera file types.
 */
public interface CameraFile extends Comparable<CameraFile> {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({VIDEO, PHOTO, HIGHLIGHT, PHOTO_BURST, CONTINUOUS })
    @interface Type{}
    int VIDEO = 0;
    int PHOTO = 1;
    int HIGHLIGHT = 2;
    int PHOTO_BURST = 3;
    int CONTINUOUS = 4;

    /**
     * Returns type of CameraFile defined in {@link Type}
     * @return {@link Type} defined as int
     */
    @CameraFile.Type int getType();

    /**
     * Returns file id.
     * @return
     */
    String getFileIdentifier();

    /**
     * Returns id which will be used for comparing. For some types, this might not be the same as
     * file identifier.
     *
     * @return
     */
    String getComparableId();

    /**
     * Returns string which would be used for filtering camera files of same type. For example,
     * {@link Highlight} would be filtered by {@link Highlight.Type}.
     * @return Key as string used for filtering.
     */
    String getKeyForFiltering();

    /**
     * 	Date and time of file creation
     * @return {@link Date} of creation
     */
    Date getDateCreated();

    /**
     * Provides serialized CameraFile as JSON
     * @return String JSON of CameraFile object
     */
    String toJsonString();

    /**
     * Check if CameraFile is corrupted or not
     * @return {@code true} if valid, {@code false} if corrupted
     */
    boolean isValid();
}
