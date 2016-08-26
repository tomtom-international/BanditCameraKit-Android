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

package com.tomtom.camera.api.v2;

import com.google.gson.annotations.SerializedName;
import com.tomtom.camera.api.notification.BackchannelNotificationType;
import com.tomtom.camera.api.notification.model.PhotoCapturedNotification;

import java.util.ArrayList;

/**
 * Concrete implementation of interface {@link PhotoCapturedNotification}
 */
class PhotoCapturedNotificationV2 implements PhotoCapturedNotification {

    @SerializedName("photo_captured")
    ArrayList<ImageV2> mImagesList;

    public PhotoCapturedNotificationV2() {

    }

    @Override
    public BackchannelNotificationType getNotificationType() {
        return BackchannelNotificationType.PHOTO_CAPTURED;
    }

    public String[] getPhotoIds() {
        String[] photoIds = new String[mImagesList.size()];
        int size = mImagesList.size();
        for (int i = 0; i < size; i++) {
            photoIds[i] = mImagesList.get(i).getFileIdentifier();
        }
        return photoIds;
    }

    @Override
    public ArrayList<ImageV2> getImages() {
        return mImagesList;
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        String[] photoIds = getPhotoIds();
        for (String s : photoIds) {
            ret.append(s).append(",");
        }
        return ret.toString();
    }

}
