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

import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Pair;

import com.tomtom.camera.api.model.Playable;
import com.tomtom.camera.util.Logger;
import com.tomtom.camera.video.VideoSurface;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

/**
 * Preview abstraction.
 */
abstract class AbstractPreviewVideo<T extends VideoSurface> {

    private static final String TAG = "PreviewVideo";

    private static final float MILLISECONDS = 1000;

    private static boolean sPreviewActive;
    private static PreviewCommand sNextStartPreviewCommand = null;
    private static PreviewCommand sCurrentPreviewCommandToExecute = null;

    T mVideoSurface;

    protected int mCurrentPlayingVideoIndex;
    private int mCurrentBufferingVideoIndex;

    private Playable mCurrentPlayableFile;
    private Playable[] mPlayableFiles;
    private ArrayList<PreviewPlayableFile> mPreviewCameraFiles;

    private int mTotalDurationMillis;
    private CameraPreviewStreamServer mPreviewStreamServer;
    @Nullable
    private OnPreviewVideoListener mOnPreviewVideoListener;

    private PreviewPlayer mPreviewPlayer;
    private final PreviewBuffer mPreviewBuffer = new PreviewBuffer();

    private boolean mIsInitialized;
    private boolean mIsRestarted;

    private PreviewApiClient mPreviewApiClient;

    private float mCurrentSeekSecs;
    private boolean mShouldPlaySound = true;

    private final CameraPreviewStreamServer.OnEosReceivedListener mOnEosReceivedListener = new CameraPreviewStreamServer.OnEosReceivedListener() {
        @Override
        public void onEosReceived() {
            mPreviewPlayer.play();
            if (mPreviewCameraFiles != null && mCurrentBufferingVideoIndex < mPreviewCameraFiles.size() - 1) {
                mCurrentSeekSecs = mCurrentSeekSecs + mCurrentPlayableFile.getDurationSecs();
                seekPreviewForFile(mPreviewCameraFiles.get(++mCurrentBufferingVideoIndex).getPlayableFile(), 0);
            }
        }
    };

    private final StreamBufferCallback mPreviewStreamBufferCallback = new StreamBufferCallback() {
        @Override
        public void onEmpty() {
            Logger.info(TAG, "Buffering is empty");
            mPreviewPlayer.stop();
        }

        @Override
        public void onLow() {
            Logger.info(TAG, "Buffering is Low");
        }

        @Override
        public void onReady() {
            Logger.info(TAG, "Buffering is ready");
            mPreviewPlayer.play();
        }
    };

    private final PreviewPlayer.PreviewPlayerCallback mPreviewPlayerCallback = new PreviewPlayer.PreviewPlayerCallback() {
        @Override
        public void drawFrame(byte[] data, int pts, boolean isFirstFrame) {
            if (isFirstFrame) {
                mCurrentPlayingVideoIndex++;
                if (mCurrentPlayingVideoIndex == 0 && mOnPreviewVideoListener != null) {
                    mOnPreviewVideoListener.onPreviewStarted((int) (mCurrentSeekSecs * MILLISECONDS));
                }
            } else if (mIsRestarted) {
                mIsRestarted = false;
                return;
            }

            if (mPreviewCameraFiles == null || mCurrentPlayingVideoIndex == -1) {
                return;
            }

            PreviewPlayableFile currentlyPlaying = mPreviewCameraFiles.get(mCurrentPlayingVideoIndex);
            if (mShouldPlaySound) {
                mPreviewPlayer.setVolumeEnabled(!currentlyPlaying.getPlayableFile().isMuted());
            } else {
                mPreviewPlayer.setVolumeEnabled(false);
            }

            if (mOnPreviewVideoListener != null) {
                mOnPreviewVideoListener.onPreviewTimeProgress(pts - (int) ((currentlyPlaying.getPlayableFile().getStartOffsetSecs() - currentlyPlaying.getOffsetSecsInTotalDuration()) * MILLISECONDS));
            }

            processFrameData(data, pts, isFirstFrame);
        }

        @Override
        public void onPlaybackFinished() {
            if (mOnPreviewVideoListener != null) {
                mOnPreviewVideoListener.onEndReceived();
            }
        }
    };

