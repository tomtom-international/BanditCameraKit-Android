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

package com.tomtom.camera.notification;

import com.tomtom.camera.api.notification.BackchannelNotification;
import com.tomtom.camera.util.Logger;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class is responsible for listening backchannel notifications, sent from camera. It creates
 * socket connection to camera and listens in separate thread, and notifies subscriber via callback
 */
public class BackchannelNotificationListener {

    private static String TAG = "BackchannelNotificationListener";

    private static final Executor sBackchannelThreadExecutor = Executors.newSingleThreadExecutor();
    private static final InetAddress IP_ADDRESS;
    private AtomicBoolean mIsRunning = new AtomicBoolean();

    private String mHost;
    private int mPort;

    private Socket mSocket = null;
    private BufferedReader mBufferedReader = null;
    private BackchannelNotificationsParser mParser;
    private BackchannelNotificationCallback mNotificationCallback;

    private StringBuilder mNotificationMessage;

    static {
        try {
            IP_ADDRESS = InetAddress.getByName("192.168.1.101");
        } catch (UnknownHostException e) {
            throw new RuntimeException("DEFAULT IP ADDRESS IS FAULTY!");
        }
    }

    private Runnable mListenerRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                open(mHost, mPort);
                Logger.debug(TAG, "Connecting notification socket to: " + mHost + " on port: " + mPort);
                while (mIsRunning.get()) {
                    listen();
                }
            } catch (IOException e) {
                stop();
                Logger.error(TAG, e.getMessage());
                Logger.exception(e);
            } finally {
                close();
            }
        }
    };

    public BackchannelNotificationListener() {}


    /**
     * Starts background thread for receiving backchannel notifications, opens socket to camera
     * on port for default IP address
     * @param port int value of port on which the socket connection will be opened
     */
    public void start(int port){
        start(IP_ADDRESS.getHostAddress(), port);
    }
    /**
     * Starts background thread for receiving backchannel notifications, opens socket to camera on port
     * @param host camera IP address
     * @param port int value of port on which the socket connection will be opened
     */
    public void start(String host, int port){
        if (mIsRunning.compareAndSet(false, true)) {
            mHost = host;
            mPort = port;
            sBackchannelThreadExecutor.execute(mListenerRunnable);
        } else {
            Logger.debug(TAG, "Background thread for notifications is not null");
            Logger.exception(new Exception("Trying to start another backchannel listener while another one is already running..."));
        }
    }

    /**
     * Stops background thread for receiving backchannel notifications
     */
    public void stop(){
        if(mIsRunning.compareAndSet(true, false)) {

        }
    }

    /**
     * Sets backchannel notification parser, which will be used to parse JSON from camera
     * when notification is received
     * @param backchannelNotificationParser {@link BackchannelNotificationsParser}
     */
    public void setBackchannelNotificationParser(BackchannelNotificationsParser backchannelNotificationParser) {
        mParser = backchannelNotificationParser;
    }

    /**
     * Sets backchannel notification callback, which will be executed when notification is received
     * @param callback {@link BackchannelNotificationCallback}
     */
    public void setBackchannelNotificationCallback(BackchannelNotificationCallback callback) {
        mNotificationCallback = callback;
    }

    /**
     * Returns true if back channel listener thread is already running, false otherwise
     * @return boolean
     */
    public boolean isRunning(){
        return mIsRunning.get();
    }

    private void open(String host, int port) throws IOException{
        mSocket = new Socket(host, port);
        setReader(new BufferedReader(new InputStreamReader(mSocket.getInputStream())));
        Logger.debug(TAG, "Backchannel notification socket successfully connected.");
    }

    private void setReader(BufferedReader reader) throws IOException {
        mBufferedReader = reader;
    }

    private void listen() throws IOException {
        if (mBufferedReader == null){
            Logger.debug(TAG, "Buffered reader is null");
            return;
        }

        if (mNotificationMessage == null) {
            mNotificationMessage = new StringBuilder();
        }


        String messageString = mBufferedReader.readLine();
        if (messageString != null) {
            Logger.info(TAG, messageString);
            mNotificationMessage.append(messageString);
            try {
                if(mParser != null) {
                    BackchannelNotification notification = mParser.parse(mNotificationMessage.toString());
                    if (notification != null) {
                        Logger.info(TAG, mNotificationMessage.toString());
                        if (mNotificationCallback != null) {
                            mNotificationCallback.onNotificationReceived(notification);
                        }
                    }
                    mNotificationMessage = null;
                }
                else {
                    Logger.exception(new Exception("BackchannelParser is null."));
                }
            } catch (JSONException e) {
                Logger.info(TAG, "Read line from socket but it is not valid JSON yet: " + mNotificationMessage.toString() + " Waiting for next line...");
            }
        }
    }

    private void close(){
        try {
            if (mBufferedReader != null) {
                mBufferedReader.close();
            } else {
                Logger.error(TAG, "reader is null");
            }
            mNotificationMessage = null;
       } catch (IOException e) {
            Logger.error(TAG, e.getLocalizedMessage());
       }
    }
}
