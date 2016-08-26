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
 * /**
 * This interface correspondents to status of view finder of camera and defines function to retrieve it
 */

public interface ViewfinderStatus {

    /**
     * Returns true if view finder is active
     *
     * @return true if view finder is active
     */
    boolean isActive();

    /**
     * Returns value which represents port number on which view finder is active
     *
     * @return int value of streaming port
     */
    int getStreamingPort();

    class Creator {
        public static ViewfinderStatus createStart(final int port) {
            return new ViewfinderStatus() {
                @Override
                public boolean isActive() {
                    return true;
                }

                @Override
                public int getStreamingPort() {
                    return port;
                }
            };
        }

        public static ViewfinderStatus createStop() {
            return new ViewfinderStatus() {
                @Override
                public boolean isActive() {
                    return false;
                }

                @Override
                public int getStreamingPort() {
                    return 0;
                }
            };
        }
    }
}