    AbstractPreviewVideo(PreviewApiClient previewApiClient, T surfaceView, OnPreviewVideoListener onPreviewVideoListener) {
        mPreviewApiClient = previewApiClient;
        mVideoSurface = surfaceView;
        mOnPreviewVideoListener = onPreviewVideoListener;
        mPreviewBuffer.setStreamBufferCallback(mPreviewStreamBufferCallback);
        mPreviewStreamServer = createPreviewStreamServer(mPreviewBuffer);
        mPreviewStreamServer.setOnEosReceivedListener(mOnEosReceivedListener);
        mPreviewPlayer = new PreviewPlayer(mPreviewBuffer);
        mPreviewPlayer.setPreviewPlayerCallback(mPreviewPlayerCallback);
        mIsInitialized = false;
    }

    /**
     * Creates new instance of {@link CameraPreviewStreamServer}
     * @param previewBuffer {@link PreviewBuffer} which server should use
     * @return instance of {@link CameraPreviewStreamServer}
     */
    protected CameraPreviewStreamServer createPreviewStreamServer(PreviewBuffer previewBuffer) {
        return new CameraPreviewStreamServer(previewBuffer);
    }

    /**
     * Sets {@link OnPreviewVideoListener} to PreviewVideo instance.
     * @param onPreviewVideoListener {@link OnPreviewVideoListener} implementation
     */
    public void setOnPreviewVideoListener(OnPreviewVideoListener onPreviewVideoListener) {
        mOnPreviewVideoListener = onPreviewVideoListener;
    }

    /**
     * Sets video surface for displaying video frames.
     * @param videoSurface {@link VideoSurface} implementation
     */
    public void setVideoSurface(T videoSurface) {
        mVideoSurface = videoSurface;
    }

    /**
     * Prepares preview for given array of playable files
     * @param playableFiles array of playable files
     */
    public void preparePreview(Playable... playableFiles) {
        try {
            mPreviewCameraFiles = new ArrayList<>(playableFiles.length);
            float offsetInTimeline = 0;
            for (int i = 0; i < playableFiles.length; i++) {
                PreviewPlayableFile previewCameraFile = new PreviewPlayableFile(playableFiles[i].clone(), offsetInTimeline);
                mPreviewCameraFiles.add(previewCameraFile);
                offsetInTimeline += playableFiles[i].getDurationSecs();
            }
            mTotalDurationMillis = (int) (offsetInTimeline * MILLISECONDS);
            if(mOnPreviewVideoListener != null) {
                mOnPreviewVideoListener.onTotalLengthSet(mTotalDurationMillis);
            }

            mIsInitialized = true;
            mPlayableFiles = playableFiles;
        } catch (CloneNotSupportedException e) {
            Logger.exception(e);
        }
    }

    /**
     * Starts the preview
     */
    public void startPreview() {
        if (!mIsInitialized) {
            throw new IllegalStateException("Preview not initialized.");
        }
        seekPreview(0);
    }

    /**
     * Stops the preview
     */
    public void stopPreview() {
        if (!isPreviewActive() || mCurrentPlayableFile == null) {
            if (mVideoSurface != null) {
                mVideoSurface.stop();
            }
            return;
        }

        Logger.info(TAG, "Stopping preview video");

        mPreviewStreamServer.stop();
        mVideoSurface.stopDrawing();
        sendStopPreview();
    }

    /**
     * Releases all allocations made with {@link #preparePreview(Playable[])} call.
     */
    public void releasePreview() {
        Logger.debug(TAG, "Releasing preview...");
        mPreviewCameraFiles = null;
        mPlayableFiles = null;
        mCurrentPlayableFile = null;
        mIsInitialized = false;
        sCurrentPreviewCommandToExecute = null;
    }

    private void seekPreviewForFile(Playable cameraFile, float seekToTimeSecs) {
        mCurrentPlayableFile = cameraFile;
        if (isPreviewActive()) {
            scheduleNextCommand(getStartOnSeekCommand(seekToTimeSecs));
            stopPreviewVideoFile();
        } else {
            sendSeekVideoFileCommand(seekToTimeSecs);
        }
    }

