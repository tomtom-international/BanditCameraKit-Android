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

import java.util.HashMap;

/**
 * Video frame abstraction.
 */
public class Frame {

    public static final int HEADER_LENGTH = 12;
    public static final int AAC_ADTS_HEADER = 7;

    public enum Type {
        VIDEO(0),
        AUDIO(1),
        SOS(127), //Start of stream
        EOS(128); //End of stream

        int mVal;

        Type(int val){
            mVal = val;
        }

        public int value(){
            return mVal;
        }

        private static final HashMap<Integer, Type> sLookUpMap = new HashMap<Integer, Type>();

        static {
            sLookUpMap.put(VIDEO.value(), VIDEO);
            sLookUpMap.put(AUDIO.value(), AUDIO);
            sLookUpMap.put(SOS.value(), SOS);
            sLookUpMap.put(EOS.value(), EOS);
        }

        public static Type fromValue(int val){
           return sLookUpMap.get(val);
        }
    }

    private int mVersion;
    private Type mType;
    private boolean mIsFirstFrame;
    private int mPts;
    private int mStatus;
    private byte[] mFrameBytes;

    public Frame(int version, Type type, int pts, int status, byte[] frameBytes, boolean isFirstFrame) {
        this(version, type, pts, status, frameBytes);
        mIsFirstFrame = isFirstFrame;
    }

    public Frame(int version, Type type, int pts, int status, byte[] frameBytes){
        mVersion = version;
        mType = type;
        mPts = pts;
        mStatus = status;
        mFrameBytes = frameBytes;
    }

    public int getVersion() {
        return mVersion;
    }

    public Type getType(){
        return mType;
    }

    public int getPts(){
        return mPts;
    }

    public int getStatus() {
        return mStatus;
    }

    public byte[] getBytes(){
        return mFrameBytes;
    }

    public boolean isFirstFrame() {
        return mIsFirstFrame;
    }

    public void setIsFirstFrame(boolean isFirstFrame) {
        mIsFirstFrame = isFirstFrame;
    }
}
