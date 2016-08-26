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

import com.tomtom.camera.util.Logger;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Implementation of ViewfinderStreamServer. Responsible for talking to camera and receiving images
 * via UDP.
 */
public class CameraViewfinderStreamServer extends ViewfinderStreamServer {

    private static final String TAG = "CamVfStreamServer";

    private static final int DEFAULT_VIEWFINDER_PORT = 4001;
    private static final int MAXIMUM_PACKET_SIZE = 1500;

    private static final ExecutorService SINGLE_THREAD_EXECUTOR = Executors.newSingleThreadExecutor();

    private DatagramSocket mDatagramSocket;
    private Thread mReceiverThread;
    private final ReceiverRunnable mReceiverRunnable = new ReceiverRunnable(mDatagramSocket, mOnStreamDataReceivedListener);


    private static class ReceiverRunnable implements Runnable {

        public boolean isRunning;
        public WeakReference<DatagramSocket> datagramSocketRef;
        public WeakReference<OnStreamDataReceivedListener> onStreamDataReceivedListenerRef;

        ReceiverRunnable(DatagramSocket datagramSocket, OnStreamDataReceivedListener onStreamDataReceivedListener) {
            datagramSocketRef = new WeakReference<>(datagramSocket);
            onStreamDataReceivedListenerRef = new WeakReference<>(onStreamDataReceivedListener);
        }

        @Override
        public void run() {
            while (isRunning) {
                byte[] packetBuffer = new byte[MAXIMUM_PACKET_SIZE];
                final DatagramPacket datagramPacket = new DatagramPacket(packetBuffer, packetBuffer.length);
                if(datagramSocketRef.get() != null) {
                    final DatagramSocket datagramSocket = datagramSocketRef.get();
                    try {
                        datagramSocket.receive(datagramPacket);
                        final byte[] data = Arrays.copyOf(datagramPacket.getData(), datagramPacket.getLength());
                        if (onStreamDataReceivedListenerRef.get() != null) {
                            onStreamDataReceivedListenerRef.get().onStreamDataReceived(data);
                        }
                    } catch (IOException e) {
                        Logger.error(TAG, String.format("Error receiving packet %s", e.getLocalizedMessage()));
                    }
                }
            }
        }
    }

    public CameraViewfinderStreamServer() {
        mPort = DEFAULT_VIEWFINDER_PORT;
    }

    @Override
    public boolean start() {
        if (!mReceiverRunnable.isRunning) {
            return startServer(mPort);
        }
        else {
            Logger.warning(TAG, "Server already started");
            return true;
        }
    }

    @Override
    public void stop() {
        if (mReceiverRunnable.isRunning) {
            stopServer();
        }
        else {
            Logger.info(TAG, "Server already stopped");
        }
    }

    @Override
    public boolean isRunning() {
        return mReceiverRunnable.isRunning;
    }

    private boolean startServer(int port) {
        Logger.info(TAG, "Start UDP server");
        if (openSocket(port)) {
            mReceiverRunnable.datagramSocketRef = new WeakReference<DatagramSocket>(mDatagramSocket);
            mReceiverRunnable.onStreamDataReceivedListenerRef = new WeakReference<OnStreamDataReceivedListener>(mOnStreamDataReceivedListener);
            mReceiverRunnable.isRunning = true;
            mReceiverThread = new Thread(mReceiverRunnable);
            mReceiverThread.start();
            return true;
        }
        return false;
    }

    private void stopServer() {
        Logger.info(TAG, "Stop UDP server");
        mReceiverRunnable.isRunning = false;
        mDatagramSocket.close();
        mReceiverThread = null;
    }

    private boolean openSocket(int port) {
        try {
            mDatagramSocket = new DatagramSocket(port);
            Logger.debug(TAG, String.format("Server socket opened on port %d ", port));
            return true;
        }
        catch (SocketException e) {
            Logger.exception(new Throwable(String.format("Couldn't open server socket on port %d, reason %s", port, e.getMessage())));
            return false;
        }
    }
}