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

package com.tomtom.camera.api.v2;

import android.graphics.Bitmap;

import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.squareup.okhttp.ConnectionPool;
import com.tomtom.camera.api.BanditOkHttpClient;
import com.tomtom.camera.api.CameraApi;
import com.tomtom.camera.api.CameraApiCallback;
import com.tomtom.camera.api.converter.CameraApiGsonConverter;
import com.tomtom.camera.api.model.CameraLicenseFile;
import com.tomtom.camera.api.model.CameraSettings;
import com.tomtom.camera.api.model.CameraStatus;
import com.tomtom.camera.api.model.CameraTime;
import com.tomtom.camera.api.model.Capabilities;
import com.tomtom.camera.api.model.Firmware;
import com.tomtom.camera.api.model.Highlight;
import com.tomtom.camera.api.model.Image;
import com.tomtom.camera.api.model.ImageMode;
import com.tomtom.camera.api.model.ImageSessions;
import com.tomtom.camera.api.model.Images;
import com.tomtom.camera.api.model.LogFile;
import com.tomtom.camera.api.model.RecordingCapabilities;
import com.tomtom.camera.api.model.RecordingHighlight;
import com.tomtom.camera.api.model.Scene;
import com.tomtom.camera.api.model.SceneCapabilities;
import com.tomtom.camera.api.model.SensorDataCollection;
import com.tomtom.camera.api.model.Settings;
import com.tomtom.camera.api.model.TranscodingCapabilities;
import com.tomtom.camera.api.model.Video;
import com.tomtom.camera.api.model.VideoHighlights;
import com.tomtom.camera.api.model.VideoMode;
import com.tomtom.camera.api.model.VideoSessions;
import com.tomtom.camera.api.model.Videos;
import com.tomtom.camera.api.model.ViewfinderStatus;
import com.tomtom.camera.api.model.capability.Framerate;
import com.tomtom.camera.api.model.capability.Resolution;
import com.tomtom.camera.util.ApiUtil;
import com.tomtom.camera.util.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

/**
 * Concrete implementation of {@link CameraApi}, interface used for Camera API interactions
 */
public class CameraApiV2 implements CameraApi {

    private static final String TAG = "CameraApiWrapperV2";

    private static final int HTTP_READ_TIMEOUT_SECONDS = 120;
    private static final int MAX_RETRY_COUNT = 5;

    RetrofitCameraApiV2 mCameraApi;

    BanditOkHttpClient mDownloadHttpClient;
    RetrofitDownloadApiV2 mDownloadApi;

    public CameraApiV2(String baseUrl) {
        RestAdapter cameraApiAdapter = new RestAdapter.Builder()
                .setClient(new OkClient(BanditOkHttpClient.getSharedInstance()))
                .setEndpoint(baseUrl)
                .setConverter(new CameraApiGsonConverter(ApiUtil.getDateHandlingGson()))
                .build();
        mCameraApi = cameraApiAdapter.create(RetrofitCameraApiV2.class);

        mDownloadHttpClient = new BanditOkHttpClient();
        mDownloadHttpClient.setConnectionPool(new ConnectionPool(BanditOkHttpClient.MAX_IDLE_HTTP_CONNECTIONS, BanditOkHttpClient.KEEP_ALIVE_TIMEOUT_MS));
        mDownloadHttpClient.setReadTimeout(HTTP_READ_TIMEOUT_SECONDS, TimeUnit.SECONDS);

        RestAdapter downloadApiAdapter = new RestAdapter.Builder()
                .setClient(new OkClient(mDownloadHttpClient))
                .setEndpoint(baseUrl)
                .setConverter(new CameraApiGsonConverter(ApiUtil.getDateHandlingGson()))
                .build();
        mDownloadApi = downloadApiAdapter.create(RetrofitDownloadApiV2.class);
    }

    @Override
    public void getCameraStatus(CameraApiCallback<CameraStatus> callback) {
        mCameraApi.getCameraStatus(new CallbackAdapter<CameraStatusV2, CameraStatus>(new CameraCallback<CameraStatus>(callback)));
    }

    @Override
    public void setRecordingStatus(boolean isRecording, CameraApiCallback<Void> callback) {
        RecordingStatusV2 recordingStatusV1 = new RecordingStatusV2(isRecording);
        mCameraApi.setRecordingStatus(recordingStatusV1, new CameraCallback<Void>(callback));
    }

