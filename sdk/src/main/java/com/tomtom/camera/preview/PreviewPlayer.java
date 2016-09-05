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

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaFormat;

import com.tomtom.camera.util.AudioUtil;
import com.tomtom.camera.util.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Controlling audio and video preview threads.
 */

class PreviewPlayer {

    private static final String TAG = "PreviewPlayer";

    private ExecutorService mPtsExecutorService = Executors.newSingleThreadExecutor();

    private ScheduledExecutorService mScheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> mHandle;
    private VideoThread mVideoRunnable;

    private AtomicBoolean mIsPlaying = new AtomicBoolean();
    private AudioThread mAudioThread;
    private boolean mReadyToPlayAudio;
    private Object mNotifyObject = new Object();

    private PreviewBuffer mPreviewBuffer;
    private PreviewPlayerCallback mPreviewPlayerCallback;

    PreviewPlayer(PreviewBuffer previewBuffer) {
        mPreviewBuffer = previewBuffer;
        mVideoRunnable = new VideoThread();
    }

    void setPreviewPlayerCallback(PreviewPlayerCallback previewPlayerCallback) {
        mPreviewPlayerCallback = previewPlayerCallback;
    }


    void setVolumeEnabled(boolean playSound) {
        if (mAudioThread != null) {
            mAudioThread.setVolumeEnabled(playSound);
        }
    }

    void play() {
        if (mIsPlaying.compareAndSet(false, true)) {
            mHandle = mScheduler.schedule(mVideoRunnable,0 , MILLISECONDS);
            if (mAudioThread == null || !mAudioThread.isAudioRunning()) {
                mAudioThread = new AudioThread();
                mAudioThread.start();
            }
        }
    }

    void stop() {
        if (mIsPlaying.compareAndSet(true, false)) {
            mHandle.cancel(true);
        }
    }

    void pause(boolean pause) {
        if (!pause) {
            play();
        }
        else {
            stop();
        }
    }

    private class AudioThread extends Thread {

        private boolean mIsAudioRunning;
        private boolean mIsMuted;
        private AudioTrack mAudioTrack;

        AudioThread() {
            mIsAudioRunning = true;
        }

        void setIsAudioRunning(boolean isAudioRunning) {
            mIsAudioRunning = isAudioRunning;
        }

        boolean isAudioRunning() {
            return mIsAudioRunning;
        }

        void setVolumeEnabled(boolean playSound) {
            mIsMuted = !playSound;
            if (mAudioTrack != null && mAudioTrack.getState() == AudioTrack.STATE_INITIALIZED) {
                try {
                    if (playSound) {
                        mAudioTrack.setStereoVolume(AudioTrack.getMaxVolume(), AudioTrack.getMaxVolume());
                    } else {
                        mAudioTrack.setStereoVolume(0f, 0f);
                    }
                } catch (IllegalStateException e) {
                    Logger.error(TAG, "IllegalState in setting volume. AudioTrack state was: " + mAudioTrack.getState());
                }
            }
        }

