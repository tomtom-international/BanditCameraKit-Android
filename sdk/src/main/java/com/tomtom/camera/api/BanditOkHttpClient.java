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

import android.support.annotation.NonNull;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.ConnectionPool;
import com.squareup.okhttp.Dispatcher;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import java.util.concurrent.TimeUnit;

/**
 * Custom OkHttpClient used for communicating with Camera Api. Provides cancelling of download request
 * without directly owning/referencing a {@link Call}
 */
public class BanditOkHttpClient extends OkHttpClient {

    public static final int MAX_IDLE_HTTP_CONNECTIONS = 1;
    public static final int KEEP_ALIVE_TIMEOUT_MS = 1;
    private static final int MAX_CAMERA_HTTP_CONNECTIONS = 4;
    private static final int HTTP_READ_TIMEOUT_SECONDS = 16;

    private final static BanditOkHttpClient sSharedInstance;

    static {
        sSharedInstance = new BanditOkHttpClient();
        sSharedInstance.setReadTimeout(HTTP_READ_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        sSharedInstance.setConnectionPool(new ConnectionPool(MAX_IDLE_HTTP_CONNECTIONS, KEEP_ALIVE_TIMEOUT_MS));
        Dispatcher dispatcher = sSharedInstance.getDispatcher();
        dispatcher.setMaxRequests(MAX_CAMERA_HTTP_CONNECTIONS);
        sSharedInstance.setDispatcher(dispatcher);
    }

    Request mCurrentDownloadRequest;

    @Override
    public Call newCall(@NonNull Request request) {
        if (isDownloadRequest(request)) {
            mCurrentDownloadRequest = request;
        }
        return super.newCall(request);
    }

    private boolean isDownloadRequest(@NonNull Request request) {
        return request.urlString().contains("/contents");
    }

    /**
     * Cancels current download request.
     */
    public void cancelCurrentDownloadRequest() {
        cancel(mCurrentDownloadRequest);
    }

    /**
     * Provides shared instance of {@link BanditOkHttpClient}
     * @return {@link BanditOkHttpClient} instance
     */
    public static BanditOkHttpClient getSharedInstance() {
        return sSharedInstance;
    }
}