    @Override
    public void setViewfinderStatus(ViewfinderStatus viewfinderStatus, CameraApiCallback<Void> callback) {
        ViewfinderStatusV2 viewfinderStatusV2;
        if (viewfinderStatus.isActive()) {
            viewfinderStatusV2 = ViewfinderStatusV2.createStart(viewfinderStatus.getStreamingPort());
        } else {
            viewfinderStatusV2 = ViewfinderStatusV2.createStop();
        }

        mCameraApi.setViewfinderStatus(viewfinderStatusV2, new CameraCallback<Void>(callback));
    }

    @Override
    public void getRecordingCapabilities(CameraApiCallback<RecordingCapabilities> callback) {
        mCameraApi.getRecordingCapabilities(new CallbackAdapter<RecordingCapabilitiesV2, RecordingCapabilities>(new CameraCallback<RecordingCapabilities>(callback)));
    }

    @Override
    public void getTranscodingCapabilities(final CameraApiCallback<TranscodingCapabilities> callback) {
        mCameraApi.getTranscodingCapabilities(new CameraCallback<TranscodingCapabilitiesResponse>(new CameraApiCallback<TranscodingCapabilitiesResponse>() {
            @Override
            public void success(TranscodingCapabilitiesResponse transcodingCapabilitiesResponse) {
                TranscodingCapabilitiesV2 transcodingCapabilitiesV2 = new TranscodingCapabilitiesV2(transcodingCapabilitiesResponse.mTranscodingCapabilities);
                callback.success(transcodingCapabilitiesV2);
            }

            @Override
            public void error(int statusCode) {
                callback.error(statusCode);
            }

            @Override
            public void failure(Throwable t) {
                callback.failure(t);
            }
        }));
    }

    @Override
    public void getSceneCapabilities(CameraApiCallback<SceneCapabilities> callback) {
        mCameraApi.getScenesCapabilities(new CameraCallback<SceneCapabilitiesV2>(callback));
    }

