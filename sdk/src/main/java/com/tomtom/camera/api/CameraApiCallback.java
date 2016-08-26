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

package com.tomtom.camera.api;

/**
 * Generic callback used for all calls to camera api server.
 */
public interface CameraApiCallback<T> {
    /**
     * If request was made successfully, this callback will be triggered providing object defined with type.
     * @param t
     */
    void success(T t);

    /**
     * Triggered if there was error handling request with http status code.
     * @param statusCode http status code.
     */
    void error(int statusCode);

    /**
     * Triggered if request failed.
     * @param t Throwable which caused fail.
     */
    void failure(Throwable t);
}
