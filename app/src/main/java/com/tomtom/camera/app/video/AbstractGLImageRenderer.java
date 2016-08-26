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
 * Abstract class that implements GLSurfaceView Renderer. Concrete implementations must override
 * drawExtra method. If drawExtra method is left empty, this class will draw only video images
 * on the surface. Any other content that should be drawn over the image, should be defined in draw
 * extra.
 */

import android.content.Context;
import android.content.res.Configuration;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.tomtom.camera.video.BitmapDrawObject;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public abstract class AbstractGLImageRenderer<T extends BitmapDrawObject> implements GLSurfaceView.Renderer {

    private static final String TAG = "GLImageRenderer";

    protected GLImage mGlImage = null;
    protected T mBitmapDrawObject;
    protected ImageDrawnListener mImageDrawnListener;

    private int mScreenWidth;
    private int mScreenHeight;
    private boolean mIsPortrait;

    private int mStartXGLImage = 0;
    private int mLenXGLImage = 0;
    private int mStartYGLImage = 0;
    private int mLenYGLImage = 0;

    public AbstractGLImageRenderer(Context context) {
        mGlImage = new GLImage();
        mScreenWidth = context.getResources().getDisplayMetrics().widthPixels;
        mScreenHeight = context.getResources().getDisplayMetrics().heightPixels;
        mIsPortrait = isPortrait(context);
        mBitmapDrawObject = null;
    }

    private boolean isPortrait(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        if (mBitmapDrawObject != null) {
            mGlImage.draw(mBitmapDrawObject.getBitmap());
            drawExtra();
        }
        mImageDrawnListener.onImageDrawn();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mGlImage = new GLImage();
        mGlImage.setup();
        mGlImage.setViewBoundaries(mStartXGLImage, mLenXGLImage, mStartYGLImage, mLenYGLImage);
    }

    /**
     * sets {@link ImageDrawnListener}, which callback function onImageDrawn() is called when the
     * image is drawn on the screen\ {@link ImageDrawnListener}
     * @param imageDrawnListener
     */
    public void setImageDrawnListener(ImageDrawnListener imageDrawnListener) {
        mImageDrawnListener = imageDrawnListener;
    }

    protected abstract void drawExtra();

    /**
     * Sets bitmapDrawObject, object that contains bitmap to be drawn on the surface.Here any object
     * that extends BitmapDrawObject can be used, usually containing relevant information that will
     * be used in drawExtramethod
     * @param bitmapDrawObject
     */
    public void setBitmapDrawObject(T bitmapDrawObject) {
        mBitmapDrawObject = bitmapDrawObject;
    }

    /**
     * Sets size of surface which will be drawn
     * @param width width of the surface
     * @param height of the surface
     * @param isZoomed used when screen is not 16 : 9. If true, image will be cut off, but will
     *                 occupy entire screen
     */
    public void setViewSize(int width, int height, boolean isZoomed) {
        mStartXGLImage = 0;
        mLenXGLImage = width;
        mStartYGLImage = 0;
        mLenYGLImage = height;
        if (width < mScreenWidth) {
            mStartXGLImage = (mScreenWidth - width) / 2;
        }
        if (!isZoomed && !mIsPortrait && height < mScreenHeight) {
            mStartYGLImage = (mScreenHeight - height) / 2;
        }
        mGlImage.setViewBoundaries(mStartXGLImage, mLenXGLImage, mStartYGLImage, mLenYGLImage);
    }

    /**
     * Interface which callback function onImageDrawn() is called when the
     * image is drawn on the screen\
     */
    public interface ImageDrawnListener {
        void onImageDrawn();
    }

}

