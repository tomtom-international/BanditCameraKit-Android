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

import com.tomtom.camera.api.model.Video;
import com.tomtom.camera.api.notification.BackchannelNotification;

import java.util.ArrayList;

/**
 * This interface correspondents to event from camera : recording stopped  and
 * defines function that provides access to recorded videos
 */
public interface RecordingStoppedNotification extends BackchannelNotification {

    /**
     * Returns list of recorded videos.If multiple videos are recorded within one session,
     * information about every recorded video will be returned in this list of objects.
     * @return list of {@link Video}
     */
    ArrayList<? extends Video> getVideos();
}
