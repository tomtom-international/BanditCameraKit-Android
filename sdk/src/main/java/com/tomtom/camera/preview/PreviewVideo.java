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

import android.graphics.Bitmap;

import com.tomtom.camera.api.model.Thumbnail;
import com.tomtom.camera.util.Logger;
import com.tomtom.camera.video.BitmapDrawObject;

/**
 * Preview implementation.
 */
public class PreviewVideo extends AbstractPreviewVideo<PreviewVideoSurface> {

    private static final String TAG = "PreviewVideo";

    public PreviewVideo(PreviewApiClient previewApiClient, PreviewVideoSurface surfaceView, OnPreviewVideoListener onPreviewVideoListener) {
        super(previewApiClient, surfaceView, onPreviewVideoListener);
    }

    @Override
    protected CameraPreviewStreamServer createPreviewStreamServer(PreviewBuffer previewBuffer) {
        return new CameraPreviewStreamServer(previewBuffer);
    }

    @Override
    protected void processFrameData(byte[] data, int pts, boolean isFirstFrame) {
        if(mVideoSurface != null) {
            mVideoSurface.queueImage(data);
        }
        else {
            Logger.warning(TAG, "Trying to queue image on null surface.");
        }
    }

    public void showImage(Thumbnail thumbnail) {
        if (mVideoSurface != null) {
            mVideoSurface.queueBitmapDrawObject(new BitmapDrawObject(thumbnail.getThumbBitmap()));
            mVideoSurface.startDrawing();
        }
    }

    public void showBitmap(Bitmap bitmap) {
        if (mVideoSurface != null) {
            mVideoSurface.queueBitmapDrawObject(new BitmapDrawObject(bitmap));
            mVideoSurface.startDrawing();
        }
    }
}
