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

package com.tomtom.camera.api;

import android.graphics.Bitmap;

import com.tomtom.camera.api.model.CameraApiVersion;
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
import com.tomtom.camera.api.v2.CameraApiV2;
import com.tomtom.camera.util.Logger;

import java.util.Date;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

/**
 * Interface used for Camera API interactions while being version agnostic. Concrete implementations
 * take care of requests and callback compatibility in a same manour to provide response models as
 * interface implementation instead of concrete api version model.
 */
public interface CameraApi {

    // Predefined URL of web server on camera
    String BASE_URL = "http://192.168.1.101/api";

    class Factory {
        /**
         * Creates CameraApiWrapper instance based on provided {@link CameraApiVersion}.
         * If {@link CameraApiVersion} is null or not supported, it will return null.
         *
         * @param cameraApiVersion CameraApiVersion instance
         * @return {@link CameraApi} instance or null
         */
        public static CameraApi create(CameraApiVersion cameraApiVersion) {
            switch (cameraApiVersion.getVersion()) {
                case CameraApiVersion.V2:
                    return new CameraApiV2(BASE_URL);
                default:
                    Logger.exception(new Exception("Can't create CameraApiWrapper from " + cameraApiVersion.getVersion() + "." + cameraApiVersion.getRevision()));
                    return null;
            }
        }

        /**
         * Provides default CameraApiWrapper for API version 2.
         * @return {@link CameraApi} instance
         */
        public static CameraApi create() {
            return new CameraApiV2(BASE_URL);
        }
    }

    /***
     * Class which encapsulates Version Api on the camera. It's version agnostic, it doesn't need
     * any param to be built, hence it's static.
     */
    class Version {
        /**
         * Provides CameraApiVersion which can be used for creating {@link CameraApi}
         * @param cameraApiVersionCallback Callback providing CameraApiVersion
         */
        public static void getApiVersion(final CameraApiCallback<CameraApiVersion> cameraApiVersionCallback) {
            RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(CameraApi.BASE_URL).build();
            RetrofitVersionApi versionApi = restAdapter.create(RetrofitVersionApi.class);
            versionApi.getApiVersion(new Callback<CameraApiVersion>() {
                @Override
                public void success(CameraApiVersion cameraApiVersion, Response response) {
                    int code = response.getStatus();
                    boolean isSuccess = code >= 200 && code < 300;
                    if(cameraApiVersion != null && isSuccess) {
                        cameraApiVersionCallback.success(cameraApiVersion);
                    }
                    else {
                        cameraApiVersionCallback.error(code);
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    cameraApiVersionCallback.failure(error);
                }
            });
        }
    }
    /**
     * Async call for getting {@link CameraStatus}
     * @param callback Callback provoding {@link CameraStatus}
     */
    void getCameraStatus(CameraApiCallback<CameraStatus> callback);

    /**
     * Async call for starting/stopping recording
     * @param callback Callback providing result of the request
     */
    void setRecordingStatus(boolean isRecording, CameraApiCallback<Void> callback);

    /**
     * Async call for setting viewfinder status to either start on port or stop.
     * @param viewfinderStatus Use {@link com.tomtom.camera.api.model.ViewfinderStatus.Creator} for creating start or stop
     * @param callback Callback providing result of the request
     */
    void setViewfinderStatus(ViewfinderStatus viewfinderStatus, CameraApiCallback<Void> callback);

    /**
     * Joint async call for all camera Capabilities
     * @param callback Callback providing {@link Capabilities}
     */
    void getCapabilities(CameraApiCallback<Capabilities> callback);

    /**
     * Async call for getting {@link RecordingCapabilities}
     * @param callback Callback providing {@link RecordingCapabilities}
     */
    void getRecordingCapabilities(CameraApiCallback<RecordingCapabilities> callback);

    /**
     * Async call for getting {@link TranscodingCapabilities}
     * @param callback Callback providing {@link TranscodingCapabilities}
     */
    void getTranscodingCapabilities(CameraApiCallback<TranscodingCapabilities> callback);

    /**
     * Async call for getting {@link SceneCapabilities}
     * @param callback Callback providing {@link SceneCapabilities}
     */
    void getSceneCapabilities(CameraApiCallback<SceneCapabilities> callback);