        @Override
        public void run() {
            while (mIsPlaying.get() && mIsAudioRunning) {
                Frame audioFrame = mPreviewBuffer.peek(Frame.Type.AUDIO);
                int adtsSampleRate;
                ByteBuffer csdBytes;
                if (audioFrame != null) {
                    adtsSampleRate = AudioUtil.getSampleRateFromAacFrame(audioFrame.getBytes());
                    csdBytes = AudioUtil.getCsdForFrame(audioFrame.getBytes());
                } else {
                    adtsSampleRate = AudioUtil.SAMPLE_RATE_DEFAULT;
                    csdBytes = ByteBuffer.wrap(new byte[]{0x11, (byte) 0x90});
                }

                MediaCodec codec;

                MediaFormat format = MediaFormat.createAudioFormat(AudioUtil.AAC_MIME_TYPE, adtsSampleRate, 1);
                format.setInteger(MediaFormat.KEY_SAMPLE_RATE, adtsSampleRate);
                format.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1);
                format.setByteBuffer("csd-0", csdBytes);
                String mime = format.getString(MediaFormat.KEY_MIME);

                // the actual decoder
                try {
                    codec = MediaCodec.createDecoderByType(mime);
                } catch (IOException e) {
                    Logger.exception(e);
                    return;
                }

                codec.configure(format, null, null, 0);
                codec.start();
                ByteBuffer[] codecInputBuffers = codec.getInputBuffers();
                ByteBuffer[] codecOutputBuffers = codec.getOutputBuffers();

                // get the sample rate to configure AudioTrack
                int sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE);
                Logger.info(TAG, "Codec output sample rate is " + sampleRate);
                // create our AudioTrack instance
                mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate,
                        AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                        AudioTrack.getMinBufferSize(sampleRate,
                                AudioFormat.CHANNEL_OUT_MONO,
                                AudioFormat.ENCODING_PCM_16BIT), AudioTrack.MODE_STREAM);

                // start playing, we will feed you later
                mAudioTrack.play();
                if (mIsMuted) {
                    mAudioTrack.setStereoVolume(0f, 0f);
                } else {
                    mAudioTrack.setStereoVolume(AudioTrack.getMaxVolume(), AudioTrack.getMaxVolume());
                }

                // start decoding
                MediaCodec.BufferInfo bufInfo = new MediaCodec.BufferInfo();
                boolean sawInputEOS = false;
                boolean sawOutputEOS = false;
                boolean sawFirstFrame = false;

                while (!sawOutputEOS && mIsPlaying.get() && mIsAudioRunning) {

                    while (!mPreviewBuffer.hasAudioFrames()) {
                        if (!(mIsAudioRunning && mIsPlaying.get())) {
                            break;
                        }
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            Logger.exception(e);
                        }
                    }

                    if (!(mIsAudioRunning && mIsPlaying.get())) {
                        break;
                    }

                    synchronized (mNotifyObject) {
                        Frame frame = mPreviewBuffer.peek(Frame.Type.AUDIO);

                        if (frame == null) {
                            break;
                        }

                        if (frame.isFirstFrame()) {
                            if (!sawFirstFrame) {
                                frame = mPreviewBuffer.dequeue(Frame.Type.AUDIO);
                                sawFirstFrame = true;
                                if (!mReadyToPlayAudio) {
                                    try {
                                        mNotifyObject.wait();
                                    } catch (InterruptedException e) {
                                        Logger.exception(e);
                                    }
                                }
                                mReadyToPlayAudio = false;
                            } else {
                                break;
                            }
                        } else {
                            frame = mPreviewBuffer.dequeue(Frame.Type.AUDIO);
                        }


                        if (!sawInputEOS) {
                            sawInputEOS = queueCodecInputBuffer(codec, codecInputBuffers, sawInputEOS, frame);
                        }
                    }

                    int outputBufIndex = codec.dequeueOutputBuffer(bufInfo, 10000);

