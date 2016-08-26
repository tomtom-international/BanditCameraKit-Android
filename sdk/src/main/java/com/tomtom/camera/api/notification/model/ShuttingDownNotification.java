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

package com.tomtom.camera.api.notification.model;

import com.tomtom.camera.api.notification.BackchannelNotification;

/**
 * This interface correspondents to event from camera : camera shut down  and
 * defines function that provides access to information of the cause
 */
public interface ShuttingDownNotification extends BackchannelNotification {

    /**
     * Returns true, if battery is the reason for camera shut down, false if not
     * @return boolean
     */
    boolean isBatteryCritical();

    /**
     * Returns true, if user initiated camera shut down, false if not
     * @return boolean
     */
    boolean isUserInitiated();
    /**
     * Returns true, if overheating is the reason for camera shut down, false if not
     * @return boolean
     */
    boolean isOverheating();
}