    /**
     * Async start preview call. Only one preview can run at a time.
     *
     * @param videoId id of the video
     * @param startPositionSecs starting position for preview in seconds
     * @param lengthSecs Length of preview
     * @param port Server port which will be used for receiving preview
     * @param callback Callback providing result of the request
     */
    void startPreview(String videoId, float startPositionSecs, float lengthSecs, int port, CameraApiCallback<Void> callback);

    /**
     * Async stop preview call.
     *
     * @param videoId id of the video
     * @param previewPort port used for the preview
     * @param callback Callback providing result of the request
     */
    void stopPreview(String videoId, int previewPort, CameraApiCallback<Void> callback);

    /**
     * Async call for getting video files list from the camera.
     * @param count Max page/list size
     * @param callback Callback providing {@link Videos}
     */
    void getVideoFiles(int count, CameraApiCallback<Videos> callback);

    /**
     * Async call for getting {@link com.tomtom.camera.api.model.VideoSession} list from the camera.
     * @param count max page/list size
     * @param offset starting position for list
     * @param cameraApiCallback Callback providing {@link VideoSessions}
     */
    void getVideoSessions(int count, int offset, CameraApiCallback<VideoSessions> cameraApiCallback);

    /**
     * Async call for getting thumbnail Bitmap from video at given offset
     * @param videoId id of the video
     * @param offsetSeconds exact moment offset from the start of the video in seconds
     * @param callback Callback providing Bitmap
     */
    void getVideoThumbnail(String videoId, float offsetSeconds, CameraApiCallback<Bitmap> callback);

    /**
     * Async call for getting highlight thumbnail Bitmap
     * @param videoId id of the highlight's video
     * @param highlightId id of the highlight
     * @param callback Callback providing Bitmap
     */
    void getHighlightThumbnail(String videoId, String highlightId, CameraApiCallback<Bitmap> callback);

    /**
     * Async call for deleting the video from the camera storage.
     * @param videoId id of the video
     * @param callback Callback providing result of the request
     */
    void deleteVideo(String videoId, CameraApiCallback<Void> callback);

    /**
     * Async call for getting list of highlights for given video.
     * @param videoId id of the video
     * @param callback Callback providing {@link VideoHighlights}
     */
    void getVideoHighlights(String videoId, CameraApiCallback<VideoHighlights> callback);

    /**
     * Async call for adding highlights to given video.
     * @param video Video which we want to add highlights to
     * @param highlights List of highlights to be added
     * @param callback Callback providing list of added highlights including parent video in model
     */
    void addHighlightsToVideo(Video video, List<Highlight> highlights, CameraApiCallback<VideoHighlights> callback);

    /**
     * Async call for deleting all highlights from given video
     * @param videoId id of the video
     * @param callback Callback providing result of the request
     */
    void deleteVideoHighlights(String videoId, CameraApiCallback<Void> callback);

    /**
     * Async request for getting highlight model.
     * @param videoId id of the video
     * @param highlightId id of the highlight we're looking for
     * @param callback Callback providing {@link Highlight}
     */
    void getHighlight(String videoId, String highlightId, CameraApiCallback<Highlight> callback);

    /**
     * Async request for updating highlight.
     * @param videoId id of the video
     * @param highlightId id of the highlight
     * @param updatedHighlight highlight which has been updated
     * @param callback Callback providing result of the request
     */
    void updateHighlight(String videoId, String highlightId, Highlight updatedHighlight, CameraApiCallback<Void> callback);

    /**
     * Async call for deleting the highlight.
     * @param videoId id of the video
     * @param highlightId id of the highlight
     * @param callback Callback providing result of the request
     */
    void deleteHighlight(String videoId, String highlightId, CameraApiCallback<Void> callback);

    /**
     * Async call for adding highlight to ongoing recording.
     * @param callback Callback providing result of the request
     */
    void addHighlightToRecording(CameraApiCallback<Void> callback);

    /**
     * Async call for getting {@link Settings}
     * @param callback Callback providing {@link Settings}
     */
    void getSettings(CameraApiCallback<Settings> callback);

