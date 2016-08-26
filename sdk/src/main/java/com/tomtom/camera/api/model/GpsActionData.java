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
 * Provides information about GpsActionData provided by GPS sensor in video footage.
 */
public interface GpsActionData {

    /**
     * Describes possible modes for GPS data.
     */
    enum Mode {
        NO_FIX("NoFix"),
        NOT_SEEN("NotSeen"),
        FIX_2D("2D"),
        FIX_3D("3D");

        String mVal;

        Mode(String value) {
            mVal = value;
        }

        public String value() {
            return mVal;
        }

        public static Mode fromString(String value) {
            for(Mode mode : values()) {
                if(mode.mVal.equals(value)) {
                    return mode;
                }
            }
            return null;
        }

        /**
         * Provides information about data validity based on GPS mode.
         * @return {@code true} if fix was obtained, {@code false} if not.
         */
        public boolean isValidForData(){
            return this == FIX_2D || this == FIX_3D;
        }

    }

    /**
     * Provides information about GPS mode for data
     * @return GpsActionData {@link Mode} enum value
     */
    Mode getMode();

    /**
     * Provides information about current speed
     * @return Speed defined in mps
     */
    String getSpeedMps();

    /**
     * Provides information about current altitude
     * @return Altitude in meters
     */
    String getAltitudeMeters();

    /**
     * Provides information about current latitude
     * @return Latitude in degrees
     */
    String getLatitude();

    /**
     * Provides information about current longitude
     * @return Longitude in degrees
     */
    String getLongitude();

    /**
     * Course made good (relative to true north)
     * @return Degrees from true north
     */
    String getTrack();


    String getHead();

    /**
     * Provides information about vertical position uncertainty
     * @return Vertical position uncertainty in meters
     */
    String getVerticalUncertaintyMeters();

    /**
     * Provides information about latitude uncertainty
     * @return Latitude uncertainty in meters
     */
    String getLatUncertaintyMeters();

    /**
     * Provides information about longitude uncertainty
     * @return Longitude uncertainty in meters
     */
    String getLongUncertaintyMeters();

    /**
     * Provides information about track uncertainty
     * @return Track uncertainty in meters
     */
    String getTrackUncertaintyMeters();
}
