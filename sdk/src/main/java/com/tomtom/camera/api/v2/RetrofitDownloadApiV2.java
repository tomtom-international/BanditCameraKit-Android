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

import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Retrofit interface which corresponds to camera rest API for download content
 */
interface RetrofitDownloadApiV2 {

    @GET ("/2/videos/{video_id}/tags/{tag_id}/contents")
    @retrofit.http.Streaming
    Response downloadTag(@Path("video_id") String videoId, @Path("tag_id") String tagId, @Query("resolution") String resolution, @Query("framerate") String framerate);

    @GET("/2/images/{image_id}/contents")
    @retrofit.http.Streaming
    Response downloadImageContent(@Path("image_id") String imageId);

    @GET("/2/videos/{video_id}/contents")
    @retrofit.http.Streaming
    Response downloadVideo(@Path("video_id") String videoId);
}
