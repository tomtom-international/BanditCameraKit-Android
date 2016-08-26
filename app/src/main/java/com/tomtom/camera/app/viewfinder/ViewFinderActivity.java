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

package com.tomtom.camera.app.viewfinder;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.tomtom.camera.api.CameraApiCallback;
import com.tomtom.camera.api.model.CameraStatus;
import com.tomtom.camera.api.model.Image;
import com.tomtom.camera.api.model.Settings;
import com.tomtom.camera.api.model.Video;
import com.tomtom.camera.api.notification.BackchannelNotification;
import com.tomtom.camera.api.notification.model.PhotoCapturedNotification;
import com.tomtom.camera.api.notification.model.RecordingStoppedNotification;
import com.tomtom.camera.app.Camera;
import com.tomtom.camera.app.R;
import com.tomtom.camera.app.video.BasicVideoGLSurfaceView;
import com.tomtom.camera.notification.BackchannelNotificationCallback;
import com.tomtom.camera.notification.BackchannelNotificationListener;
import com.tomtom.camera.notification.BackchannelNotificationsParser;
import com.tomtom.camera.util.Logger;
import com.tomtom.camera.viewfinder.Viewfinder;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Activity launched when viewfinder button on start page is pressed. It is used to demonstrate
 * how to record of video or make photo
 */

public class ViewFinderActivity extends AppCompatActivity implements BackchannelNotificationCallback {

    private static final String TAG = "ViewFinderActivity";

    @BindView(R.id.view_finder_surfaceView)
    BasicVideoGLSurfaceView mSurfaceView;

    @BindView(R.id.start_stop_recording)
    Button mStartStopRecording;

    @BindView(R.id.radio_group_recording_type)
    RadioGroup mChooseMode;

    @BindView(R.id.btn_image)
    RadioButton mTakePicture;

    @BindView(R.id.btn_video)
    RadioButton mMakeVideo;

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;

    @BindString(R.string.btn_stop_label)
    String mStopLabel;

    @BindString(R.string.btn_record_label)
    String mRecordLabel;

    @BindString(R.string.snackbar_photo_captured)
    String mPhotoSnackbarText;

    @BindString(R.string.snackbar_video_created)
    String mVideoCreatedSnackbarText;

    @BindString(R.string.snackbar_video_creation_started)
    String mVideoRecordingSnackbarText;

    private boolean mIsRecordingStarted = false;

