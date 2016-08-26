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

package com.tomtom.camera.preview;

import com.tomtom.camera.util.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okio.BufferedSource;
import okio.Okio;
import okio.Source;

/**
 * CameraPreview stream server implementation.
 */
class CameraPreviewStreamServer {

    private static final String TAG = "PreviewStreamServer";

    private static final int MIN_PORT = 4010;
    private static final int MAX_PORT = 4999;
    private static final int BUFFER_SIZE = 65536;

    private int mPort = MIN_PORT;
    private boolean mIsBufferingRunning;

    private ServerSocket mPreviewServerSocket;
    private Socket mSocket;
    private boolean mWaitingForFirstVideoFrame;
    private boolean mWaitingForFirstAudioFrame;

    private ExecutorService mServerBackgroundExecutor = Executors.newSingleThreadExecutor();
    private ExecutorService mQueueExecutor = Executors.newSingleThreadExecutor();
    private OnEosReceivedListener mOnEosReceivedListener;

    private final PreviewBuffer mPreviewBuffer;

    private final Runnable mServerRunnable = new Runnable() {
        @Override
        public void run() {
            while (mIsBufferingRunning) {
                try {
                    mSocket = mPreviewServerSocket.accept();
                    Source source = Okio.source(mSocket);
                    BufferedSource preview = Okio.buffer(source);
                    mWaitingForFirstVideoFrame = true;
                    mWaitingForFirstAudioFrame = true;

                    Logger.debug(TAG, "Connection established with " + mSocket.getRemoteSocketAddress().toString() + " from local port  " + mSocket.getLocalPort());

                    while (mIsBufferingRunning) {
                        if (mPreviewBuffer != null
                                && mPreviewBuffer.getBufferState() != StreamBuffer.BufferState.FULL) {
                            final int version = preview.readInt();
                            int frameType = preview.readInt();
                            final Frame.Type fType = Frame.Type.fromValue(frameType);
                            final int pts;
                            final int status;
                            final byte[] data;
                            if (fType == Frame.Type.AUDIO || fType == Frame.Type.VIDEO) {
                                int dataLen = preview.readInt();
                                pts = preview.readInt();
                                status = preview.readInt();
                                data = preview.readByteArray(dataLen);
                            }
                            // Reading rest of header for reserved part used in EOS/SOS
                            else {
                                preview.readByteArray(Frame.HEADER_LENGTH);
                                pts = 0;
                                status = 0;
                                data = null;
                            }
                            //TODO Write SOS to buffer when we start handling it
                            if (fType != Frame.Type.SOS) {
                                mQueueExecutor.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        Frame frame = new Frame(version, fType, pts, status, data);

                                        if (fType == Frame.Type.VIDEO && mWaitingForFirstVideoFrame) {
                                            mWaitingForFirstVideoFrame = false;
                                            frame.setIsFirstFrame(true);
                                        } else if (fType == Frame.Type.AUDIO && mWaitingForFirstAudioFrame) {
                                            mWaitingForFirstAudioFrame = false;
                                            frame.setIsFirstFrame(true);
                                        }
                                        mPreviewBuffer.queue(frame);

                                        if (fType == Frame.Type.EOS) {
                                            mWaitingForFirstVideoFrame = true;
                                            mWaitingForFirstAudioFrame = true;
                                            mPreviewBuffer.resetVideoOnlyState();
                                            if (mOnEosReceivedListener != null) {
                                                mOnEosReceivedListener.onEosReceived();
                                            }
                                        }
                                    }
                                });

                                if (fType == Frame.Type.EOS) {
                                    break;
                                }

                            }
                        }
                    }
                } catch (IOException e) {
                    mIsBufferingRunning = false;
                    Logger.error(TAG, "Socket connection error : " + e.getMessage());
                }
            }
        }
    };

    public CameraPreviewStreamServer(PreviewBuffer previewBuffer) {
        this(previewBuffer, null);
    }

    public CameraPreviewStreamServer(PreviewBuffer previewBuffer, OnEosReceivedListener onPreviewStreamListener){
        mPreviewBuffer = previewBuffer;
        mOnEosReceivedListener = onPreviewStreamListener;
    }

    /**
     *
     * @param onEosReceivedListener
     */
    void setOnEosReceivedListener(OnEosReceivedListener onEosReceivedListener) {
        mOnEosReceivedListener = onEosReceivedListener;
    }

    boolean start() {
        if(!mIsBufferingRunning) {
            mIsBufferingRunning = true;
            if (startServer()) {
                Logger.info(TAG, "Client started listening on port : " + mPort);
                return true;
            } else {
                Logger.info(TAG, "Client could listen on any port");
                return false;
            }
        }
        else {
            Logger.info(TAG, "Client already running on port : " + mPort);
            return false;
        }
    }

    void stop(){
        mIsBufferingRunning = false;
        try{
            if (mSocket != null) {
                mSocket.close();
            }
            if (mPreviewServerSocket != null){
                mPreviewServerSocket.close();
            }
            if (mPreviewBuffer != null) {
                mPreviewBuffer.clear();
            }
        }
        catch (IOException e){
            Logger.error(TAG, "Error closing socket: " + e.getMessage());
        }
    }

    boolean isRunning() {
        return mIsBufferingRunning;
    }

    private boolean startServer(){
        //always start on a different port, so we don't get some old information
        mPort++;
        if (mPort == MAX_PORT){
            mPort = MIN_PORT;
        }
        for(int port = mPort; port <= MAX_PORT; port++){
            try{
                mPreviewServerSocket = new ServerSocket(port);
                Logger.debug(TAG, "Server socket opened on " + port);
                mPreviewServerSocket.setReceiveBufferSize(BUFFER_SIZE);
                Logger.debug(TAG, "Socket buffer size set to " + BUFFER_SIZE);
                mPort = port;
                mServerBackgroundExecutor.execute(mServerRunnable);
                return true;
            }
            catch (IOException e){
                continue;
            }
        }
        return false;
    }

    int getPort(){
        return mPort;
    }

    interface OnEosReceivedListener {
        void onEosReceived();
    }
}
