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

package com.tomtom.camera.app.video;

/**
 * Example of concrete implementation of {@link AbstractVideoGLSurfaceView}
 */

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;

import com.tomtom.camera.preview.PreviewVideoSurface;
import com.tomtom.camera.util.Logger;
import com.tomtom.camera.video.BitmapDrawObject;

public class BasicVideoGLSurfaceView extends AbstractVideoGLSurfaceView implements PreviewVideoSurface{

    public BasicVideoGLSurfaceView(Context context) {
        super(context);
    }

    public BasicVideoGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Example of GL renderer creation
     * @param context {@Link Context}
     */
    @Override
    public void createGLRenderer(Context context) {
        if(mGlRenderer == null) {
            mGlRenderer = new AbstractGLImageRenderer(context) {
                @Override
                protected void drawExtra() {

                }
            };
        }
    }

    /**
     * Queues next image to be drawn
     *
     * @param image as byte array
     */
    public void queueImage(final byte[] image) {
        if (image == null){
            return;
        }
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_DISPLAY);
                try {
                    mBitmapDrawObject = new BitmapDrawObject(BitmapFactory.decodeByteArray(image, 0, image.length, mOptions));
                    queueBitmapDrawObject(null);
                    if (mOptions.inBitmap == null || mBitmapDrawObject == null) {
                        mOptions.inBitmap = mBitmapDrawObject.getBitmap();
                    }
                } catch (Exception e) {
                    mOptions.inBitmap = null;
                    Logger.exception(new Throwable("Unable to decode into existing bitmap. Skipping this one. Exception: " + e.getMessage()));
                }
            }
        };
        new Thread(runnable).start();
    }
}
