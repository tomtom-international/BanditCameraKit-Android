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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.tomtom.camera.util.Logger;
import com.tomtom.camera.video.BitmapDrawObject;
import com.tomtom.camera.video.VideoSurface;

import java.util.Vector;

/**
 * Abstract class that implements logic for displaying images on surface view.
 */

public abstract class AbstractVideoGLSurfaceView extends GLSurfaceView implements VideoSurface {

    private static final int OVERLOADED_BUFFER_SIZE = 3;
    private static final String TAG = "ViewFinderSurfaceView";

    protected BitmapFactory.Options mOptions;
    protected BitmapDrawObject mBitmapDrawObject;
    protected AbstractGLImageRenderer mGlRenderer;

    private Vector<BitmapDrawObject> mBitmapQueue;
    private boolean mStoppedDrawing;
    private boolean mSurfaceStopped; //added this to track when we explicitly stopped ViewFinder (so that we can avoid race condition happening with resizeViewfinderToFitBitmap)
    private boolean mOverflow;
    private int mBitmapWidth = 0;
    private int mBitmapHeight = 0;
    private
    @Nullable
    OnViewfinderChangedListener mOnViewfinderChangedListener;
    private byte[] mDecodeTempStorage = new byte[16 * 1024];
    private boolean mIsZoomed = true;

    private AbstractGLImageRenderer.ImageDrawnListener mImageDrawnListener = new AbstractGLImageRenderer.ImageDrawnListener() {
        @Override
        public void onImageDrawn() {
            drawImage();
        }
    };

    protected AbstractVideoGLSurfaceView(Context context) {
        super(context);
        init(context);
    }

    protected AbstractVideoGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mOverflow = false;
        setEGLContextClientVersion(2);
        createGLRenderer(context);
        mGlRenderer.setImageDrawnListener(mImageDrawnListener);
        setRenderer(mGlRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mBitmapQueue = new Vector<>(3);
        mOptions = newOptions();
    }

    /**
     * concrete implementations of {@link AbstractVideoGLSurfaceView} must override this method and
     * use it for defining {@Link AbstractGLImageRenderer}
     *
     * @param context {@Link Context}
     */
    protected abstract void createGLRenderer(Context context);

    @Override
    public void startDrawing() {
        if (isActivated())
            mStoppedDrawing = false;
        mSurfaceStopped = false;
        drawImage();
    }

    /**
     * Draws next image on surface
     */
    public void drawImage() {
        if (hasImages()) {
            try {
                mGlRenderer.setBitmapDrawObject(getNextBitmapDrawObject());
            } catch (IndexOutOfBoundsException e) {
                Logger.info(TAG, "stopped drawing, no images - " + e.getLocalizedMessage());
                mStoppedDrawing = true;
                return;
            }
            removeImage();
            requestRender();
        } else {
            mStoppedDrawing = true;
        }
    }

    /**
     * Pauses surface view drawing
     */
    public void stop() {
        mSurfaceStopped = true;
    }

    /**
     * Stops drawing and resets all flags and buffers
     */
    public void stopDrawing() {
        mBitmapQueue.clear();
        mBitmapDrawObject = null;
        mBitmapHeight = 0;
        mBitmapWidth = 0;
        mStoppedDrawing = true;
        mSurfaceStopped = true;
        mGlRenderer.setBitmapDrawObject(null);
        requestRender();
    }


    /**
     * Queues next Bitmap to be drawn
     *
     * @param bitmapDrawObject
     */
    public void queueBitmapDrawObject(BitmapDrawObject bitmapDrawObject) {
        if (bitmapDrawObject != null) {
            mBitmapDrawObject = bitmapDrawObject;
        }
        if (mBitmapQueue.size() > OVERLOADED_BUFFER_SIZE) {
            Logger.error(TAG, "overflow queue, removing image");
            mOverflow = true;
            removeImage();
        }

        if (!mSurfaceStopped) {
            resizeViewfinderToFitBitmap();
        }

        mBitmapQueue.add(mBitmapDrawObject);
        if (mStoppedDrawing) {
            startDrawing();
        }
    }

    private void resizeViewfinderToFitBitmap() {
        post(new Runnable() {
            @Override
            public void run() {
                if (mBitmapDrawObject != null
                        && mBitmapDrawObject.getBitmap() != null
                        && mBitmapDrawObject.getBitmap().getWidth() != mBitmapWidth
                        && mBitmapDrawObject.getBitmap().getHeight() != mBitmapHeight) {
                    mBitmapWidth = mBitmapDrawObject.getBitmap().getWidth();
                    mBitmapHeight = mBitmapDrawObject.getBitmap().getHeight();
                    requestLayout();
                    invalidate();

                    if (mOnViewfinderChangedListener != null) {
                        mOnViewfinderChangedListener.onViewfinderResize((float) mBitmapDrawObject.getBitmap().getWidth() / (float) mBitmapDrawObject.getBitmap().getHeight());
                    }
                }
            }
        });
    }

    /**
     * Returns next Bitmap object to be drawn
     *
     * @return boolean
     */
    public BitmapDrawObject getNextBitmapDrawObject() {
        return mBitmapQueue.get(0);
    }

    /**
     * true if queue contains images
     *
     * @return boolean
     */
    public boolean hasImages() {
        return mBitmapQueue.size() > 0;
    }

    private void removeImage() {
        try {
            mBitmapQueue.remove(0);
        } catch (IndexOutOfBoundsException e) {
            Logger.error(TAG, "Nothing to remove from image queue");
        }
    }

    /**
     * true if queue overflows OVERLOADED_BUFFER_SIZE
     *
     * @return boolean
     */
    public boolean isOverflow() {
        return mOverflow;
    }

    /**
     * Returns image renderer
     *
     * @return {@link AbstractGLImageRenderer}
     */
    public AbstractGLImageRenderer getGlRenderer() {
        return mGlRenderer;
    }

    private BitmapFactory.Options newOptions() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        options.inMutable = true;
        options.inTempStorage = mDecodeTempStorage;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return options;
    }

    /**
     * Sets {@link OnViewfinderChangedListener} implementation, which will be called when surface
     * size iz changed
     *
     * @return {@link AbstractGLImageRenderer}
     */
    public void setOnViewFinderChangedListener(@Nullable OnViewfinderChangedListener onViewfinderChangedListener) {
        mOnViewfinderChangedListener = onViewfinderChangedListener;
    }

    @Override
    public void setTag(Object tag) {
        if (tag instanceof Boolean) {
            mIsZoomed = (boolean) tag;
        } else {
            super.setTag(tag);
        }
    }

    /**
     * Interface which defines callback, which will be called when surface size iz changed
     */

    public interface OnViewfinderChangedListener {
        void onViewfinderResize(float newAspectRatio);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
        mGlRenderer.setViewSize(width, height, mIsZoomed);
    }

}