    /**
     * Seeks to given offset in current file
     * @param seekToTimeMilliseconds seek position in millis
     */
    public synchronized void seekPreview(int seekToTimeMilliseconds) {
        if (!mIsInitialized) {
            throw new IllegalStateException("Preview not initialized.");
        }
        mCurrentSeekSecs = (float) (seekToTimeMilliseconds / MILLISECONDS);
        mIsRestarted = true;
        mCurrentBufferingVideoIndex = getPreviewPlayableFileIndex(seekToTimeMilliseconds);
        mCurrentPlayingVideoIndex = mCurrentBufferingVideoIndex - 1;

        PreviewPlayableFile previewPlayableFile = mPreviewCameraFiles.get(mCurrentBufferingVideoIndex);
        if (seekToTimeMilliseconds == mTotalDurationMillis) {
            if (mOnPreviewVideoListener != null) {
                mOnPreviewVideoListener.onEndReceived();
            }
        } else {
            float seekToTimeSecs = mCurrentSeekSecs - previewPlayableFile.getOffsetSecsInTotalDuration();
            seekPreviewForFile(previewPlayableFile.getPlayableFile(), seekToTimeSecs);
        }
    }

    /**
     * Seeks to given offset in current file and plays only for given duration
     * @param seekToTimeSeconds seek position in millis
     * @param duration duration of preview
     */
    public synchronized void seekPreview(float seekToTimeSeconds, float duration) {
        if (!mIsInitialized) {
            throw new IllegalStateException("Preview not initialized.");
        }
        mCurrentSeekSecs = seekToTimeSeconds;
        mIsRestarted = true;
        mCurrentBufferingVideoIndex = getPreviewPlayableFileIndex((int) (seekToTimeSeconds * MILLISECONDS));
        mCurrentPlayingVideoIndex = mCurrentBufferingVideoIndex - 1;

        PreviewPlayableFile previewPlayableFile = mPreviewCameraFiles.get(mCurrentBufferingVideoIndex);

        seekPreviewForFile(mCurrentSeekSecs - previewPlayableFile.getOffsetSecsInTotalDuration(), duration, previewPlayableFile.getPlayableFile());
    }

    private void seekPreviewForFile(float seekToTimeInFile, float duration, Playable cameraFile) {
        mCurrentPlayableFile = cameraFile;
        if (isPreviewActive()) {
            Logger.info(TAG, "Scheduling start preview command");
            scheduleNextCommand(getStartPreviewCommand(seekToTimeInFile, duration));
            prepareForPreviewStart();
        } else {
            startPreviewStreamServer();
            sendPreviewCommand(getStartPreviewCommand(seekToTimeInFile, duration));
        }
    }

    /**
     * Pauses the preview
     * @param pausePreview {@code true} if pausing, {@code false} if unpausing/resuming
     */
    public void pausePreview(boolean pausePreview) {
        mPreviewPlayer.pause(pausePreview);
    }

    private void startPreviewStreamServer() {
        mPreviewStreamServer.start();
    }

    /**
     * Enables sound for preview
     * @param playSound {@code true} if enabling, {@code false} if disabling
     */
    public void setVolumeEnabled(boolean playSound) {
        mShouldPlaySound = playSound;
        mPreviewPlayer.setVolumeEnabled(playSound);
    }

    /**
     * Provides information if sound is enabled.
     * @return {@code true} if enabled, {@code false} if disabled
     */
    public boolean isVolumeEnabled() {
        return mShouldPlaySound;
    }

    private synchronized void stopPreviewVideoFile() {
        if (mCurrentBufferingVideoIndex == 0) {
            mPreviewStreamServer.stop();
            mVideoSurface.stopDrawing();
        }
        sendStopPreview();
    }

    private void sendSeekVideoFileCommand(float seekToTimeSecs) {
        startPreviewStreamServer();
        Logger.info(TAG, "Sending seek preview command");
        sendPreviewCommand(getStartOnSeekCommand(seekToTimeSecs));
    }

    private PreviewCommand getStartOnSeekCommand(float seekToTimeSecs) {
        return getStartPreviewCommand(seekToTimeSecs + mCurrentPlayableFile.getStartOffsetSecs(), mCurrentPlayableFile.getDurationSecs() - seekToTimeSecs);
    }

