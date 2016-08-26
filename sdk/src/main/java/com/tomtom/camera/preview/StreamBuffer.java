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

import android.support.annotation.NonNull;

import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * Generic buffer structure with given set of states - {@link BufferState}.
 */
class StreamBuffer<T> {

    private static final String TAG = "StreamBuffer";

    static final int DEFAULT_BUFFER_CAPACITY = 120;
    static final int DEFAULT_BUFFER_LOW_LIMIT = 30;
    static final int DEFAULT_BUFFER_READY_LIMIT = 60;

    enum BufferState {
        EMPTY, LOW, READY, FULL
    }

    private int mBufferCapacity = DEFAULT_BUFFER_CAPACITY;
    private int mLowBufferLimit = DEFAULT_BUFFER_LOW_LIMIT;
    private int mReadyLimit = DEFAULT_BUFFER_READY_LIMIT;

    private boolean mIsReady;

    private BufferState mBufferState;
    private LinkedList<T> mBufferQueue;
    private StreamBufferCallback mCallback;

    StreamBuffer(@NonNull StreamBufferCallback streamBufferCallback){
        mBufferQueue = new LinkedList<T>();
        mCallback = streamBufferCallback;
    }

    private synchronized void setBufferState(BufferState state) {
        if(mBufferState == BufferState.LOW && state == BufferState.READY){
            mBufferState = state;
            mCallback.onReady();
        }
        else {
            mBufferState = state;
        }

    }

    synchronized BufferState getBufferState(){
        return mBufferState;
    }

    StreamBuffer(StreamBufferCallback streamBufferCallback, int bufferCapacity, int bufferLowLimit, int bufferReadyLimit){
        setBufferState(BufferState.EMPTY);
        mBufferCapacity = bufferCapacity;
        mLowBufferLimit = bufferLowLimit;
        mReadyLimit = bufferReadyLimit;
        mCallback = streamBufferCallback;
        mBufferQueue = new LinkedList<T>();
    }

    BufferState queue(T frame) {

        if (mBufferQueue.size() == mBufferCapacity) {
            setBufferState(BufferState.FULL);
        }
        else {
            boolean accepted = mBufferQueue.offer(frame);

            if (!accepted) {
                setBufferState(BufferState.FULL);
            }
            else {
                if(mBufferQueue.size() <= mLowBufferLimit) {
                    mIsReady = false;
                    setBufferState(BufferState.LOW);
                }

                else if(mBufferQueue.size() <= mReadyLimit) {
                    if (mIsReady) {
                        setBufferState(BufferState.READY);
                    } else {
                        mIsReady = false;
                        setBufferState(BufferState.LOW);
                    }
                }

                else if(mBufferQueue.size() < mBufferCapacity) {
                    mIsReady = true;
                    setBufferState(BufferState.READY);
                }

                else {
                    return BufferState.FULL;
                }
            }
        }

        return mBufferState;
    }

    T dequeue() {
        try {
            T t = mBufferQueue.removeFirst();
            if(mBufferQueue.size() == 0){
                setBufferState(BufferState.EMPTY);
                mCallback.onEmpty();
            }

            else if(mBufferQueue.size() <= mLowBufferLimit){
                setBufferState(BufferState.LOW);
                mCallback.onLow();
            }

            return t;
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    void clear() {
        mIsReady = false;
        mBufferQueue.clear();
        setBufferState(BufferState.EMPTY);
        mCallback.onEmpty();
    }

    int size(){
        return mBufferQueue.size();
    }

    int remaining(){
        return mBufferCapacity - mBufferQueue.size();
    }

    int getReadyToPlayLimit(){
        return mReadyLimit;
    }

    int getLowBufferLimit(){
        return mLowBufferLimit;
    }

    int getCapacity(){
        return mBufferCapacity;
    }

    T peek(){
        return mBufferQueue.peek();
    }
}