    @Override
    public void getCapabilities(final CameraApiCallback<Capabilities> callback) {
        mCameraApi.getRecordingCapabilities(new Callback<RecordingCapabilitiesV2>() {
            final CameraCallback<Capabilities> cameraCallback = new CameraCallback<>(callback);
            int retryCount = 0;

            @Override
            public void success(final RecordingCapabilitiesV2 recordingCapabilitiesV2, Response response) {
                Logger.debug(TAG, response.getBody().toString());
                mCameraApi.getTranscodingCapabilities(new Callback<TranscodingCapabilitiesResponse>() {
                    @Override
                    public void success(final TranscodingCapabilitiesResponse transcodingCapabilitiesResponse, Response response) {
                        Logger.debug(TAG, response.getBody().toString());
                        mCameraApi.getScenesCapabilities(new Callback<SceneCapabilitiesV2>() {
                            @Override
                            public void success(SceneCapabilitiesV2 sceneCapabilitiesV2, Response response) {
                                Logger.debug(TAG, response.getBody().toString());
                                TranscodingCapabilitiesV2 transcodingCapabilitiesV2 = new TranscodingCapabilitiesV2(transcodingCapabilitiesResponse.mTranscodingCapabilities);
                                CapabilitiesV2 capabilitiesV2 = new CapabilitiesV2(recordingCapabilitiesV2, transcodingCapabilitiesV2, sceneCapabilitiesV2);
                                cameraCallback.success(capabilitiesV2, response);
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                Logger.debug(TAG, error.getMessage());
                                if (retryCount < MAX_RETRY_COUNT) {
                                    retryCount++;
                                    mCameraApi.getScenesCapabilities(this);
                                } else {
                                    cameraCallback.failure(error);
                                }
                            }
                        });
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Logger.debug(TAG, error.getMessage());
                        if (retryCount < MAX_RETRY_COUNT) {
                            retryCount++;
                            mCameraApi.getTranscodingCapabilities(this);
                        } else {
                            cameraCallback.failure(error);
                        }
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                Logger.debug(TAG, error.getMessage());
                if (retryCount < MAX_RETRY_COUNT) {
                    retryCount++;
                    mCameraApi.getRecordingCapabilities(this);
                } else {
                    callback.failure(error);
                }
            }
        });
    }

    @Override
    public void startPreview(String videoId, float startPositionSecs, float lengthSecs, int port, CameraApiCallback<Void> callback) {
        Logger.debug(TAG, "Starting preview on " + port + " for " + videoId + " " + startPositionSecs + " " + lengthSecs);
        PreviewV2 previewV2 = new PreviewV2(true, startPositionSecs, lengthSecs, videoId, port);
        mCameraApi.setPreviewStatus(previewV2, new CameraCallback<Void>(callback));
    }

    @Override
    public void stopPreview(String videoId, int previewPort, CameraApiCallback<Void> callback) {
        Logger.debug(TAG, "Stopping preview on " + previewPort);
        PreviewV2 previewV2 = new PreviewV2(false, previewPort, videoId);
        mCameraApi.setPreviewStatus(previewV2, new CameraCallback<Void>(callback));
    }

    @Override
    public void getVideoFiles(int count, CameraApiCallback<Videos> callback) {
        mCameraApi.getVideoFiles(count, new CallbackAdapter<VideosV2, Videos>(new CameraCallback<Videos>(callback)));
    }

    @Override
    public void getVideoSessions(int count, int offset, CameraApiCallback<VideoSessions> cameraApiCallback) {
        mCameraApi.getVideoSessions(count, offset, new CallbackAdapter<VideoSessionsV2, VideoSessions>(new CameraCallback<VideoSessions>(cameraApiCallback)));
    }

    @Override
    public void getVideoThumbnail(String videoId, float offsetSeconds, CameraApiCallback<Bitmap> callback) {
        mCameraApi.getVideoThumbnail(videoId, offsetSeconds, new CameraCallback<Bitmap>(callback));
    }

    @Override
    public void getHighlightThumbnail(String videoId, String highlightId, CameraApiCallback<Bitmap> callback) {
        mCameraApi.getHighlightThumbnail(videoId, highlightId, new CameraCallback<Bitmap>(callback));
    }

    @Override
    public void deleteVideo(String videoId, CameraApiCallback<Void> callback) {
        mCameraApi.deleteVideo(videoId, new CameraCallback<Void>(callback));
    }

    @Override
    public void getVideoHighlights(String videoId, CameraApiCallback<VideoHighlights> callback) {
        mCameraApi.getVideoHighlights(videoId, new CallbackAdapter<VideoHighlightsV2, VideoHighlights>(new CameraCallback<VideoHighlights>(callback)));
    }

    @Override
    public void addHighlightsToVideo(final Video video, final List<Highlight> highlights, final CameraApiCallback<VideoHighlights> callback) {
        final ArrayList<HighlightV2> highlightsV2 = new ArrayList<>(highlights.size());
        final CameraCallback<VideoHighlights> cameraCallback = new CameraCallback<>(callback);
        for (Highlight highlight : highlights) {
            HighlightV2 highlightV2 = new HighlightV2(highlight);
            highlightsV2.add(highlightV2);
        }
        VideoHighlightsV2 videoHighlightsV2 = new VideoHighlightsV2();
        videoHighlightsV2.mItems = highlightsV2;
        Logger.debug(TAG, "Highlights to be set: " + new Gson().toJson(videoHighlightsV2));
        mCameraApi.setHighlightsToVideo(video.getVideoId(), videoHighlightsV2, new Callback<VideoHighlightsV2>() {
            @Override
            public void success(VideoHighlightsV2 videoHighlightsV2, Response response) {
                for (int i = 0; i < highlightsV2.size(); i++) {
                    String id = videoHighlightsV2.getVideoHighlights().get(i).getId();
                    highlightsV2.get(i).mId = id;
                    highlightsV2.get(i).mVideoFile = video;
                    Float delOffsecs = videoHighlightsV2.getVideoHighlights().get(i).getDeliveredOffsetSecs();
                    highlightsV2.get(i).setDeliveredOffsetSecs(delOffsecs);
                    Float delLenSecs = videoHighlightsV2.getVideoHighlights().get(i).getDeliveredLengthSecs();
                    highlightsV2.get(i).setmDeliveredLengthSecs(delLenSecs);
                }
                videoHighlightsV2.mItems = highlightsV2;
                cameraCallback.success(videoHighlightsV2, response);
            }

            @Override
            public void failure(RetrofitError error) {
                cameraCallback.failure(error);
            }
        });
    }

    @Override
    public void deleteVideoHighlights(String videoId, CameraApiCallback<Void> callback) {
        mCameraApi.deleteVideoHighlights(videoId, new CameraCallback<Void>(callback));
    }

    @Override
    public void getHighlight(String videoId, String highlightId, CameraApiCallback<Highlight> callback) {
        mCameraApi.getHighlight(videoId, highlightId,
                new CallbackAdapter<HighlightV2, Highlight>(new CameraCallback<Highlight>(callback)));
    }

    @Override
    public void updateHighlight(String videoId, String highlightId, Highlight updatedHighlight, CameraApiCallback<Void> callback) {
        HighlightV2 updatedHighlightV2 = new HighlightV2(updatedHighlight);
        mCameraApi.updateHighlight(videoId, highlightId, updatedHighlightV2, new CameraCallback<Void>(callback));
    }

    @Override
    public void deleteHighlight(String videoId, String highlightId, CameraApiCallback<Void> callback) {
        mCameraApi.deleteHighlight(videoId, highlightId, new CameraCallback<Void>(callback));
    }

    @Override
    public void addHighlightToRecording(CameraApiCallback<Void> callback) {
        RecordingHighlight recordingHighlight = new RecordingHighlight();
        mCameraApi.addHighlightToRecording(recordingHighlight, new CameraCallback<Void>(callback));
    }

    @Override
    public void getSettings(CameraApiCallback<Settings> callback) {
        mCameraApi.getSettings(new CallbackAdapter<SettingsV2, Settings>(new CameraCallback<Settings>(callback)));
    }

    @Override
    public void setCameraSettings(CameraSettings cameraSettings, CameraApiCallback<Void> callback) {
        CameraSettingsV2 cameraSettingsV2 = new CameraSettingsV2(cameraSettings);
        mCameraApi.setCameraSettings(cameraSettingsV2, new CameraCallback<Void>(callback));
    }

    @Override
    public void getLog(final CameraApiCallback<LogFile> logFileCallback) {
        mCameraApi.getLog(new Callback() {
            @Override
            public void success(Object o, final Response response) {
                int code = response.getStatus();
                boolean isSuccess = code >= 200 && code < 300;
                if(isSuccess) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String log = new String(ByteStreams.toByteArray(response.getBody().in()));
                                LogFile logFile = new LogFile(log);
                                logFileCallback.success(logFile);

                            } catch (IOException e) {
                                Logger.exception(e);
                                logFileCallback.failure(e);
                            }
                        }
                    }).start();
                }
                else {
                    logFileCallback.error(code);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                logFileCallback.failure(error.getCause());
            }
        });
    }

    @Override
    public void updateFirmware(TypedFile firmwareFile, CameraApiCallback<Void> callback) {
        mCameraApi.upgradeFirmware(firmwareFile, new CameraCallback<Void>(callback));
    }

    @Override
    public void updateQuickGps(TypedFile quickGpsFile, CameraApiCallback<Void> callback) {
        mCameraApi.updateQuickgps(quickGpsFile, new CameraCallback<Void>(callback));
    }

    @Override
    public void updateGlonass(TypedFile glonassFile, CameraApiCallback<Void> callback) {
        mCameraApi.updateGlonass(glonassFile, new CameraCallback<Void>(callback));
    }

    @Override
    public void switchToVideoMode(Video.Mode videoMode, final CameraApiCallback<Settings> callback) {
        VideoModeV2 videoModeV2 = new VideoModeV2(videoMode, null, null, null, null);
        final CameraCallback<Settings> cameraCallback = new CameraCallback<>(callback);
        mCameraApi.switchToVideoMode(videoModeV2, new Callback<VideoModeV2>() {
            @Override
            public void success(VideoModeV2 videoModeV2, Response response) {
                mCameraApi.getSettings(new CallbackAdapter<SettingsV2, Settings>(cameraCallback));
            }

            @Override
            public void failure(RetrofitError error) {
                cameraCallback.failure(error);
            }
        });
    }

    @Override
    public void switchToImageMode(Image.Mode imageMode, final CameraApiCallback<Settings> callback) {
        ImageModeV2 imageModeV2 = new ImageModeV2(imageMode);
        final CameraCallback<Settings> cameraCallback = new CameraCallback<>(callback);
        mCameraApi.switchToImageMode(imageModeV2, new Callback<ImageModeV2>() {
            @Override
            public void success(ImageModeV2 imageModeV2, Response response) {
                mCameraApi.getSettings(new CallbackAdapter<SettingsV2, Settings>(cameraCallback));
            }

            @Override
            public void failure(RetrofitError error) {
                cameraCallback.failure(error);
            }
        });
    }

    @Override
    public void setVideoMode(VideoMode videoMode, CameraApiCallback<Void> callback) {
        VideoModeV2 videoModeV2 = new VideoModeV2(videoMode.getMode(), videoMode.getResolution(), videoMode.getFramerate(), videoMode.getFieldOfView(), videoMode.getSlowMotionRate(), videoMode.getIntervalSecs());
        mCameraApi.setVideoMode(videoModeV2, new CameraCallback<Void>(callback));
    }

    @Override
    public void setImageMode(ImageMode imageMode, CameraApiCallback<Void> callback) {
        ImageModeV2 imageModeV2 = new ImageModeV2(imageMode.getMode(), imageMode.getResolution(), imageMode.getDurationSecs(), imageMode.getIntervalSecs(), imageMode.getCount());
        mCameraApi.setImageMode(imageModeV2, new CameraCallback<Void>(callback));
    }

    @Override
    public void setScene(Scene scene, CameraApiCallback<Void> callback) {
        SceneV2 sceneV2 = new SceneV2(scene);
        mCameraApi.setScene(sceneV2, new CameraCallback<Void>(callback));
    }

    @Override
    public void getFirmware(CameraApiCallback<Firmware> callback) {
        mCameraApi.getFirmware(new CallbackAdapter<FirmwareV2, Firmware>(new CameraCallback<Firmware>(callback)));
    }

    @Override
    public void getImageContent(String imageId, CameraApiCallback<Bitmap> callback) {
        mCameraApi.getImageContent(imageId, new CameraCallback<Bitmap>(callback));
    }

    @Override
    public void getImage(String imageId, CameraApiCallback<Image> callback) {
        mCameraApi.getImage(imageId, new CallbackAdapter<ImageV2, Image>(new CameraCallback<Image>(callback)));
    }

    @Override
    public void getImages(Map<String, String> params, CameraApiCallback<Images> callback) {
        mCameraApi.getImages(params, new CallbackAdapter<ImagesV2, Images>(new CameraCallback<Images>(callback)));
    }

    @Override
    public void getImageSessions(int count, int offset, CameraApiCallback<ImageSessions> imageSessionsCallback) {
        mCameraApi.getImageSessions(count, offset, new CallbackAdapter<ImageSessionsV2, ImageSessions>(new CameraCallback<ImageSessions>(imageSessionsCallback)));
    }

    @Override
    public void deleteImage(String imageId, CameraApiCallback<Void> callback) {
        mCameraApi.deleteImage(imageId, new CameraCallback<Void>(callback));
    }

    @Override
    public void getLicenseFile(final CameraApiCallback<CameraLicenseFile> cameraLicenceCallback) {
        mCameraApi.getLicenseFile(new Callback() {
            @Override
            public void success(Object o, final Response response) {
                int code = response.getStatus();
                boolean isSuccess = code >= 200 && code < 300;
                if(isSuccess) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                CameraLicenseFile cameraLicenseFile = new CameraLicenseFile(ByteStreams.toByteArray(response.getBody().in()));
                                cameraLicenceCallback.success(cameraLicenseFile);

                            } catch (IOException e) {
                                Logger.exception(e);
                                cameraLicenceCallback.failure(e);
                            }
                        }
                    }).start();
                }
                else {
                    cameraLicenceCallback.error(code);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                cameraLicenceCallback.failure(error.getCause());
            }
        });
    }

    @Override
    public void getHighlightActionData(String videoId, String highlightId, CameraApiCallback<SensorDataCollection> callback) {
        mCameraApi.getHighlightActionData(videoId, highlightId, new CallbackAdapter<SensorDataCollectionV2, SensorDataCollection>(new CameraCallback<SensorDataCollection>(callback)));
    }

    @Override
    public void getVideoActionData(String videoId, Map<String, String> filter, CameraApiCallback<SensorDataCollection> callback) {
        mCameraApi.getVideoActionData(videoId, filter,
                new CallbackAdapter<SensorDataCollectionV2, SensorDataCollection>(new CameraCallback<SensorDataCollection>(callback)));
    }

    @Override
    public void getPhotoFromVideo(String videoId, float offset, CameraApiCallback<Response> callback) {
        mCameraApi.getPhotoFromVideo(videoId, offset, new CameraCallback<Response>(callback));
    }

    public void setGpsEnabled(boolean isEnabled, CameraApiCallback<Void> callback) {
        CameraSettingsV2.Builder builder = new CameraSettingsV2.Builder().gpsEnabled(isEnabled);
        mCameraApi.setCameraSettings(builder.build(), new CameraCallback<Void>(callback));
    }

    @Override
    public void setRotationEnabled(boolean enabled, CameraApiCallback<Void> callback) {
        CameraSettingsV2.Builder builder = new CameraSettingsV2.Builder().rotationEnabled(enabled);
        mCameraApi.setCameraSettings(builder.build(), new CameraCallback<Void>(callback));
    }

    @Override
    public void setExternalMicEnabled(boolean isEnabled, CameraApiCallback<Void> externalMicCallback) {
        CameraSettingsV2.Builder builder = new CameraSettingsV2.Builder().externalMic(isEnabled);
        mCameraApi.setCameraSettings(builder.build(), new CameraCallback<Void>(externalMicCallback));
    }

    @Override
    public void getTime(CameraApiCallback<CameraTime> callback) {
        mCameraApi.getCameraTime(new CallbackAdapter<CameraTimeV2, CameraTime>(new CameraCallback<CameraTime>(callback)));
    }

    @Override
    public void setTime(Date date, CameraApiCallback<Void> callback) {
        CameraTimeV2 cameratimeV2 = new CameraTimeV2();
        cameratimeV2.setDate(date);
        mCameraApi.setCameraTime(cameratimeV2, new CameraCallback<Void>(callback));
    }

    /***
     * DOWNLOAD API implementation
     */

    @Override
    public Response downloadVideo(String videoId) {
        return mDownloadApi.downloadVideo(videoId);
    }

    @Override
    public Response downloadHighlight(String videoId, String highlightId, Resolution resolution, Framerate framerate) {
        String strResolution = resolution != null ? resolution.value() : null;
        String strFramerate = framerate != null ? framerate.value() : null;
        return mDownloadApi.downloadTag(videoId, highlightId, strResolution, strFramerate);
    }

    @Override
    public Response downloadPhoto(String photoId) {
        return mDownloadApi.downloadImageContent(photoId);
    }

    @Override
    public void cancelCurrentDownload() {
        mDownloadHttpClient.cancelCurrentDownloadRequest();
    }

    private static class CameraCallback<T> implements Callback<T>{

        CameraApiCallback<T> mCameraApiCallback;

        public CameraCallback(CameraApiCallback cameraApiCallback){
            mCameraApiCallback = cameraApiCallback;
        }

        @Override
        public void failure(RetrofitError error) {
            mCameraApiCallback.failure(error);
        }

        @Override
        public void success(T t, Response response) {
            int code = response.getStatus();
            boolean isSuccess = code >= 200 && code < 300;
            if(isSuccess) {
                mCameraApiCallback.success(t);
            }
            else{
                mCameraApiCallback.error(code);
            }
        }
    }

    /***
     * @param <I> Api model V2 implementation file which callback CameraApiV2 consumes,
     *            package-private
     * @param <O> Wrapped Api interface callback which is propagated to the caller
     */
    private static class CallbackAdapter<I extends O, O> implements Callback<I> {

        private CameraCallback<O> mOutputCallback;

        private CallbackAdapter(CameraCallback<O> outputCallback) {
            mOutputCallback = outputCallback;
        }

        @Override
        public void success(I i, Response response) {
            mOutputCallback.success(i, response);
        }

        @Override
        public void failure(RetrofitError error) {
            Logger.debug(TAG, error.getUrl() + (error.getKind() != null ? error.getKind().name() : "No kind") + " " + (error.getMessage() != null ? error.getMessage() : "No error message for "));
            mOutputCallback.failure(error);
        }
    }

}
