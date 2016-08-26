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
 * Class that wraps OpenGL functions for displaying video images on surface. Contains some code
 * from Android platform cts (EglConfigGLSurfaceView) which is under Apache 2.0 license.
 */

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import com.tomtom.camera.util.Logger;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class GLImage {

    private static final String TAG = "GLImage";

    private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
    private static final int TRIANGLE_VERTICES_DATA_UV_OFFSET = 3;
    protected static final int FLOAT_SIZE_BYTES = 4;
    protected static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 5 * FLOAT_SIZE_BYTES;

    private static final String VERTEX_SHADER_OVERLAY =
            "uniform mat4 uMVPMatrixBase;\n" +
                    "uniform mat4 uSTMatrix;\n" +
                    "attribute vec4 aPosition;\n" +
                    "attribute vec4 aTextureCoord;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "void main() {\n" +
                    "  gl_Position = uMVPMatrixBase * aPosition;\n" +
                    "  vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n" +
                    "}\n";
    private static final String FRAGMENT_SHADER_OVERLAY =
            "precision mediump float;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "uniform sampler2D overlayTexture;\n" +
                    "void main() {\n" +
                    " gl_FragColor = texture2D(overlayTexture, vTextureCoord);\n" +
                    "}\n";

    private FloatBuffer mVertexBuffer;	// buffer holding the mVertices
    private float mVertices[] = {
            -1.f, -1.f, 0, 0.f, 1.f, //bottom left
            1.f, -1.f, 0, 1.f, 1.f, //bottom right
            -1.f,  1.0f, 0, 0.f, 0.f,//top left
            1.f,  1.0f, 0, 1.f, 0.f,//top right
    };

    private float[] mMVPMatrix = new float[16];
    private float[] mSTMatrix = new float[16];

    /** The mTexture pointer */
    private int[] mTextures = new int[1];

    private int mProgram;
    private int maPostionHandle;
    private int maTextureHandle;
    private int muMVPMatrixHandle;
    private int muSTMatrixHandle;

    private int mStartX = 0;
    private int mlenX = 0;
    private int mStartY = 0;
    private int mLenY = 0;


    public GLImage() {
        initBuffer();
    }

    private void initBuffer() {
        // a float has 4 bytes so we allocate for each coordinate 4 bytes
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(mVertices.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        mVertexBuffer = byteBuffer.asFloatBuffer();
        mVertexBuffer.put(mVertices).position(0);
    }

    /**
     * Creates openGL program using vertex and fragment shader, generates and binds textures
     */
    public void setup() {

        mProgram = createProgram(VERTEX_SHADER_OVERLAY, FRAGMENT_SHADER_OVERLAY);
        if (mProgram == 0) {
            throw new RuntimeException("failed creating program");
        }
        maPostionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        if (maPostionHandle == -1) {
            throw new RuntimeException("Could not get attrib location for aPosition");
        }
        maTextureHandle = GLES20.glGetAttribLocation(mProgram, "aTextureCoord");
        if (maTextureHandle == -1) {
            throw new RuntimeException("Could not get attrib location for aTextureCoord");
        }
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrixBase");
        if (muMVPMatrixHandle == -1) {
            throw new RuntimeException("Could not get attrib location for uMVPMatrixBase");
        }
        muSTMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uSTMatrix");
        if (muSTMatrixHandle == -1) {
            throw new RuntimeException("Could not get attrib location for uSTMatrix");
        }
        // generate one mTexture pointer
        GLES20.glGenTextures(1, mTextures, 0);
        // ...and bind it to our array
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures[0]);

        // create nearest filtered mTexture
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

    }

    /**
     * Sets start (x,y) surface coordinates and surface width and height.
     * This is the surface that will hold images.
     * @param startX x coordinate of the surface
     * @param startY y coordinate of the surface
     * @param lenX surface width
     * @param lenY surface height
     */
    public void setViewBoundaries(int startX, int lenX, int startY, int lenY){
        mStartX = startX;
        mStartY = startY;
        mlenX = lenX;
        mLenY = lenY;
    }

    /** Draws bitmap on GL surface using GL program with generated textures
     * @param bitmap {@link Bitmap} to be drawn
     * */
    public void draw(Bitmap bitmap) {
        if (bitmap == null){
            return;
        }

        if (mlenX != 0 && mLenY != 0){
            GLES20.glViewport(mStartX, mStartY, mlenX, mLenY);
        }

        GLES20.glUseProgram(mProgram);
        checkGlError("glUseProgram");
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures[0]);

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        // Point to our buffers
        mVertexBuffer.position(TRIANGLE_VERTICES_DATA_POS_OFFSET);
        GLES20.glVertexAttribPointer(maPostionHandle, 3, GLES20.GL_FLOAT, false,
                TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mVertexBuffer);
        checkGlError("glVertexAttribPointer maPosition");

        GLES20.glEnableVertexAttribArray(maPostionHandle);
        checkGlError("glEnableVertexAttribArray maPositionHandle");
        mVertexBuffer.position(TRIANGLE_VERTICES_DATA_UV_OFFSET);
        GLES20.glVertexAttribPointer(maTextureHandle, 2, GLES20.GL_FLOAT, false,
                TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mVertexBuffer);
        GLES20.glEnableVertexAttribArray(maTextureHandle);

        Matrix.setIdentityM(mMVPMatrix, 0);
        Matrix.setIdentityM(mSTMatrix, 0);

        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(muSTMatrixHandle, 1, false, mSTMatrix, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }

    private int createProgram(String vertexSource, String fragmentSource) {
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0) {
            return 0;
        }
        int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (pixelShader == 0) {
            return 0;
        }
        int program = GLES20.glCreateProgram();
        checkGlError("glCreateProgram");
        if (program == 0) {
            Logger.error(TAG, "Could not create program");
        }
        GLES20.glAttachShader(program, vertexShader);
        checkGlError("glAttachShader");
        GLES20.glAttachShader(program, pixelShader);
        checkGlError("glAttachShader");
        GLES20.glLinkProgram(program);
        int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] != GLES20.GL_TRUE) {
            Logger.error(TAG, "Could not link program: ");
            Logger.error(TAG, GLES20.glGetProgramInfoLog(program));
            GLES20.glDeleteProgram(program);
            program = 0;
        }
        return program;
    }

    private int loadShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        checkGlError("glCreateShader type=" + shaderType);
        GLES20.glShaderSource(shader, source);
        GLES20.glCompileShader(shader);
        int[] compiled = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            Logger.error(TAG, "Could not compile shader " + shaderType + ": " + GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader);
            shader = 0;
        }
        return shader;
    }

    private void checkGlError(String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Logger.error(TAG, op + ": glError " + error);
            throw new RuntimeException(op + ": glError " + error);
        }
    }
}

