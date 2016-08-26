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

import java.util.Date;

/**
 * Camera's current date and time in ISO8601 format.
 * Datetime values used in the API (for both GET and POST methods) should be passed in one of the formats defined by ISO8601 (this standard defines several possible formats).
 * Exact format used in this API will be:
 *
 * yyyy-mm-ddThh:mm:ssZ
 *
 * For example, these are all valid datetimes:
 * 2012-03-04T12:15:40Z
 * 2012-12-31T05:12:59Z
 * 2015-09-10T23:45:12Z
 *
 * Please note that ISO8601 uses the 24-hour clock system.
 */
public interface CameraTime {
    /**
     * Provides camera's current date and time
     * @return Current camera Date
     */
    Date getDate();
}
