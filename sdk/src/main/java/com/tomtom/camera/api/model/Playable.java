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
 * Interface used for preview, implemented by {@link Video} and {@link Highlight}
 */
public interface Playable <T extends Playable> extends Cloneable {
    /**
     * Provides playable id. Reffers to video identifier.
     * @return id of Playable
     */
    String getPlayableId();

    /**
     * Provides exact point in video from which should be played.
     * @return offset in seconds.
     */
    float getStartOffsetSecs();

    /**
     * Provides how long preview should playe from starting point.
     * @return Duration in seconds
     */
    float getDurationSecs();

    /**
     * Provides information about sound in video.
     * @return {@code true} if muted, {@code false} if sound should be played
     */
    boolean isMuted();

    T clone() throws CloneNotSupportedException;
}