                    if (outputBufIndex >= 0) {
                        sawOutputEOS = queueAudioTrack(codec, mAudioTrack, codecOutputBuffers[outputBufIndex], bufInfo, sawOutputEOS, outputBufIndex);
                    } else if (outputBufIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                        codecOutputBuffers = codec.getOutputBuffers();
                        Logger.info(TAG, "output buffers have changed.");
                    } else if (outputBufIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                        MediaFormat oformat = codec.getOutputFormat();

                        Logger.info(TAG, "output format has changed to " + oformat);
                    } else {
                        Logger.info(TAG, "dequeueOutputBuffer returned " + outputBufIndex);
                    }

                }

                Logger.debug(TAG, "Stopping audio track");

                // ////////closing
                if (mAudioTrack != null) {
                    mAudioTrack.flush();
                    mAudioTrack.release();
                }
                codec.stop();
            }
            Logger.debug(TAG, "Finishing AudioThread");
            mIsAudioRunning = false;
        }

        private boolean queueAudioTrack(MediaCodec codec, AudioTrack audioTrack, ByteBuffer codecOutputBuffer, MediaCodec.BufferInfo bufInfo, boolean sawOutputEOS, int outputBufIndex) {

            ByteBuffer buf = codecOutputBuffer;
            buf.position(bufInfo.offset);
            buf.limit(bufInfo.offset + bufInfo.size);

            final byte[] chunk = new byte[bufInfo.size];
            buf.get(chunk);
            buf.clear();

            if (chunk.length > 0) {
                // play
                audioTrack.write(chunk, 0, chunk.length);

            }
            codec.releaseOutputBuffer(outputBufIndex, false);
            if ((bufInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                Logger.info(TAG, "Output EOS reached");
                sawOutputEOS = true;
            }
            return sawOutputEOS;
        }

        private boolean queueCodecInputBuffer(MediaCodec codec, ByteBuffer[] codecInputBuffers, boolean sawInputEOS, Frame frame) {
            int inputBufIndex = codec.dequeueInputBuffer(10000);
            if (inputBufIndex >= 0) {
                ByteBuffer dstBuf = codecInputBuffers[inputBufIndex];
                dstBuf.clear();

                int actualRead = 0;

                if (mPreviewBuffer.isEosReached()) {
                    sawInputEOS = true;
                } else {
                    dstBuf.put(frame.getBytes(), Frame.AAC_ADTS_HEADER, frame.getBytes().length - Frame.AAC_ADTS_HEADER);
                    actualRead = frame.getBytes().length - 7;
                }

                codec.queueInputBuffer(inputBufIndex, 0, actualRead, 0, sawInputEOS ? MediaCodec.BUFFER_FLAG_END_OF_STREAM : 0);
            } else {
                Logger.debug(TAG, "inputBufIndex " + inputBufIndex);
            }
            return sawInputEOS;
        }
    }

    private class VideoThread implements Runnable {

        private static final int PTS_DIVIDER_MILLIS = 90;

        private final ExecutorService mPlayerExecutorService = Executors.newSingleThreadExecutor();

        @Override
        public void run() {
            final Frame frame = mPreviewBuffer.dequeue(Frame.Type.VIDEO);
            if (frame == null) {
                return;
            }

            Frame nextFrame = mPreviewBuffer.peek(Frame.Type.VIDEO);

            mPlayerExecutorService.execute(new Runnable() {
                @Override
                public void run() {
                    if (mPreviewPlayerCallback != null) {
                        mPreviewPlayerCallback.drawFrame(frame.getBytes(), frame.getPts() / PTS_DIVIDER_MILLIS, frame.isFirstFrame());
                    }
                    synchronized (mNotifyObject) {
                        if (frame.isFirstFrame() && mPreviewBuffer.hasAudioFrames()) {
                            mReadyToPlayAudio = true;
                            mNotifyObject.notify();
                        }
                    }
                }
            });

            int nextPts = mPreviewBuffer.getNextVideoPts();
            if(nextPts != -1
                    && mPreviewBuffer.getBufferState() != StreamBuffer.BufferState.EMPTY
                    && mIsPlaying.get()) {

                int deltaPts;
                if (nextFrame != null && nextFrame.isFirstFrame()) {
                    deltaPts = 0;
                } else {
                    deltaPts = ((nextPts - frame.getPts()) / PTS_DIVIDER_MILLIS);
                }

                mHandle = mScheduler.schedule(mVideoRunnable, (long) deltaPts, MILLISECONDS);

            }
            else {
                if (mPreviewPlayerCallback != null && mPreviewBuffer.isEosReached()) {
                    mPtsExecutorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            mPreviewPlayerCallback.onPlaybackFinished();
                        }
                    });
                }
            }
        }
    }

    interface PreviewPlayerCallback {
        void drawFrame(byte[] data, int pts, boolean isFirstFrame);
        void onPlaybackFinished();
    }
}
