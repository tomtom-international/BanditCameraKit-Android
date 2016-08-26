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

import com.tomtom.camera.api.model.Image;
import com.tomtom.camera.api.notification.BackchannelNotification;

import java.util.ArrayList;

/**
 * This interface correspondents to event from camera : photo captured  and
 * defines function that provides access to information about captured photo,
 * encapsulated in Image model
 */
public interface PhotoCapturedNotification extends BackchannelNotification {

    /**
     * Returns list of Strings, containing the id's of captured photos
     * @return list of {@link String}
     */
    String[] getPhotoIds();

    /**
     * Returns list of objects, containing the information about captured photos
     * @return list of {@link Image}
     */
    ArrayList<? extends Image> getImages();

}