    /**
     * Async call for changing/updating {@link CameraSettings}
     * @param cameraSettings changed/updated {@link CameraSettings}
     * @param callback Callback providing result of the request
     */
    void setCameraSettings(CameraSettings cameraSettings, CameraApiCallback<Void> callback);

    /**
     * Async call for getting {@link LogFile} from the camera
     * @param callback Callback providing {@link LogFile}
     */
    void getLog(CameraApiCallback<LogFile> callback);

    /**
     * Async call for updating firmware on the camera
     * @param firmwareFile firmware file
     * @param callback Callback providing result of the request
     */
    void updateFirmware(TypedFile firmwareFile, CameraApiCallback<Void> callback);

    /**
     * Async call for updating quick gps on the camera
     * @param quickGpsFile quick gps file
     * @param callback Callback providing result of the request
     */
    void updateQuickGps(TypedFile quickGpsFile, CameraApiCallback<Void> callback);

    /**
     * Async call for updating quick glonass on the camera
     * @param glonassFile glonass file
     * @param callback Callback providing result of the request
     */
    void updateGlonass(TypedFile glonassFile, CameraApiCallback<Void> callback);

    /**
     * Async call for switching to video mode, no need to provide anything except {@link Video.Mode}.
     * @param videoMode video mode to be used
     * @param callback Callback providing new {@link Settings}
     */
    void switchToVideoMode(Video.Mode videoMode, CameraApiCallback<Settings> callback);

    /**
     * Async call for switching to image mode, no need to provide anything except {@link Image.Mode}.
     * @param imageMode image mode to be used
     * @param callback Callback providing new {@link Settings}
     */
    void switchToImageMode(Image.Mode imageMode, CameraApiCallback<Settings> callback);

    /**
     * Async call for setting {@link VideoMode}
     * @param videoMode videomode to be set
     * @param callback Callback providing result for the request
     */
    void setVideoMode(VideoMode videoMode, CameraApiCallback<Void> callback);

    /**
     * Async call for setting image mode
     * @param imageMode image mode to be set
     * @param callback Callback providing result for the request
     */
    void setImageMode(ImageMode imageMode, CameraApiCallback<Void> callback);

    /**
     * Async call for getting camera {@link Firmware}
     * @param callback Callback providing {@link Firmware}
     */
    void getFirmware(CameraApiCallback<Firmware> callback);

    /**
     * Async call for getting {@link Image} as a Bitmap
     * @param imageId id of the image
     * @param callback Callback providing Bitmap
     */
    void getImageContent(String imageId, CameraApiCallback<Bitmap> callback);

    /**
     * Async call for getting {@link Image}
     * @param imageId id of the image
     * @param callback Callback providing {@link Image}
     */
    void getImage(String imageId, CameraApiCallback<Image> callback);

    /**
     * Async call for getting {@link Images}, wrapper for list of {@link Image}
     *
     * Parameters:
     * Parameter 'offset' is 0-based so offset=0 will address the first file from the resource.
     * 'offset' and 'count' are used (based on sort criteria set by 'sort' parameter and 'order')
     * to retrieve image information for 'count' files, first starting at specified 'offset'
     * from beginning of a sorted list of files. If 'offset' is bigger than total count of files,
     * error code will be returned. However, if 'offset' falls into range but 'count' specifies
     * more items than available, this method will return as many items as possible
     * and won't return error status code.
     *
     * For the list of possible parameters, please see Camera Rest Api documentation.
     *
     * @param params query params for call.
     * @param callback Callback providing Bitmap
     */
    void getImages(Map<String, String> params, CameraApiCallback<Images> callback);

    /**
     * Async call for getting list of {@link com.tomtom.camera.api.model.ImageSession}
     * @param count max page/list size
     * @param offset starting position of the page/list
     * @param imageSessionsCallback Callback providing {@link ImageSessions}
     */
    void getImageSessions(int count, int offset, CameraApiCallback<ImageSessions> imageSessionsCallback);
    /**
     * Async call for deleting the image.
     * @param imageId id of the image
     * @param callback Callback providing result of the request
     */
    void deleteImage(String imageId, CameraApiCallback<Void> callback);

