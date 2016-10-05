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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Full Viewfinder implementation. Before starting, you might want to set port if it differs from
 * default one (4001). After successful start, set {@link OnImageReceivedListener} which will provide
 * images for consuming.
 *
 */
public class Viewfinder {

    private static final String TAG = "Viewfinder";

    private final ViewfinderStreamServer mViewfinderStreamServer;
    private final AtomicBoolean mIsStarted = new AtomicBoolean();
    private final ImageStreamParser mImageStreamParser = new ImageStreamParser();

    private @Nullable OnImageReceivedListener mOnImageReceivedListener;

    private final ViewfinderStreamServer.OnStreamDataReceivedListener mOnStreamDataReceivedListener = new ViewfinderStreamServer.OnStreamDataReceivedListener() {
        @Override
        public void onStreamDataReceived(byte[] data) {
            mImageStreamParser.parseStream(data);
        }
    };

    private final ImageStreamParser.OnImageParsedListener mOnImageParsedListener = new ImageStreamParser.OnImageParsedListener() {
        @Override
        public void onImageParsed(float timeSecs, @Nullable byte[] image) {
            if(mOnImageReceivedListener != null) {
                mOnImageReceivedListener.onImageReceived(timeSecs, image);
            }
        }
    };

    /**
     * Default constructor which uses default ViewfinderStreamServer implementation.
     */
    public Viewfinder() {
        this(new CameraViewfinderStreamServer());
    }

    public Viewfinder(@NonNull ViewfinderStreamServer viewfinderStreamServer) {
        mViewfinderStreamServer = viewfinderStreamServer;
    }

    /**
     * Starts the viewfinder server on default port (4001) if not set different.
     * @return {@code true} if started, {@code false} if not
     */
    public boolean start(){
        return startServer();
    }

    /**
     * Stops the server.
     */
    public void stop(){
        stopServer();
    }

    /**
     * Provides port number which is used by viewfinder server.
     * @return port number.
     */
    public int getPort() {
        return mViewfinderStreamServer.getPort();
    }

    /**
     * Defines port which will be used when starting the server with {@code start()}. If you want
     * different port, have on mind you need to define it before {@code start()} is called.
     * @param port Port number which server will run on
     */
    public void setPort(int port) {
        mViewfinderStreamServer.setPort(port);
    }

    private void stopServer() {
        releaseListeners();
        mViewfinderStreamServer.stop();
    }

    private boolean startServer() {
        if(mIsStarted.compareAndSet(false, true)) {
            prepareForListening();
            boolean isStarted = mViewfinderStreamServer.start();
            if (!isStarted) {
                releaseListeners();
            }
            mIsStarted.compareAndSet(true, false);
            return isStarted;
        }
        return false;
    }

    private void prepareForListening() {
        mImageStreamParser.setOnImageParsedListener(mOnImageParsedListener);
        mViewfinderStreamServer.setOnStreamDataReceivedListener(mOnStreamDataReceivedListener);
    }

    private void releaseListeners() {
        mImageStreamParser.setOnImageParsedListener(null);
        mViewfinderStreamServer.setOnStreamDataReceivedListener(null);
    }

    /**
     * Provides information if Viewfinder is running/listening.
     * @return {@code true} if running, {@code false} if not
     */
    public boolean isRunning() {
        return mViewfinderStreamServer.isRunning();
    }

    /**
     * Sets {@link OnImageReceivedListener} to Viewfinder in order to get JPG images from camera
     * viewfinder stream. To protect from leaking references, set to null when needed.
     * @param onImageReceivedListener Listener implementation
     */
    public void setOnImageReceivedListener(OnImageReceivedListener onImageReceivedListener) {
        mOnImageReceivedListener = onImageReceivedListener;
    }

    public interface OnImageReceivedListener {
        void onImageReceived(float timeSecs, @Nullable byte[] image);
    }
}