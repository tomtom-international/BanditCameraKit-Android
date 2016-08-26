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

/**
 * Preview buffer implementation. Provides separate buffers for video and audio frames and callbacks
 * for buffer states.
 */
class PreviewBuffer {

    private static final String TAG = "PreviewBuffer";

    private StreamBufferCallback mExternalStreamBufferCallback;
    private StreamBuffer<Frame> mVideoBuffer;
    private StreamBuffer<Frame> mAudioBuffer;
    private boolean mHasReachedEos;
    private boolean mIsVideoOnly;

    private StreamBufferCallback mLocalStreamBufferCallback = new StreamBufferCallback() {
        @Override
        public void onEmpty() {
            Logger.info(TAG, "OnEmpty");
            if(mVideoBuffer.getBufferState() == StreamBuffer.BufferState.EMPTY) {
                if (mIsVideoOnly) {
                    mExternalStreamBufferCallback.onEmpty();
                } else {
                    if (mAudioBuffer.getBufferState() == StreamBuffer.BufferState.EMPTY) {
                        mExternalStreamBufferCallback.onEmpty();
                    } else {
                        Logger.debug(TAG, "onEmpty() Video buffer state: " + mVideoBuffer.getBufferState().name() + " Audio buffer state: " + mAudioBuffer.getBufferState().name());
                    }
                }
            }
            else {
                if (mVideoBuffer.getBufferState() != null) {
                    Logger.debug(TAG, "onEmpty() Video buffer state: " + mVideoBuffer.getBufferState().name() + " Audio buffer state: " + mAudioBuffer.getBufferState().name());
                }
            }
        }

        @Override
        public void onLow() {
            Logger.info(TAG, "OnLow");
            if(mVideoBuffer.getBufferState() == StreamBuffer.BufferState.LOW) {
                if (mIsVideoOnly) {
                    mExternalStreamBufferCallback.onLow();
                } else {
                    if (mAudioBuffer.getBufferState() == StreamBuffer.BufferState.LOW) {
                        mExternalStreamBufferCallback.onLow();
                    } else {
                        Logger.debug(TAG, "onLow() Video buffer state: " + mVideoBuffer.getBufferState().name() + " Audio buffer state: " + mAudioBuffer.getBufferState().name());
                    }
                }
            }
            else {
                Logger.debug(TAG, "onLow() Video buffer state: " + mVideoBuffer.getBufferState().name() + " Audio buffer state: " + mAudioBuffer.getBufferState().name());
            }
        }

        @Override
        public void onReady() {
            Logger.info(TAG, "OnReady");
            if(mIsVideoOnly){
                Logger.info(TAG, "REALLY OnReady");
                mExternalStreamBufferCallback.onReady();
            }
            else {
                if(mVideoBuffer.getBufferState() == StreamBuffer.BufferState.READY && mAudioBuffer.getBufferState().ordinal() >= StreamBuffer.BufferState.READY.ordinal()){
                    mExternalStreamBufferCallback.onReady();
                }
            }
        }
    };

    PreviewBuffer() {
        this(null);
    }

    PreviewBuffer(StreamBufferCallback streamBufferCallback){
        mExternalStreamBufferCallback = streamBufferCallback;
        mVideoBuffer = new StreamBuffer<Frame>(mLocalStreamBufferCallback);
        mAudioBuffer = new StreamBuffer<Frame>(mLocalStreamBufferCallback);
        mHasReachedEos = false;
        mIsVideoOnly = true;
    }

    void setStreamBufferCallback(StreamBufferCallback streamBufferCallback) {
        mExternalStreamBufferCallback = streamBufferCallback;
    }

    Frame dequeue(Frame.Type type){
        if(type == Frame.Type.VIDEO){
            return mVideoBuffer.dequeue();
        }
        else if(type == Frame.Type.AUDIO) {
            return mAudioBuffer.dequeue();
        }
        else{
            return null;
        }
    }

    StreamBuffer.BufferState queue(Frame frame) {
        mHasReachedEos = false;
        switch(frame.getType()){
            case AUDIO:
                if(mIsVideoOnly){
                    mIsVideoOnly = false;
                }
                mAudioBuffer.queue(frame);
                break;
            case VIDEO:
                mVideoBuffer.queue(frame);
                break;
            case EOS:
                mHasReachedEos = true;
        }
        return getBufferState();
    }

    StreamBuffer.BufferState getBufferState(){
        if(mIsVideoOnly){
            if(mHasReachedEos && mVideoBuffer.getBufferState() == StreamBuffer.BufferState.LOW) {
                return StreamBuffer.BufferState.READY;
            }
            else{
                return mVideoBuffer.getBufferState();
            }
        }
        else{
            if(mVideoBuffer.getBufferState() == StreamBuffer.BufferState.LOW || mAudioBuffer.getBufferState() == StreamBuffer.BufferState.LOW){
                if(mHasReachedEos){
                    return StreamBuffer.BufferState.READY;
                }
                else {
                    return StreamBuffer.BufferState.LOW;
                }
            }

            else if(mVideoBuffer.getBufferState() == StreamBuffer.BufferState.FULL || mAudioBuffer.getBufferState() == StreamBuffer.BufferState.FULL) {
                return StreamBuffer.BufferState.FULL;
            }


            else if(mVideoBuffer.getBufferState() == StreamBuffer.BufferState.EMPTY && mAudioBuffer.getBufferState() == StreamBuffer.BufferState.EMPTY){
                return StreamBuffer.BufferState.EMPTY;
            }

            else if(mVideoBuffer.getBufferState() ==StreamBuffer.BufferState.READY && mAudioBuffer.getBufferState() == StreamBuffer.BufferState.READY){
                return StreamBuffer.BufferState.READY;
            }

            Logger.exception(new Exception("None of the given conditions are met. " + " VideoState: " + mVideoBuffer.getBufferState().name() + " Audio: " + mAudioBuffer.getBufferState().name() + " Reached EOS: " + mHasReachedEos));
            return StreamBuffer.BufferState.READY;
        }
    }

    void clear(){
        mAudioBuffer.clear();
        mVideoBuffer.clear();
        mHasReachedEos = false;
        mIsVideoOnly = true;
    }

    int getNextVideoPts(){
        if (mVideoBuffer.peek() != null) {
            return mVideoBuffer.peek().getPts();
        }
        return -1;
    }

    /**
     * Provides information if all frames were consumed.
     *
     * Condition is that EOS frame was offered to
     * buffer and buffer is empty.
     * @return {@code true} if reached, {@code false} if not
     */
    //FIXME This logic should be moved to player.
    boolean isEosReached() {
        if (mIsVideoOnly) {
            return mHasReachedEos && (mVideoBuffer.size() == 0);
        } else {
            return mHasReachedEos && (mVideoBuffer.size() == 0) && (mAudioBuffer.size() == 0);
        }
    }

    boolean hasAudioFrames() {
        return mAudioBuffer.size() > 0;
    }

    boolean isVideoOnly(){
        return mIsVideoOnly;
    }

    void resetVideoOnlyState() {
        mIsVideoOnly = true;
    }

    Frame peek(Frame.Type frameType) {
        switch(frameType){
            case AUDIO:
                if(mAudioBuffer != null) {
                    return mAudioBuffer.peek();
                }
                return null;

            case VIDEO:
                if(mVideoBuffer != null) {
                    return mVideoBuffer.peek();
                }
                return null;
        }

        return null;
    }
}
