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

package com.tomtom.camera.api.converter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.gson.Gson;
import com.tomtom.camera.util.Logger;

import java.io.IOException;
import java.lang.reflect.Type;

import retrofit.converter.ConversionException;
import retrofit.converter.GsonConverter;
import retrofit.mime.TypedInput;

/**
 * Provides direct decoding of byte[]/InputStream into Bitmap in order to have cleaner Retrofit Api
 * interface.
 */
public class CameraApiGsonConverter extends GsonConverter {

    public static final String TAG = "CameraApiConverter";

    public CameraApiGsonConverter(Gson gson) {
        super(gson);
    }

    @Override
    public Object fromBody(final TypedInput typedInput, Type type) throws ConversionException {

        if (type == Bitmap.class) {
            try {
                return BitmapFactory.decodeStream(typedInput.in());
            } catch (IOException e) {
                Logger.exception(e);
                return null;
            }
        }
        else {
            return super.fromBody(typedInput, type);
        }
    }
}