    private Viewfinder mViewfinder;
    private BackchannelNotificationListener mBackchannelNotificationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_finder);
        ButterKnife.bind(this);
        setVideoModeAsDefault();
        setupSurfaceView();
        setupViewfinder();
        mChooseMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.btn_image) {
                    setImageModeAsDefault();
                } else if (checkedId == R.id.btn_video) {
                    setVideoModeAsDefault();
                }
            }
        });
    }

    private void setVideoModeAsDefault() {
        Camera.getCameraApi().switchToVideoMode(Video.Mode.NORMAL, new CameraApiCallback<Settings>() {
            @Override
            public void success(Settings settings) {
            }

            @Override
            public void error(int statusCode) {
            }

            @Override
            public void failure(Throwable throwable) {
            }
        });
    }

    private void setupViewfinder() {
        if (mViewfinder == null) {
            mViewfinder = new Viewfinder();
        }
    }

    private void setupSurfaceView() {
        mSurfaceView.setKeepScreenOn(true);
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        android.view.ViewGroup.LayoutParams lp = mSurfaceView.getLayoutParams();
        lp.height = (int) (screenWidth * 9f / 16f);
        mSurfaceView.setLayoutParams(lp);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSurfaceView.onResume();
        setupBackChannelNotifications();
        mViewfinder.setOnImageReceivedListener(new Viewfinder.OnImageReceivedListener() {
            @Override
            public void onImageReceived(float timeSecs, @Nullable byte[] image) {
                if (mSurfaceView != null && image != null) {
                    mSurfaceView.queueImage(image);
                }
            }
        });
        getCameraStatus();
    }

    private void getCameraStatus() {
        CameraApiCallback<CameraStatus> cameraStatusCallback = new CameraApiCallback<CameraStatus>() {
            @Override
            public void success(CameraStatus status) {
                if (!mBackchannelNotificationListener.isRunning()) {
                    mBackchannelNotificationListener.start(status.getBackchannelPort());
                }
                if (status.isViewfinderActive()) {
                    if(status.getViewFinderStreamingPort() == mViewfinder.getPort()) {
                        boolean started = mViewfinder.start();
                    }
                    else {
                        // Ports are different so we need to change port on Viewfinder and start server again
                    }
                }
                else {
                    mViewfinder.start();
                    sendStartViewfinder(mViewfinder.getPort());
                }
            }

            @Override
            public void error(int statusCode) {
                //TODO Handle error
            }

            @Override
            public void failure(Throwable throwable) {
                //TODO Handle failure
            }
        };
        Camera.getCameraApi().getCameraStatus(cameraStatusCallback);
    }

    private void sendStartViewfinder(int port) {
        Camera.getCameraApi().setViewfinderStatus(new StartViewfinderStatus(port), new CameraApiCallback<Void>() {
            @Override
            public void success(Void aVoid) {
                Logger.debug(TAG, "Viewfinder start success");
            }

            @Override
            public void error(int statusCode) {
                Logger.debug(TAG, "Viewfinder start error");
                // Check a/o retry, possibly ViewFinder already started
            }

            @Override
            public void failure(Throwable throwable) {
                Logger.debug(TAG, "Viewfinder start failure");
                // Retry, possible connection error
            }
        });
    }

    private void sendStopViewfinder() {
        Camera.getCameraApi().setViewfinderStatus(new StopViewfinderStatus(), new CameraApiCallback<Void>() {
            @Override
            public void success(Void aVoid) {
                Logger.debug(TAG, "Viewfinder stop success");
            }

            @Override
            public void error(int statusCode) {
                Logger.debug(TAG, "Viewfinder stop error");
                // Check a/o retry, possibly ViewFinder already stopped
            }

            @Override
            public void failure(Throwable throwable) {
                Logger.debug(TAG, "Viewfinder stop failure");
                // Retry, possible connection error
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBackchannelNotificationListener.setBackchannelNotificationCallback(null);
        if (mBackchannelNotificationListener.isRunning()) {
            mBackchannelNotificationListener.stop();
        }
        mViewfinder.stop();
        mViewfinder.setOnImageReceivedListener(null);
        sendStopViewfinder();
        mSurfaceView.onPause();
        mSurfaceView.stopDrawing();
    }

    @OnClick(R.id.start_stop_recording)
    public void onMakeRecording() {
        if (mMakeVideo.isChecked()) {
            if (!mIsRecordingStarted) {
                sendStartRecording(true);
            } else {
                sendStopRecording();
            }
        } else {
            makePicture();
        }
    }

    private void makePicture() {
        sendStartRecording(false);
    }

    private void setupBackChannelNotifications() {
        mBackchannelNotificationListener = new BackchannelNotificationListener();
        mBackchannelNotificationListener.setBackchannelNotificationCallback(this);
        mBackchannelNotificationListener.setBackchannelNotificationParser(BackchannelNotificationsParser.Creator.newInstance(Camera.getCameraApiVersion()));
    }

    private void sendStartRecording(final boolean isVideoMode) {
        CameraApiCallback<Void> cameraRecordingCallback = new CameraApiCallback<Void>() {
            @Override
            public void success(Void aVoid) {

            }

            @Override
            public void error(int statusCode) {
                //TODO Handle error
            }

            @Override
            public void failure(Throwable throwable) {
                //TODO Handle failure
            }
        };
        Camera.getCameraApi().setRecordingStatus(true, cameraRecordingCallback);
    }

    private void sendStopRecording() {
        CameraApiCallback<Void> cameraRecordingCallback = new CameraApiCallback<Void>() {
            @Override
            public void success(Void aVoid) {

            }

            @Override
            public void error(int statusCode) {
                //TODO Handle error
            }

            @Override
            public void failure(Throwable throwable) {
                //TODO Handle failure
            }
        };
        Camera.getCameraApi().setRecordingStatus(false, cameraRecordingCallback);
    }

    private void setImageModeAsDefault() {
        Camera.getCameraApi().switchToImageMode(Image.Mode.SINGLE, new CameraApiCallback<Settings>() {
            @Override
            public void success(Settings settings) {
            }

            @Override
            public void error(int statusCode) {
            }

            @Override
            public void failure(Throwable throwable) {
            }
        });
    }

    @Override
    public void onNotificationReceived(BackchannelNotification notification) {
        switch (notification.getNotificationType()) {
            case PHOTO_CAPTURED:
                onPhotoCaptured((PhotoCapturedNotification) notification);
                break;
            case RECORDING_STOPPED:
                onRecordingStopped(((RecordingStoppedNotification) notification));
                break;
            case RECORDING_STARTED:
                onRecordingStarted();
                break;
            default:
                break;
        }
    }


    private void onRecordingStarted() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mIsRecordingStarted = true;
                mStartStopRecording.setText(mStopLabel);
                mMakeVideo.setEnabled(false);
                mTakePicture.setEnabled(false);
                Snackbar snackbar = Snackbar
                        .make(mCoordinatorLayout, mVideoRecordingSnackbarText, Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        });

    }

    private void onRecordingStopped(RecordingStoppedNotification recordingStoppedNotification) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mIsRecordingStarted = false;
                mStartStopRecording.setText(mRecordLabel);
                mMakeVideo.setEnabled(true);
                mTakePicture.setEnabled(true);
                Snackbar snackbar = Snackbar
                        .make(mCoordinatorLayout, mVideoCreatedSnackbarText, Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        });

    }

    private void onPhotoCaptured(PhotoCapturedNotification photoCapturedNotification) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Snackbar snackbar = Snackbar
                        .make(mCoordinatorLayout, mPhotoSnackbarText, Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mIsRecordingStarted) {
            sendStopRecording();
        } else{
            super.onBackPressed();
        }
    }
}
