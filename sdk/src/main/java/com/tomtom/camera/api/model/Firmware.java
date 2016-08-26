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
 * Provides information about camera's current and possibly pending Firmware version which will be
 * installed after restarting the camera.
 */
public interface Firmware {
    int getMajor();
    int getMinor();
    int getRevision();
    int getBuild();

    /**
     * @return 0 if there's no pending firmware
     */
    int getPendingMajor();

    /**
     * @return 0 if there's no pending firmware
     */
    int getPendingMinor();

    /**
     * @return 0 if there's no pending firmware
     */
    int getPendingRevision();

    /**
     * @return 0 if there's no pending firmware
     */
    int getPendingBuild();

    /**
     * Provides current firmware version as a string
     * @return String in format major.minor.revision.build
     */
    String getCurrent();

    /**
     * Provides pending firmware version as a string
     * @return String in format major.minor.revision.build. If there's no pending firmware, it will be
     * "0.0.0.0"
     */
    String getPending();
}