    private PreviewCommand getStartPreviewCommand(float previewStartPositionSecs, float lengthSecs) {
        return PreviewCommand.createStart(mCurrentPlayableFile.getPlayableId(), mPreviewStreamServer.getPort(), previewStartPositionSecs, lengthSecs);
    }

    private void sendStopPreview() {
        if (mCurrentPlayableFile != null) {
            Logger.info(TAG, "Sending stop preview command.");
            sendPreviewCommand(PreviewCommand.createStop(mCurrentPlayableFile.getPlayableId(), mPreviewStreamServer.getPort()));
        } else {
            Logger.exception(new Exception("Trying to send StopPreviewCommand for null current playable file"));
        }
    }

    private synchronized void sendPreviewCommand(PreviewCommand previewCommand) {
        if (sCurrentPreviewCommandToExecute != null) {
            if (previewCommand.command == PreviewCommand.START) {
                sNextStartPreviewCommand = previewCommand;
                Logger.info(TAG, "Adding next preview start to schedule");
            } else {
                Logger.info(TAG, "Already executing a command " + sCurrentPreviewCommandToExecute.command);
            }
            return;
        }
        if (previewCommand == null) {
            Logger.error(TAG, "Null command sent");
            return;
        }
        sCurrentPreviewCommandToExecute = previewCommand;
        mPreviewApiClient.executePreviewCommand(previewCommand);
    }

    /**
     * Should be called as soon as preview has been started on the camera
     * @param isSuccessful camera preview start request success
     */
    public void onCameraPreviewStarted(boolean isSuccessful) {
        sCurrentPreviewCommandToExecute = null;
        if (isSuccessful) {
            sPreviewActive = true;
            Logger.info(TAG, "PreviewVideo started");
            sendPendingStartCommand();
        }
    }

    private void prepareForPreviewStart() {
        stopPreviewVideoFile();
        startPreviewStreamServer();
    }

    /**
     * Should be called as soon as preview has been stopped on the camera
     * @param isSuccessful camera preview stop request success
     */
    public void onCameraPreviewStopped(boolean isSuccessful) {
        sCurrentPreviewCommandToExecute = null;
        if (isSuccessful) {
            sPreviewActive = false;
            Logger.info(TAG, "PreviewVideo stopped");
            sendPendingStartCommand();
        }
    }

    private void sendPendingStartCommand() {
        if(sNextStartPreviewCommand != null) {
            startPreviewStreamServer();
            Logger.info(TAG, "Sending scheduled start");
            sNextStartPreviewCommand.previewVideoPort = mPreviewStreamServer.getPort();
            sendPreviewCommand(sNextStartPreviewCommand);
            sNextStartPreviewCommand = null;
        }
    }


    private void scheduleNextCommand(PreviewCommand command) {
        Logger.info(TAG, "Scheduling " + command.command);
        sNextStartPreviewCommand = command;
    }

    /**
     * Process video frame data
     * @param data image bytes
     * @param pts pts of frame
     * @param isFirstFrame is first frame
     */
    protected abstract void processFrameData(byte[] data, int pts, boolean isFirstFrame);

    /**
     * Provides currently playing {@link Playable}
     * @return current playing {@link Playable}
     */
    public Playable getCurrentPlayableFile() {
        if (mCurrentPlayingVideoIndex > -1 && mPreviewCameraFiles != null) {
            return mPreviewCameraFiles.get(mCurrentPlayingVideoIndex).getPlayableFile();
        }
        return mCurrentPlayableFile;
    }

    /**
     * Provides list of playable files
     * @return list of playable files
     */
    public Playable[] getPlayableFiles() {
        return mPlayableFiles;
    }

    private PreviewPlayableFile getPreviewPlayableFileOnPosition(int timeMillisInTotalDuration) {
        return mPreviewCameraFiles.get(getPreviewPlayableFileIndex(timeMillisInTotalDuration));
    }

