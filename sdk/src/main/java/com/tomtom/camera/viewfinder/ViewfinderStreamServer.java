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

package com.tomtom.camera.viewfinder;

import android.support.annotation.Nullable;

import com.tomtom.camera.util.Logger;

/**
 * Viewfinder stream server abstraction which provides interface for communication between
 * stream consumer as {@link OnStreamDataReceivedListener} and camera as provider.
 */
public abstract class ViewfinderStreamServer {

    private static final String TAG = "VFStreamServer";

    protected int mPort;
    protected @Nullable OnStreamDataReceivedListener mOnStreamDataReceivedListener;

    /**
     * Consumer of Viewfinder stream should provide implementation of {@link OnStreamDataReceivedListener}
     * to ViewfinderStreamSertver in order to be able to receive data. It can be set to null in order
     * to remove reference and avoid memory leak.
     * @param onStreamDataReceivedListener Listener implementation
     */
    public void setOnStreamDataReceivedListener(@Nullable OnStreamDataReceivedListener onStreamDataReceivedListener) {
        Logger.debug(TAG, "setOnStreamDataReceivedListener " + onStreamDataReceivedListener);
        mOnStreamDataReceivedListener = onStreamDataReceivedListener;
    }

    /**
     * Starts Viewfinder server and listens for camera viewfinder stream.
     * @return {@code true} if started, {@code false} if not
     */
    public abstract boolean start();

    /**
     * Stops Viewfinder server.
     */
    public abstract void stop();

    /**
     * Provides information about server state.
     * @return {@code true} if running, {@code false} if not.
     */
    public abstract boolean isRunning();

    /**
     * Defines port which will be used when starting the server with {@code start()}. If you want
     * different port, have on mind you need to define it before {@code start()} is called.
     * @param port Port number which server will run on
     */
    public void setPort(int port) {
        mPort = port;
    }

    /**
     * Provides port number which is used by server.
     * @return port number.
     */
    public int getPort() {
        return mPort;
    }

    /**
     * ViewfinderStreamServer consumer abstraction. Provides data received by server to consumer.
     */
    public interface OnStreamDataReceivedListener {
        void onStreamDataReceived(byte[] data);
    }
}
