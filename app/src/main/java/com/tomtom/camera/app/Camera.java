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

package com.tomtom.camera.app;

import com.tomtom.camera.api.CameraApi;
import com.tomtom.camera.api.CameraApiCallback;
import com.tomtom.camera.api.model.CameraApiVersion;

/**
 * Holds version of camera, provides function for initialisation and creation of REST camera Api
 * and getter function for retrieving it
 */

public class Camera {

    private static CameraApi mCameraApi;
    private static CameraApiVersion mCameraApiVersion;

    /**
     * Function for initialisation and creation of REST camera Api. Must be called before rest any
     * api call
     * @param callback
     */
    public static void init(final OnCameraInitCallback callback) {
        CameraApi.Version.getApiVersion(new CameraApiCallback<CameraApiVersion>() {
            @Override
            public void success(CameraApiVersion cameraApiVersion) {
                mCameraApiVersion = cameraApiVersion;
                mCameraApi = CameraApi.Factory.create(cameraApiVersion);
                if (callback != null) {
                    callback.onCameraInitialisationFinished(true);
                }
            }

            @Override
            public void error(int statusCode) {
                callback.onCameraInitialisationFinished(false);
            }

            @Override
            public void failure(Throwable throwable) {
                callback.onCameraInitialisationFinished(false);
            }
        });
    }

    /**
     * Returns object which is used for REST api calls
     * @return {@link CameraApi}
     */
    public static CameraApi getCameraApi() {
        return mCameraApi;
    }

    /**
     * Returns CameraApiVersion object, which contains information regarding api version, revision
     * @return {@link CameraApiVersion}
     */
    public static CameraApiVersion getCameraApiVersion() {
        return mCameraApiVersion;
    }

    /**
     * Interface which defines callback function, called when camera initialisation finished
     */
    public interface OnCameraInitCallback {
        void onCameraInitialisationFinished(boolean isSuccess);
    }
}