    /**
     * Provides index about {@link Playable} file at the given time position
     * @param timeMillisInTotalDuration millis in total duration
     * @return index of file
     */
    public int getPreviewPlayableFileIndex(int timeMillisInTotalDuration) {
        if(mPreviewCameraFiles != null) {
            if (timeMillisInTotalDuration == mTotalDurationMillis) {
                return mPreviewCameraFiles.size() - 1;
            }

            int tmpLength = 0;

            for (int i = 0; i < mPreviewCameraFiles.size(); i++) {
                tmpLength += mPreviewCameraFiles.get(i).getPlayableFile().getDurationSecs() * MILLISECONDS;
                if (timeMillisInTotalDuration < tmpLength) {
                    return i;
                }
            }
        }
        else {
            Logger.exception(new Exception("Preview camera file list is null"));
        }
        return -1;
    }

    /**
     * Provides information about which file and its offset is on the total offset for all video files
     * set in preview.
     * @param timeMillisInTotalDuration current millis in total duration
     * @return {@link Pair} of given video and offet.
     */
    public Pair<String, Float> getPlayableFileIdAndOffset(int timeMillisInTotalDuration) {
        float secondsInTotalDuration = (float) timeMillisInTotalDuration / MILLISECONDS;
        PreviewPlayableFile previewPlayableFile = getPreviewPlayableFileOnPosition(timeMillisInTotalDuration);
        return new Pair<>(previewPlayableFile.getPlayableFile().getPlayableId(), secondsInTotalDuration - previewPlayableFile.getOffsetSecsInTotalDuration() + previewPlayableFile.getPlayableFile().getStartOffsetSecs());
    }

    /**
     * Provides information about current state of preview
     * @return {@code true} if active, {@code false} if not.
     */
    public boolean isPreviewActive() {
        return sPreviewActive;
    }

    private class PreviewPlayableFile {
        private Playable mCameraFile;
        private float mOffsetSecsInTotalDuration;

        public PreviewPlayableFile(Playable cameraFile, float offsetSecsInTotalDuration) {
            mCameraFile = cameraFile;
            mOffsetSecsInTotalDuration = offsetSecsInTotalDuration;
        }

        /**
         * Provides current playable file
         * @return current {@link Playable}
         */
        public Playable getPlayableFile() {
            return mCameraFile;
        }

        /**
         * Offset in total duration of video file
         * @return offset in seconds
         */
        public float getOffsetSecsInTotalDuration() {
            return mOffsetSecsInTotalDuration;
        }
    }

    /**
     * Wrapper for data needed to start/stop preview on camera
     */
    public static class PreviewCommand {

        @Retention(RetentionPolicy.SOURCE)
        @IntDef({START, STOP})
        public @interface Command{}
        public static final int START = 0;
        public static final int STOP = 1;

        /**
         *
         */
        public @Command int command;
        public String videoId;
        public int previewVideoPort;
        public Float startPositionSecs;
        public Float lengthSecs;

        /**
         * Static instantiator for STOP command
         * @param videoId id for given video to be stopped
         * @param previewVideoPort port of running preview
         * @return {@link PreviewCommand} instance
         */
        static PreviewCommand createStop(String videoId, int previewVideoPort) {
            PreviewCommand stop = new PreviewCommand();
            stop.command = STOP;
            stop.videoId = videoId;
            stop.previewVideoPort = previewVideoPort;
            return stop;
        }

        /**
         * Static instantiator for START command
         * @param videoId id of given video to be started
         * @param previewVideoPort port of running review
         * @param startPositionSecs starting position in seconds
         * @param lengthSecs duration of preview
         * @return {@link PreviewCommand} instance
         */
        static PreviewCommand createStart(String videoId, int previewVideoPort, Float startPositionSecs, Float lengthSecs) {
            PreviewCommand start = new PreviewCommand();
            start.command = START;
            start.videoId = videoId;
            start.previewVideoPort = previewVideoPort;
            start.startPositionSecs = startPositionSecs;
            start.lengthSecs = lengthSecs;
            return start;
        }
    }

    /**
     * Interface for communicating with camera and forwarding explicit commands to start/stop
     * preview. Neccessary due to synchronized nature of preview steps.
     */
    public interface PreviewApiClient {
        /**
         * Sends given start/stop commands to Camera
         * @param previewCommand start/stop command
         */
        void executePreviewCommand(PreviewCommand previewCommand);
    }
}