    /**
     * Async call for getting Camera License file (licenses) as {@link CameraLicenseFile}
     * @param callback Callback providing {@link CameraLicenseFile}
     */
    void getLicenseFile(CameraApiCallback<CameraLicenseFile> callback);

    /**
     * Async call for getting {@link SensorDataCollection} for given {@link Highlight}
     * @param videoId id of the video
     * @param highlightId id of the highlight
     * @param callback Callback providing {@link SensorDataCollection}
     */
    void getHighlightActionData(String videoId, String highlightId, CameraApiCallback<SensorDataCollection> callback);

    /**
     * Async call for getting {@link SensorDataCollection} for given {@link Video}
     * If 'filter' parameter is not specified, all sensor data existing in the video is returned.
     * 'Filter' is comma-separated list of sensor names whose values are requested.
     * Possible sensor name values are (at least one has to be specified if 'filter' exists in request):
     *
     * 'filter' can be one or more comma separated values (gnss, accel, gyro, magnet, temp, pressure,
     * heart_rate, cadence).
     *
     * 'offset_secs' as float offset (in seconds) from beginning of a video
     * marking the point from which sensor data is delivered.
     *
     * 'length_secs' as float length (in seconds) of the interval in video
     * which sensor data is requested.
     * @param videoId id of the video
     * @param params params for request
     * @param callback Callback providing {@link SensorDataCollection}
     */
    void getVideoActionData(String videoId, Map<String, String> params, CameraApiCallback<SensorDataCollection> callback);

    /**
     * Async call for setting Gps to enabled/disabled
     * @param isEnabled {@code true} if enabling, {@code false} if disabling
     * @param callback Callback providing result for the request
     */
    void setGpsEnabled(boolean isEnabled, CameraApiCallback<Void> callback);

    /**
     * Async call for rotating image upside down (180 degrees)
     * @param enabled {@code true} if rotated, {@code false} if default
     * @param callback Callback providing result for the request
     */
    void setRotationEnabled(boolean enabled, CameraApiCallback<Void> callback);

    /**
     * Async call for enabling external microphone
     * @param isEnabled {@code true} if external mic enabled, {@code false} internal mic enabled
     * @param gpsEnabledCallback Callback providing result for the request
     */
    void setExternalMicEnabled(boolean isEnabled, CameraApiCallback<Void> gpsEnabledCallback);

    /**
     * Async call for setting the {@link Scene}
     * @param scene {@link Scene} to be set
     * @param callback Callback providing result for the request
     */
    void setScene(Scene scene, CameraApiCallback<Void> callback);

    /**
     * Async call for getting snapshot from the video
     * @param videoId id of the video
     * @param offset offset in seconds from the start of the video
     * @param callback Callback providing Response so jpeg bytes can be retrieved
     */
    void getPhotoFromVideo(String videoId, float offset, CameraApiCallback<Response> callback);

    /**
     * Async call for getting time on the camera
     * @param callback Callback providing {@link CameraTime}
     */
    void getTime(CameraApiCallback<CameraTime> callback);

    /**
     * Async call for setting time on the camera
     * @param currentDate {@link Date} to be set on the camera
     * @param callback Callback providing request result
     */
    void setTime(Date currentDate, CameraApiCallback<Void> callback);

    // DownloadAPI

    /**
     * Sync call for downloading {@link Video} file. {@link Response} should be consumed in a
     * separate thread.
     * @param videoId id of the video
     * @return {@link Response} for video
     */
    Response downloadVideo(String videoId);

    /**
     * Sync call for downloading {@link Highlight} file. {@link Response} should be consumed in a
     * separate thread.
     * @param videoId id of the video
     * @param highlightId id of the highlight
     * @param resolution {@link Resolution} of the output file
     * @param framerate {@link Framerate} of the output file
     * @return {@link Response} for highlight
     */
    Response downloadHighlight(String videoId, String highlightId, Resolution resolution, Framerate framerate);

    /**
     * Sync call for downloading {@link Image} file. {@link Response} should be consumed in a
     * separate thread.
     * @param photoId id of the photo
     * @return {@link Response} for image
     */
    Response downloadPhoto(String photoId);

    /**
     * Cancels current download.
     */
    void cancelCurrentDownload();
}
