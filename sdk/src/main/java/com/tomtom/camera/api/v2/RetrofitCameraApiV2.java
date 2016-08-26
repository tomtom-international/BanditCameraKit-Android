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

import com.tomtom.camera.api.model.RecordingHighlight;

import java.util.Map;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.QueryMap;
import retrofit.mime.TypedFile;

/**
 * Retrofit interface which corresponds to camera rest API
 */
interface RetrofitCameraApiV2 {

    @GET("/2/status")
    void getCameraStatus(Callback<CameraStatusV2> cameraStatusCallback);

    @POST("/2/record")
    void setRecordingStatus(@Body RecordingStatusV2 recordingStatus, Callback<Void> captureSetCallback);

    @POST("/2/viewfinder")
    void setViewfinderStatus(@Body ViewfinderStatusV2 viewfinderStatus, Callback<Void> viewfinderSetCallback);

    @GET("/2/videos?sort=filename&order=desc")
    void getVideoFiles(@Query("count") int count, Callback<VideosV2> videoFilesCallback);

    @GET("/2/sessions/videos")
    void getVideoSessions(@Query("count") int count, @Query("offset") int offset, Callback<VideoSessionsV2> videoSessionsCallback);

    @GET("/2/videos/{video_id}/thumb")
    void getVideoThumbnail(@Path("video_id") String videoId, @Query("offset_secs") float offset, Callback<Bitmap> videoThumbnailCallback);

    @DELETE("/2/videos/{video_id}/")
    void deleteVideo(@Path("video_id") String videoId, Callback<Void> deleteVideoCallback);

    @GET("/2/settings")
    void getSettings(Callback<SettingsV2> settingsCallback);

    @GET("/2/capabilities/recording")
    void getRecordingCapabilities(Callback<RecordingCapabilitiesV2> recordingCapabilitiesCallback);

    @GET("/2/capabilities/transcoding")
    void getTranscodingCapabilities(Callback<TranscodingCapabilitiesResponse> transcodingCapabilitiesCallback);

    @GET("/2/capabilities/scenes")
    void getScenesCapabilities(Callback<SceneCapabilitiesV2> sceneCapabilitiesCallback);

    @PUT("/2/settings/camera")
    void setCameraSettings(@Body CameraSettingsV2 cameraSettings, Callback<Void> callback);

    @POST("/2/settings/video")
    void setVideoMode(@Body VideoModeV2 videoMode, Callback<Void> setVideoModesCallback);

    @POST("/2/settings/image")
    void setImageMode(@Body ImageModeV2 imageMode, Callback<Void> setImageModesCallback);

    @PUT("/2/settings/video")
    void switchToVideoMode(@Body VideoModeV2 videoModeSettings, Callback<VideoModeV2> setVideoModeCallback);

    @PUT("/2/settings/image")
    void switchToImageMode(@Body ImageModeV2 imageModeSettings, Callback<ImageModeV2> setVideoModeCallback);

    @PUT("/2/settings/scene")
    void setScene(@Body SceneV2 scene, Callback<Void> setSceneCallback);

    @GET("/2/log")
    void getLog(Callback logCallback);

    @Multipart
    @POST("/2/firmware")
    void upgradeFirmware(@Part("upgrade") TypedFile firmwareFile, Callback<Void> firmwareUpgradeCallback);

    @GET("/2/firmware")
    void getFirmware(Callback<FirmwareV2> getFirmwareCallback);

    @Multipart
    @POST("/2/quickgpsfix")
    void updateQuickgps(@Part("quickgps") TypedFile quickgpsFile, Callback<Void> quickgpsUpdateCallback);

    @Multipart
    @POST("/2/glonass")
    void updateGlonass(@Part("glonass") TypedFile glonassFile, Callback<Void> glonassUpdateCallback);

    @POST("/2/preview")
    void setPreviewStatus(@Body PreviewV2 preview, Callback<Void> previewStatusCallback);

    @GET("/2/videos/{video_id}/tags")
    void getVideoHighlights(@Path("video_id") String videoId, Callback<VideoHighlightsV2> tagsCallback);

    @POST("/2/record/tag")
    void addHighlightToRecording(@Body RecordingHighlight tag, Callback<Void> setTagToRecordingVideoCallback);

    @POST("/2/videos/{video_id}/tags")
    void setHighlightsToVideo(@Path("video_id") String videoId, @Body VideoHighlightsV2 videoHighlightsV2, Callback<VideoHighlightsV2> highlightsUuidCallback);

    @DELETE("/2/videos/{video_id}/tags")
    void deleteVideoHighlights(@Path("video_id") String videoId, Callback<Void> deleteTagsCallback);

    @GET("/2/videos/{video_id}/tags/{tag_id}")
    void getHighlight(@Path("video_id") String videoId, @Path("tag_id") String tagId, Callback<HighlightV2> getTagCallback);

    @PUT("/2/videos/{video_id}/tags/{tag_id}")
    void updateHighlight(@Path("video_id") String videoId, @Path("tag_id") String tagId, @Body HighlightV2 tag, Callback<Void> updateTagCallback);

    @DELETE("/2/videos/{video_id}/tags/{tag_id}")
    void deleteHighlight(@Path("video_id") String videoId, @Path("tag_id") String tagId, Callback<Void> deleteTagCallback);

    @GET("/2/videos/{video_id}/tags/{tag_id}/thumb")
    void getHighlightThumbnail(@Path("video_id") String videoId, @Path("tag_id") String tagId, Callback<Bitmap> tagThumbnailCallback);

    @GET("/2/videos/{video_id}/sensors")
    void getVideoActionData(@Path("video_id") String videoId, @QueryMap Map<String,String> params, Callback<SensorDataCollectionV2> actionDataCallback);

    @GET("/2/videos/{video_id}/tags/{tag_id}/sensors")
    void getHighlightActionData(@Path("video_id") String videoId, @Path("tag_id") String highlightId, Callback<SensorDataCollectionV2> highlightActionDataCallback);

    @GET("/2/license")
    void getLicenseFile(Callback licenceCallback);

    @GET("/2/images")
    void getImages(@QueryMap Map<String, String> params, Callback<ImagesV2> getImagesCallback);

    @GET("/2/sessions/images")
    void getImageSessions(@Query("count") int count, @Query("offset") int offset, Callback<ImageSessionsV2> imageSessionsCallback);

    @GET("/2/images/{image_id}")
    void getImage(@Path("image_id") String imageId, Callback<ImageV2> getImageCallback);

    @DELETE("/2/images/{image_id}")
    void deleteImage(@Path("image_id") String imageId, Callback<Void> deleteImageCallback);

    @GET("/2/images/{image_id}/contents")
    void getImageContent(@Path("image_id") String imageId, Callback<Bitmap> imageContentCallback);

    @GET("/2/videos/{video_id}/frames")
    void getPhotoFromVideo(@Path("video_id") String videoId, @Query("offset_secs") float offset, Callback<Response> imageContentCallback);

    @GET("/2/datetime")
    void getCameraTime(Callback<CameraTimeV2> getCameraTimeCallback);

    @POST("/2/datetime")
    void setCameraTime(@Body CameraTimeV2 date, Callback<Void> setCameraTimeCallback);
}
