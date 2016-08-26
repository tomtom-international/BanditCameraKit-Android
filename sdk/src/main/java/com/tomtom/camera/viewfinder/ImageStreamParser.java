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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Used for parsing images from camera's viewfinder stream.
 */

class ImageStreamParser {

    private static final String TAG = "StreamParser";

    public enum MessageFrame {

        MESSAGE_FRAME_START(0),
        MESSAGE_FRAME_DATA(1);

        private int mIntValue;

        MessageFrame(int intValue) {
            mIntValue = intValue;
        }

        public int getValue() {
            return mIntValue;
        }
    }

    private static final short PACKET_SYNC = 0x55AA;
    private static final int FRAME_HEADER_LENGTH = 7;
    private static final int FPS_MEASUREMENT_TIME = 1000;

    private OnImageParsedListener mOnImageParsedListener;
    private ByteBuffer mImageData;
    private int mImageDataLength;
    private int mPacketCount;
    private boolean mIsWaitingNewFrame;
    private int mFramesCount;
    private long mStartTime;
    private float mCurrentPts;

    public ImageStreamParser() {
        mIsWaitingNewFrame = true;
    }

    public void parseStream(byte[] data) {
        parseData(data);
    }

    private void parseData(byte[] data) {

        int packetLength = data.length;

        if (packetLength < FRAME_HEADER_LENGTH) {
            Logger.error(TAG, String.format("Got packet data of length %d but expected at least %d length", packetLength, FRAME_HEADER_LENGTH));
            return;
        }

        ByteBuffer bigEndianBuffer = ByteBuffer.wrap(data);
        bigEndianBuffer.order(ByteOrder.BIG_ENDIAN);
        bigEndianBuffer.rewind();

        short sync = bigEndianBuffer.getShort();
        int message = bigEndianBuffer.get();
        int packetNumber = bigEndianBuffer.getShort() & 0xffff;

        short payloadLength = bigEndianBuffer.getShort();

        if (packetLength != payloadLength + FRAME_HEADER_LENGTH) {
            Logger.error(TAG, String.format("Got packet data of length %d but expected %d length", packetLength, payloadLength + FRAME_HEADER_LENGTH));
            return;
        }

        if (sync != PACKET_SYNC) {
            Logger.error(TAG, "Sync data not same");
            return;
        }

        if (message != MessageFrame.MESSAGE_FRAME_START.getValue()
                && message != MessageFrame.MESSAGE_FRAME_DATA.getValue()) {
            Logger.error(TAG, String.format("Got unexpected message %d", message));
            return;
        }

        if (message == MessageFrame.MESSAGE_FRAME_START.getValue()) {
            mImageDataLength = bigEndianBuffer.getInt();
            float pts = bigEndianBuffer.getFloat();
            if (mCurrentPts != pts) {
                mCurrentPts = pts;
            }

            mPacketCount = packetNumber + 1;
            mIsWaitingNewFrame = false;

            if (mFramesCount == 0) {
                mStartTime = System.currentTimeMillis();
            }

            clearData();
        }
        else {
            if (mIsWaitingNewFrame) {
                return;
            }

            if (mPacketCount != packetNumber) {
                Logger.debug(TAG, String.format("Expected packet number %d but got %d", mPacketCount, packetNumber));
                sendImageData(mCurrentPts, null);
                mIsWaitingNewFrame = true;
            }
            else {
                byte[] packetImageData = new byte[payloadLength];
                bigEndianBuffer.get(packetImageData);
                mImageData.put(packetImageData);
                if (mImageData.remaining() == 0) {
                    mIsWaitingNewFrame = true;
                    mFramesCount++;

                    if ((System.currentTimeMillis() - mStartTime) >= FPS_MEASUREMENT_TIME) {
                        mFramesCount = 0;
                    }
                    sendImageData(mCurrentPts, mImageData.array());
                }
                mPacketCount++;
            }
        }
    }

    private void clearData () {
        mImageData = ByteBuffer.allocate(mImageDataLength);
        mImageData.rewind();
    }

    private void sendImageData(float timestamp, byte[] bytes) {
        if (mOnImageParsedListener != null) {
            mOnImageParsedListener.onImageParsed(timestamp, bytes);
        }
    }

    public void setOnImageParsedListener(OnImageParsedListener listener){
        mOnImageParsedListener = listener;
    }

    public interface OnImageParsedListener {
        void onImageParsed(float timeSecs, @Nullable byte[] image);
    }
}