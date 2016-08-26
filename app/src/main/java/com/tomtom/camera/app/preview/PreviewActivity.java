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

package com.tomtom.camera.app.preview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.tomtom.camera.api.CameraApiCallback;
import com.tomtom.camera.api.model.Video;
import com.tomtom.camera.app.Camera;
import com.tomtom.camera.app.R;
import com.tomtom.camera.app.video.BasicVideoGLSurfaceView;
import com.tomtom.camera.preview.OnPreviewVideoListener;
import com.tomtom.camera.preview.PreviewVideo;
import com.tomtom.camera.util.ApiUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Example of running preview of the video. Video is sent via Intent's bundle as JSON string.
 * We're using {@link BasicVideoGLSurfaceView} as {@link com.tomtom.camera.preview.PreviewVideoSurface}
 * implementation and {@link PreviewVideo} as main component.
 *
 * Before running preview, we need to construct PreviewVideo providing {@link com.tomtom.camera.preview.AbstractPreviewVideo.PreviewApiClient}
 * implementation, prepare preview for given video and attach {@link com.tomtom.camera.preview.PreviewVideoSurface} to it.
 *
 * After it's all done, preview is started calling {@link PreviewVideo#startPreview()}.
 *
 * In {@link com.tomtom.camera.preview.AbstractPreviewVideo.PreviewApiClient} start we need to call {@link PreviewVideo#onCameraPreviewStarted(boolean)} or on stop {@link PreviewVideo#onCameraPreviewStopped(boolean)}
 * based on Camera response (success/fail/error).
 *
 * Preview is stopped calling {@link PreviewVideo#stopPreview()}.
 */
public class PreviewActivity extends AppCompatActivity {

    public static final String EXTRA_VIDEO_JSON = "video";

    @BindView(R.id.gl_video_surface_view)
    BasicVideoGLSurfaceView mGlVideoSurfaceView;

    @BindView(R.id.start_preview)
    Button mStartPreview;

    @BindView(R.id.stop_preview)
    Button mStopPreview;

    Video mVideo;
    PreviewVideo mPreviewVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        ButterKnife.bind(this);
        setupSurfaceView();
        String videoJson = getIntent().getStringExtra(EXTRA_VIDEO_JSON);
        mVideo = ApiUtil.getDateHandlingGson().fromJson(videoJson, Video.class);
        mPreviewVideo = new PreviewVideo(new SamplePreviewApiClient(), null, null);

    }

    private void setupSurfaceView() {
        mGlVideoSurfaceView.setKeepScreenOn(true);
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        android.view.ViewGroup.LayoutParams lp = mGlVideoSurfaceView.getLayoutParams();
        lp.height = (int) (screenWidth * 9f / 16f);
        mGlVideoSurfaceView.setLayoutParams(lp);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPreviewVideo.setOnPreviewVideoListener(new OnPreviewVideoListener() {
            @Override
            public void onTotalLengthSet(int totalLengthMillis) {

            }

            @Override
            public void onPreviewTimeProgress(int currentMillis) {

            }

            @Override
            public void onPreviewStarted(int startMillis) {

            }


            @Override
            public void onEndReceived() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mStopPreview.setEnabled(false);
                        mStartPreview.setEnabled(true);
                    }
                });
            }
        });
        mPreviewVideo.preparePreview(mVideo);
        mPreviewVideo.setVideoSurface(mGlVideoSurfaceView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPreviewVideo.stopPreview();
        mGlVideoSurfaceView.onPause();
        mPreviewVideo.setVideoSurface(null);
    }

    @OnClick(R.id.start_preview)
    public void startPreview() {
        mStopPreview.setEnabled(true);
        mStartPreview.setEnabled(false);
        mPreviewVideo.startPreview();
    }

    @OnClick(R.id.stop_preview)
    public void stopPreview() {
        mStopPreview.setEnabled(false);
        mStartPreview.setEnabled(true);
        mPreviewVideo.stopPreview();
    }

    private class SamplePreviewApiClient implements PreviewVideo.PreviewApiClient {

        @Override
        public void executePreviewCommand(PreviewVideo.PreviewCommand previewCommand) {
            switch (previewCommand.command) {
                case PreviewVideo.PreviewCommand.START:
                    Camera.getCameraApi().startPreview(previewCommand.videoId, previewCommand.startPositionSecs, previewCommand.lengthSecs, previewCommand.previewVideoPort, new CameraApiCallback<Void>() {
                        @Override
                        public void success(Void aVoid) {
                            mGlVideoSurfaceView.onResume();
                            mPreviewVideo.onCameraPreviewStarted(true);
                        }

                        @Override
                        public void error(int statusCode) {
                            mPreviewVideo.onCameraPreviewStarted(false);
                        }

                        @Override
                        public void failure(Throwable t) {
                            mPreviewVideo.onCameraPreviewStarted(false);
                        }
                    });
                    break;
                case PreviewVideo.PreviewCommand.STOP:
                    Camera.getCameraApi().stopPreview(previewCommand.videoId, previewCommand.previewVideoPort, new CameraApiCallback<Void>() {
                        @Override
                        public void success(Void aVoid) {
                            mPreviewVideo.onCameraPreviewStopped(true);
                        }

                        @Override
                        public void error(int statusCode) {
                            mPreviewVideo.onCameraPreviewStopped(false);
                        }

                        @Override
                        public void failure(Throwable t) {
                            mPreviewVideo.onCameraPreviewStopped(false);
                        }
                    });
                    break;
            }
        }
    }
}
